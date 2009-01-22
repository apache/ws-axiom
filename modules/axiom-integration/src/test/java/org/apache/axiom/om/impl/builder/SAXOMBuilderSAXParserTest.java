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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLIdentical;
import static org.custommonkey.xmlunit.XMLUnit.compareXML;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.apache.axiom.om.OMElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@RunWith(Parameterized.class)
public class SAXOMBuilderSAXParserTest {
    private final SAXParserFactory factory;
    
    @Parameters
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                { org.apache.crimson.jaxp.SAXParserFactoryImpl.class },
                { org.apache.xerces.jaxp.SAXParserFactoryImpl.class }
        });
    }
    
    public SAXOMBuilderSAXParserTest(Class<? extends SAXParserFactory> factoryClass) throws Exception {
        this.factory = factoryClass.newInstance();
    }

    private InputSource toInputSource(OMElement element) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        element.serialize(baos);
        return new InputSource(new ByteArrayInputStream(baos.toByteArray()));
    }
    
    @Test
    public void test() throws Exception {
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        SAXOMBuilder builder = new SAXOMBuilder();
        reader.setContentHandler(builder);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
        InputStream in = SAXOMBuilderSAXParserTest.class.getResourceAsStream("test.xml");
        try {
            reader.parse(new InputSource(in));
        } finally {
            in.close();
        }
        in = SAXOMBuilderSAXParserTest.class.getResourceAsStream("test.xml");
        try {
            assertXMLIdentical(compareXML(new InputSource(in), toInputSource(builder.getRootElement())), true);
        } finally {
            in.close();
        }
    }
}
