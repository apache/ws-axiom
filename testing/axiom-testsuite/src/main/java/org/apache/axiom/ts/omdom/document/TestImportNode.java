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

package org.apache.axiom.ts.omdom.document;

import java.util.Iterator;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

public class TestImportNode extends AxiomTestCase {
    public TestImportNode(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(
                AbstractTestCase.getTestResource("sigEncr.xml"));
        Document doc2 = ((DOMMetaFactory)metaFactory).newDocumentBuilderFactory().newDocumentBuilder().newDocument();
        Node n = doc2.importNode(doc.getDocumentElement(), true);
        compare(doc.getDocumentElement(), (OMElement) n);
    }

    private static void compare(Element ele, OMElement omele) throws Exception {
        if (ele == null && omele == null) {
            return;
        } else if (ele != null && omele != null) {
            assertEquals("Element name not correct",
                                  ele.getLocalName(),
                                  omele.getLocalName());
            if (omele.getNamespace() != null) {
                assertEquals("Namespace URI not correct",
                             ele.getNamespaceURI(),
                             omele.getNamespace().getNamespaceURI());

            }

            //go through the attributes
            NamedNodeMap map = ele.getAttributes();
            Iterator attIterator = omele.getAllAttributes();
            OMAttribute omattribute;
            while (attIterator.hasNext()) {
                omattribute = (OMAttribute) attIterator.next();
                Node node = map.getNamedItemNS(
                        omattribute.getNamespaceURI(),
                        omattribute.getLocalName());
                if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                    Attr attr = (Attr) node;
                    assertEquals(attr.getValue(),
                                 omattribute.getAttributeValue());
                } else {
                    throw new OMException("return type is not a Attribute");
                }

            }
            Iterator it = omele.getChildren();
            NodeList list = ele.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    assertTrue(it.hasNext());
                    OMNode tempOmNode = (OMNode) it.next();
                    while (tempOmNode.getType() != OMNode.ELEMENT_NODE) {
                        assertTrue(it.hasNext());
                        tempOmNode = (OMNode) it.next();
                    }
                    compare((Element) node, (OMElement) tempOmNode);
                }
            }


        } else {
            throw new Exception("One is null");
        }
    }
}
