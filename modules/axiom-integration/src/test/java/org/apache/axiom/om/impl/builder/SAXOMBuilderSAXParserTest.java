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

package org.apache.axiom.om.impl.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestSuite;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXOMBuilderSAXParserTest extends AbstractTestCase {
    private final SAXParserFactory factory;
    private final ConformanceTestFile file;
    
    public SAXOMBuilderSAXParserTest(String name, SAXParserFactory factory, ConformanceTestFile file) {
        super(name);
        this.factory = factory;
        this.file = file;
    }

    @Override
    protected void runTest() throws Throwable {
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        SAXOMBuilder builder = new SAXOMBuilder();
        reader.setContentHandler(builder);
        reader.setDTDHandler(builder);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
        reader.setProperty("http://xml.org/sax/properties/declaration-handler", builder);
        InputStream in = file.getAsStream();
        try {
            reader.parse(new InputSource(in));
        } finally {
            in.close();
        }
        in = file.getAsStream();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            builder.getDocument().serialize(baos);
            XMLUnit.setIgnoreAttributeOrder(true);
            assertXMLIdentical(compareXML(
                    new InputSource(in),
                    new InputSource(new ByteArrayInputStream(baos.toByteArray()))), true);
        } finally {
            in.close();
        }
    }
    
    private static void addTests(TestSuite suite, SAXParserFactory factory, String name) throws Exception {
        for (ConformanceTestFile file : ConformanceTestFile.getConformanceTestFiles()) {
            suite.addTest(new SAXOMBuilderSAXParserTest(
                    file.getShortName() + " - " + name, factory, file));
        }
    }
    
    public static TestSuite suite() throws Exception {
        TestSuite suite = new TestSuite();
        addTests(suite, new org.apache.crimson.jaxp.SAXParserFactoryImpl(), "crimson");
        addTests(suite, new org.apache.xerces.jaxp.SAXParserFactoryImpl(), "xerces");
        return suite;
    }
}
