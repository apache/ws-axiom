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
package org.apache.axiom.om.impl.dom;

import junit.framework.TestCase;
import org.apache.axiom.om.impl.dom.factory.OMDOMFactory;
import org.apache.axiom.om.impl.dom.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentImplTest extends TestCase {

    public DocumentImplTest() {
        super();
    }

    public DocumentImplTest(String name) {
        super(name);
    }

    public void testCreateElement() {
        String tagName = "LocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        OMDOMFactory fac = new OMDOMFactory();
        DocumentImpl doc = new DocumentImpl(fac);
        Element elem = doc.createElement(tagName);

        assertEquals("Local name misnatch", tagName, elem.getNodeName());

        elem = doc.createElementNS(namespace, "axis2:" + tagName);
        assertEquals("Local name misnatch", tagName, elem.getLocalName());
        assertEquals("Namespace misnatch", namespace, elem.getNamespaceURI());

    }

    public void testCreateAttribute() {
        String attrName = "attrIdentifier";
        String attrValue = "attrValue";
        String attrNs = "http://ws.apache.org/axis2/ns";
        String attrNsPrefix = "axis2";

        OMDOMFactory fac = new OMDOMFactory();
        DocumentImpl doc = new DocumentImpl(fac);
        Attr attr = doc.createAttribute(attrName);

        assertEquals("Attr name mismatch", attrName, attr.getName());
        assertNull("Namespace value should be null", attr.getNamespaceURI());


        attr = doc.createAttributeNS(attrNs, attrNsPrefix + ":" + attrName);
        assertEquals("Attr name mismatch", attrName, attr.getLocalName());
        assertNotNull("Namespace value should not be null", attr.getNamespaceURI());
        assertEquals("NamsspaceURI mismatch", attrNs, attr.getNamespaceURI());
        assertEquals("namespace prefix mismatch", attrNsPrefix, attr.getPrefix());

        attr.setValue(attrValue);

    }

    public void testCreateText() {
        String textValue = "temp text value";

        OMDOMFactory fac = new OMDOMFactory();
        DocumentImpl doc = new DocumentImpl(fac);
        Text txt = doc.createTextNode(textValue);

        assertEquals("Text value mismatch", textValue, txt.getData());
    }

    public void testDocumentSiblings() {
        try {
            DocumentBuilderFactoryImpl.setDOOMRequired(true);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element elem = doc.createElement("test");
            doc.appendChild(elem);

            Node node = doc.getNextSibling();
            assertNull("Document's next sibling has to be null", node);
            Node node2 = doc.getPreviousSibling();
            assertNull("Document's previous sibling has to be null", node2);
            Node node3 = doc.getParentNode();
            assertNull("Document's parent has to be null", node3);
            DocumentBuilderFactoryImpl.setDOOMRequired(false);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
