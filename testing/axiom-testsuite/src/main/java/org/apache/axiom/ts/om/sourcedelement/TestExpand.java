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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;

/** Make sure the expanded OMSourcedElement behaves like a normal OMElement. */
public class TestExpand extends AxiomTestCase {
    public TestExpand(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMSourcedElement element =
                TestDocument.DOCUMENT1.createOMSourcedElement(
                        metaFactory.getOMFactory(), false, true);
        element.getAllDeclaredNamespaces();
        assertEquals(
                "Expanded namespace count error",
                1,
                countItems(element.getAllDeclaredNamespaces()));
        assertEquals("Expanded attribute count error", 1, countItems(element.getAllAttributes()));
        assertEquals(
                "Expanded attribute value error",
                "1",
                element.getAttributeValue(new QName("books")));
        OMElement child = element.getFirstElement();
        assertEquals("Child element name", "type", child.getLocalName());
        assertEquals(
                "Child element namespace",
                "http://www.sosnoski.com/uwjws/library",
                child.getNamespace().getNamespaceURI());
        OMNode next = child.getNextOMSibling();
        assertTrue("Expected child element", next instanceof OMElement);
        next = next.getNextOMSibling();
        assertTrue("Expected child element", next instanceof OMElement);
        child = (OMElement) next;
        assertEquals("Child element name", "book", child.getLocalName());
        assertEquals(
                "Child element namespace",
                "http://www.sosnoski.com/uwjws/library",
                child.getNamespace().getNamespaceURI());
        assertEquals("Attribute value error", "xml", child.getAttributeValue(new QName("type")));
    }

    private int countItems(Iterator iter) {
        int count = 0;
        while (iter.hasNext()) {
            count++;
            iter.next();
        }
        return count;
    }
}
