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

package org.apache.axiom.om.impl.dom.jaxp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TransformerTest extends XMLTestCase {
    private void testStylesheet(TransformerFactory tf) throws Exception {
        DocumentBuilderFactory dbf = new DOOMDocumentBuilderFactory();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document input = builder.parse(TransformerTest.class.getResourceAsStream("input.xml"));
        Document stylesheet
                = builder.parse(TransformerTest.class.getResourceAsStream("stylesheet.xslt"));
        Document expected = builder.parse(TransformerTest.class.getResourceAsStream("output.xml"));
        Document actual = builder.newDocument();
        Transformer transformer = tf.newTransformer(new DOMSource(stylesheet));
        transformer.transform(new DOMSource(input), new DOMResult(actual));
        XMLUnit.setIgnoreWhitespace(true);
        assertXMLEqual(expected, actual);
    }

    public void testStylesheetWithXalan() throws Exception {
        testStylesheet(new org.apache.xalan.processor.TransformerFactoryImpl());
    }
    
    public void testStyleSheetWithSaxon() throws Exception {
        testStylesheet(new net.sf.saxon.TransformerFactoryImpl());
    }
    
    // This test failed with Saxon 8.9 because NodeImpl#compareDocumentPosition
    // threw an UnsupportedOperationException instead of a DOMException.
    private void testIdentity(TransformerFactory tf) throws Exception {
        DocumentBuilderFactory dbf = new DOOMDocumentBuilderFactory();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        
        Document document = builder.newDocument();
        Element root = document.createElement("root");
        Element element = document.createElementNS("urn:mynamespace", "element1");
        element.setAttribute("att", "testValue");
        element.appendChild(document.createTextNode("test"));
        root.appendChild(element);
        document.appendChild(root);
        
        Document stylesheet
                = builder.parse(TransformerTest.class.getResourceAsStream("identity.xslt"));
        Document output = builder.newDocument();
        Transformer transformer = tf.newTransformer(new DOMSource(stylesheet));
        transformer.transform(new DOMSource(document), new DOMResult(output));
        XMLUnit.setIgnoreWhitespace(false);
        assertXMLEqual(document, output);
    }

    public void testIdentityWithXalan() throws Exception {
        testIdentity(new org.apache.xalan.processor.TransformerFactoryImpl());
    }
    
    public void testIdentityWithSaxon() throws Exception {
        testIdentity(new net.sf.saxon.TransformerFactoryImpl());
    }
}
