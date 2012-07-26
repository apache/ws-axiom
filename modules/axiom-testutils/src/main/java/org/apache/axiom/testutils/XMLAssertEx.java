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
package org.apache.axiom.testutils;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class XMLAssertEx {
    private XMLAssertEx() {}
    
    private static Document parse(InputSource is, boolean expandEntityReferences) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(expandEntityReferences);
        Document document = factory.newDocumentBuilder().parse(is);
        // XMLUnit doesn't support entity references; replace them by elements so that we can still
        // compare documents
        replaceEntityReferences(document);
        return document;
    }
    
    private static void replaceEntityReferences(Node node) {
        NodeList children = node.getChildNodes();
        for (int i=0, l=children.getLength(); i<l; i++) {
            Node child = children.item(i);
            if (child instanceof EntityReference) {
                Element replacement = node.getOwnerDocument().createElementNS(null, "entity-reference");
                replacement.setAttributeNS(null, "name", child.getNodeName());
                node.replaceChild(replacement, child);
            } else {
                replaceEntityReferences(child);
            }
        }
    }
    
    /**
     * Asserts that the two documents are identical.
     * 
     * @param control
     *            the control (expected) document
     * @param test
     *            the test (actual) document; this document is parsed any references to external
     *            entities are resolved in the same way as for the control document
     * @param entityReferencesExpanded
     *            indicates whether in the test document, entity references have been expanded
     * @throws Exception
     */
    public static void assertXMLIdentical(URL control, InputStream test, boolean entityReferencesExpanded) throws Exception {
        InputSource controlInputSource = new InputSource(control.toString());
        InputSource testInputSource = new InputSource(test);
        testInputSource.setSystemId(new URL(control, "dummy.xml").toString());
        assertXMLIdentical(controlInputSource, testInputSource, entityReferencesExpanded);
    }

    public static void assertXMLIdentical(InputSource control, InputSource test, boolean entityReferencesExpanded) throws Exception {
        XMLAssert.assertXMLIdentical(XMLUnit.compareXML(
                parse(control, entityReferencesExpanded),
                parse(test, false)), true);
    }
}
