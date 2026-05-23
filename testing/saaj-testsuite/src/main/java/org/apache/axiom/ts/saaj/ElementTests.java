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
package org.apache.axiom.ts.saaj;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPPart;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.apache.axiom.testutils.suite.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementTests {
    @Inject
    private SOAPFactory soapFactory;

    /** Tests the behavior of {@link SOAPElement#addChildElement(String)}. */
    @Test
    public void addChildElementLocalName() throws Throwable {
        SOAPElement root = soapFactory.createElement("root", "p", "urn:test");
        SOAPElement element = root.addChildElement("child");
        assertThat(element.getLocalName()).isEqualTo("child");
        assertThat(element.getNamespaceURI()).isNull();
        assertThat(element.getPrefix()).isNull();
        assertThat(element.getParentNode()).isSameAs(root);
        assertThat(element.getAttributes().getLength()).isEqualTo(0);
    }

    /** Tests the behavior of {@link SOAPElement#addChildElement(String, String, String)}. */
    @Test
    public void addChildElementLocalNamePrefixAndURI() throws Throwable {
        SOAPElement root = soapFactory.createElement("root", "ns1", "urn:ns1");
        SOAPElement element = root.addChildElement("child", "ns2", "urn:ns2");
        assertThat(element.getLocalName()).isEqualTo("child");
        assertThat(element.getNamespaceURI()).isEqualTo("urn:ns2");
        assertThat(element.getPrefix()).isEqualTo("ns2");
        assertThat(element.getParentNode()).isSameAs(root);
        NamedNodeMap attributes = element.getAttributes();
        assertThat(attributes.getLength()).isEqualTo(1);
        Attr attr = (Attr) attributes.item(0);
        assertThat(attr.getNamespaceURI()).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        assertThat(attr.getPrefix()).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE);
        assertThat(attr.getLocalName()).isEqualTo("ns2");
    }

    /**
     * Tests the behavior of the {@link Node#getOwnerDocument()} method when invoked on a {@link
     * SOAPElement} as well as the properties of the returned document.
     */
    @Test
    public void getOwnerDocument() throws Throwable {
        Document doc = soapFactory.createElement(new QName("test")).getOwnerDocument();
        assertThat(doc).isNotInstanceOf(SOAPPart.class);
        assertThat(doc.createElementNS(null, "test")).isInstanceOf(SOAPElement.class);
    }

    @Test
    public void setParentElement() throws Throwable {
        SOAPElement parent = soapFactory.createElement(new QName("parent"));
        SOAPElement child1 = parent.addChildElement(new QName("child1"));
        SOAPElement child2 = (SOAPElement) parent.getOwnerDocument().createElementNS(null, "child2");
        child2.setParentElement(parent);
        NodeList children = parent.getChildNodes();
        assertThat(children.getLength()).isEqualTo(2);
        assertThat(children.item(0)).isSameAs(child1);
        assertThat(children.item(1)).isSameAs(child2);
    }
}
