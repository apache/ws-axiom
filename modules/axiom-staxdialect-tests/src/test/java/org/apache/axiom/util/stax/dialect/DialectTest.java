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

import java.util.Properties;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;

import junit.framework.TestSuite;

public class DialectTest extends TestSuite {
    private final ClassLoader classLoader;
    private final Properties props;
    private StAXDialect dialect;
    
    public DialectTest(ClassLoader classLoader, String name, Properties props) {
        super(name);
        this.classLoader = classLoader;
        this.props = props;
        addDialectTest(new IsCharactersOnCDATASectionTestCase());
    }
    
    private void addDialectTest(DialectTestCase testCase) {
        testCase.init(this);
        addTest(testCase);
    }
    
    XMLInputFactory newXMLInputFactory() {
        String className = props == null ? null : props.getProperty(XMLInputFactory.class.getName());
        if (className == null) {
            ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                if (factory.getClass().getClassLoader() != classLoader) {
                    throw new FactoryConfigurationError("Wrong factory!");
                }
                return factory;
            } finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        } else {
            try {
                return (XMLInputFactory)classLoader.loadClass(className).newInstance();
            } catch (Exception ex) {
                throw new FactoryConfigurationError(ex);
            }
        }
    }
    
    XMLInputFactory newNormalizedXMLInputFactory() {
        XMLInputFactory factory = newXMLInputFactory();
        if (dialect == null) {
            dialect = StAXDialectDetector.getDialect(factory.getClass());
        }
        return dialect.normalize(factory);
    }
}
