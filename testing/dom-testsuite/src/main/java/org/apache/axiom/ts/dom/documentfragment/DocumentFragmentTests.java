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
package org.apache.axiom.ts.dom.documentfragment;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axiom.testutils.suite.Test;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Tests for {@link DocumentFragment}. */
public class DocumentFragmentTests {
    @Inject
    private DocumentBuilderFactory dbf;

    /** Tests {@link Node#cloneNode(boolean)} with {@code deep} set to {@code true}. */
    @Test
    public void cloneNodeDeep() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        fragment.appendChild(document.createComment("comment"));
        fragment.appendChild(document.createElementNS(null, "test"));
        DocumentFragment clone = (DocumentFragment) fragment.cloneNode(true);
        assertThat(clone.getOwnerDocument()).isSameAs(document);
        Node child = clone.getFirstChild();
        assertThat(child).isNotNull();
        assertThat(child.getNodeType()).isEqualTo(Node.COMMENT_NODE);
        child = child.getNextSibling();
        assertThat(child).isNotNull();
        assertThat(child.getNodeType()).isEqualTo(Node.ELEMENT_NODE);
        assertThat(child.getLocalName()).isEqualTo("test");
        child = child.getNextSibling();
        assertThat(child).isNull();
    }

    /** Tests {@link Node#cloneNode(boolean)} with {@code deep} set to {@code false}. */
    @Test
    public void cloneNodeShallow() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        fragment.appendChild(document.createElementNS(null, "test"));
        DocumentFragment clone = (DocumentFragment) fragment.cloneNode(false);
        assertThat(clone.getOwnerDocument()).isSameAs(document);
        assertThat(clone.getFirstChild()).isNull();
        assertThat(clone.getLastChild()).isNull();
        assertThat(clone.getChildNodes().getLength()).isEqualTo(0);
    }

    /**
     * Tests that a call to {@link Node#lookupNamespaceURI(String)} on a {@link DocumentFragment}
     * always returns {@code null} (in contrast to {@link Document}), even if one of its children
     * has a matching namespace declaration.
     */
    @Test
    public void lookupNamespaceURI() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        Element element = document.createElementNS("urn:test", "ns:root");
        element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:ns", "urn:test");
        fragment.appendChild(element);
        assertThat(fragment.lookupNamespaceURI("ns")).isNull();
    }

    /**
     * Tests that a call to {@link Node#lookupPrefix(String)} on a {@link DocumentFragment} always
     * returns {@code null} (in contrast to {@link Document}), even if one of its children has a
     * matching namespace declaration.
     */
    @Test
    public void lookupPrefix() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        Element element = document.createElementNS("urn:test", "ns:root");
        element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:ns", "urn:test");
        fragment.appendChild(element);
        assertThat(fragment.lookupPrefix("urn:test")).isNull();
    }
}
