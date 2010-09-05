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
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestSuite;

public class DialectTest extends TestSuite {
    private final ClassLoader classLoader;
    private final Properties props;
    private StAXDialect dialect;
    
    public DialectTest(ClassLoader classLoader, String name, Properties props) {
        super(name);
        this.classLoader = classLoader;
        this.props = props;
        addDialectTest(new CreateXMLEventWriterWithNullEncodingTestCase());
        addDialectTest(new CreateXMLStreamReaderThreadSafetyTestCase());
        addDialectTest(new CreateXMLStreamWriterThreadSafetyTestCase());
        addDialectTest(new CreateXMLStreamWriterWithNullEncodingTestCase());
        addDialectTest(new DisallowDoctypeDeclWithDenialOfServiceTestCase());
        addDialectTest(new DisallowDoctypeDeclWithExternalSubsetTestCase());
        addDialectTest(new DisallowDoctypeDeclWithInternalSubsetTestCase());
        addDialectTest(new GetCharacterEncodingSchemeTestCase());
        addDialectTest(new GetEncodingExternalTestCase());
        addDialectTest(new GetEncodingFromDetectionTestCase("UTF-8", "UTF-8"));
        addDialectTest(new GetEncodingFromDetectionTestCase("UnicodeBig", "UTF-16BE"));
        addDialectTest(new GetEncodingFromDetectionTestCase("UnicodeLittle", "UTF-16LE"));
        addDialectTest(new GetEncodingFromDetectionTestCase("UnicodeBigUnmarked", "UTF-16BE"));
        addDialectTest(new GetEncodingFromDetectionTestCase("UnicodeLittleUnmarked", "UTF-16LE"));
        addDialectTest(new GetEncodingTestCase());
        addDialectTest(new GetEncodingWithCharacterStreamTestCase());
        addDialectTest(new GetVersionTestCase());
        addDialectTest(new IsCharactersOnCDATASectionTestCase());
        addDialectTest(new IsStandaloneTestCase());
        addDialectTest(new MaskedNamespaceTestCase());
        addDialectTest(new NextAfterEndDocumentTestCase());
        addDialectTest(new StandaloneSetTestCase());
        addDialectTest(new WriteStartDocumentWithNullEncodingTestCase());
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
                if (classLoader != ClassLoader.getSystemClassLoader()
                        && factory.getClass().getClassLoader() != classLoader) {
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

    XMLOutputFactory newXMLOutputFactory() {
        String className = props == null ? null : props.getProperty(XMLOutputFactory.class.getName());
        if (className == null) {
            ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            try {
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                if (classLoader != ClassLoader.getSystemClassLoader()
                        && factory.getClass().getClassLoader() != classLoader) {
                    throw new FactoryConfigurationError("Wrong factory!");
                }
                return factory;
            } finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        } else {
            try {
                return (XMLOutputFactory)classLoader.loadClass(className).newInstance();
            } catch (Exception ex) {
                throw new FactoryConfigurationError(ex);
            }
        }
    }
    
    XMLOutputFactory newNormalizedXMLOutputFactory() {
        XMLOutputFactory factory = newXMLOutputFactory();
        if (dialect == null) {
            dialect = StAXDialectDetector.getDialect(factory.getClass());
        }
        return dialect.normalize(factory);
    }
    
    StAXDialect getDialect() {
        return dialect;
    }
}
