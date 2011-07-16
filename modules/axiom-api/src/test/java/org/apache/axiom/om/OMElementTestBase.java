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

package org.apache.axiom.om;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.om.util.AXIOMUtil;

public abstract class OMElementTestBase extends AbstractTestCase {
    protected final OMMetaFactory omMetaFactory;

    public OMElementTestBase(OMMetaFactory omMetaFactory) {
        this.omMetaFactory = omMetaFactory;
    }

    public void testSetText() {
        OMFactory factory = omMetaFactory.getOMFactory();
        String localName = "TestLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        OMElement elem = factory.createOMElement(localName, namespace, prefix);

        String text = "The quick brown fox jumps over the lazy dog";

        elem.setText(text);

        assertEquals("Text value mismatch", text, elem.getText());
    }

    public void testCDATA() throws Exception {
        OMFactory factory = omMetaFactory.getOMFactory();
        OMElement omElement = factory.createOMElement("TestElement", null);
        final String text = "this is <some> text in a CDATA";
        factory.createOMText(omElement, text, XMLStreamConstants.CDATA);
        assertEquals(text, omElement.getText());

        // OK, CDATA on its own worked - now confirm that a plain text + a CDATA works
        omElement = factory.createOMElement("element2", null);
        final String normalText = "regular text and ";
        factory.createOMText(omElement, normalText);
        factory.createOMText(omElement, text, XMLStreamConstants.CDATA);
        assertEquals(normalText + text, omElement.getText());
    }
    
    public void testAddChild() {
        OMFactory factory = omMetaFactory.getOMFactory();
        String localName = "TestLocalName";
        String childLocalName = "TestChildLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";

        OMElement elem = factory.createOMElement(localName, namespace, prefix);
        OMElement childElem = factory.createOMElement(childLocalName, namespace, prefix);

        elem.addChild(childElem);

        Iterator it = elem.getChildrenWithName(new QName(namespace, childLocalName));

        int count = 0;
        while (it.hasNext()) {
            OMElement child = (OMElement) it.next();
            assertEquals("Child local name mismatch", childLocalName, child.getLocalName());
            assertEquals("Child namespace mismatch", namespace,
                         child.getNamespace().getNamespaceURI());
            count ++;
        }
        assertEquals("In correct number of children", 1, count);
    }
    
    public void testFindNamespaceByPrefix() throws Exception {
        OMElement root =
                AXIOMUtil.stringToOM(omMetaFactory.getOMFactory(), "<a:root xmlns:a='urn:a'><child/></a:root>");
        OMNamespace ns = root.getFirstElement().findNamespace(null, "a");
        assertNotNull(ns);
        assertEquals("urn:a", ns.getNamespaceURI());
        root.close(false);
    }
}
