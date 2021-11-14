/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public final class StAXImplementation {
    private final String name;
    private final ClassLoader classLoader;
    private final Properties props;
    private StAXDialect dialect;
    
    public StAXImplementation(String name, ClassLoader classLoader, Properties props) {
        this.name = name;
        this.classLoader = classLoader;
        this.props = props;
    }
    
    public String getName() {
        return name;
    }

    private <T> T newFactory(Class<T> type) {
        String className = props == null ? null : props.getProperty(type.getName());
        if (className == null) {
            // We do the lookup ourselves instead of using the StAX API. This allows
            // us to completely ignore the system properties. The Axiom build allows
            // to use system properties to specify the StAX implementation to be used
            // by unit tests, but this must not interfere with the dialect tests.
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        classLoader.getResourceAsStream("META-INF/services/" + type.getName()), "UTF-8"));
                try {
                    className = in.readLine();
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new FactoryConfigurationError(ex);
            }
        }
        T factory;
        try {
            factory = classLoader.loadClass(className).asSubclass(type).getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new FactoryConfigurationError(ex);
        }
        // Check that the parser has actually been loaded from the expected class loader.
        // If the parser has been loaded from the JRE, then comparing the class loaders
        // is not reliable (because it may be null). Hence the check on ParentLastURLClassLoader.
        if (classLoader instanceof ParentLastURLClassLoader
                && factory.getClass().getClassLoader() != classLoader) {
            throw new FactoryConfigurationError("Wrong factory: got " + factory.getClass().getName()
                    + " loaded from " + factory.getClass().getClassLoader());
        }
        return factory;
    }
    
    public XMLInputFactory newXMLInputFactory() {
        return newFactory(XMLInputFactory.class);
    }
    
    public XMLInputFactory newNormalizedXMLInputFactory() {
        XMLInputFactory factory = newXMLInputFactory();
        if (dialect == null) {
            dialect = StAXDialectDetector.getDialect(factory.getClass());
        }
        return dialect.normalize(factory);
    }

    public XMLOutputFactory newXMLOutputFactory() {
        return newFactory(XMLOutputFactory.class);
    }
    
    @SuppressWarnings("deprecation")
    public XMLOutputFactory newNormalizedXMLOutputFactory() {
        XMLOutputFactory factory = newXMLOutputFactory();
        if (dialect == null) {
            dialect = StAXDialectDetector.getDialect(factory.getClass());
        }
        return dialect.normalize(factory);
    }
    
    public StAXDialect getDialect() {
        return dialect;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
