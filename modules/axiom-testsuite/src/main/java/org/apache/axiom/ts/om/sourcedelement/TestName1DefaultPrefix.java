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
package org.apache.axiom.ts.om.sourcedelement;

import java.io.StringWriter;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the OMSourcedElement localName, namespace and prefix settings before and after
 * serialization Document: testDocument (which uses the default namespace) Type of
 * Serialization: Serialize and cache Prefix test
 */
public class TestName1DefaultPrefix extends AxiomTestCase {
    public TestName1DefaultPrefix(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory f = metaFactory.getOMFactory();

        // Create OMSE with an unknown prefix
        OMNamespace rootNS = f.createOMNamespace("http://sampleroot", "rootPrefix");
        OMNamespace ns =
                f.createOMNamespace("http://www.sosnoski.com/uwjws/library", null);
        OMElement element =
                f.createOMElement(new TestDataSource(TestDocument.DOCUMENT1.getContent()), "library", ns);
        OMElement root = f.createOMElement("root", rootNS);
        root.addChild(element);

        // Test getting the local name and namespace URI. This should used not result in expansion
        assertTrue(element.getLocalName().equals("library"));
        assertTrue(element.getNamespace().getNamespaceURI().equals(
                "http://www.sosnoski.com/uwjws/library"));

        // Serialize and cache.  This should cause expansion.  The prefix should be updated to match the testDocument string
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = StAXUtils.createXMLStreamWriter(writer);
        root.serialize(writer);
        xmlwriter.flush();
        String result = writer.toString();

        assertTrue(element.getLocalName().equals("library"));
        assertTrue(element.getNamespace().getNamespaceURI().equals(
                "http://www.sosnoski.com/uwjws/library"));
        assertTrue(element.getNamespace().getPrefix().equals(""));
        assertTrue(element.getDefaultNamespace() != null);
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);

        // Serialize again
        writer = new StringWriter();
        xmlwriter = StAXUtils.createXMLStreamWriter(writer);
        root.serialize(writer);
        xmlwriter.flush();
        result = writer.toString();

        assertTrue(element.getLocalName().equals("library"));
        assertTrue(element.getNamespace().getNamespaceURI().equals(
                "http://www.sosnoski.com/uwjws/library"));
        assertTrue(element.getNamespace().getPrefix().equals(""));
        assertTrue(element.getDefaultNamespace() != null);
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
    }
}
