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
package org.apache.axiom.ts.dom.attr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axiom.testutils.suite.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class AttrTests {
    @Inject
    private DocumentBuilderFactory dbf;

    @Test
    public void getChildNodes() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS(null, "name");
        attr.setValue("value");
        NodeList children = attr.getChildNodes();
        assertThat(children.getLength()).isEqualTo(1);
        Node child = children.item(0);
        assertThat(child).isInstanceOf(Text.class);
        assertThat(((Text) child).getData()).isEqualTo("value");
    }

    @Test
    public void getFirstChild() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS(null, "name");
        attr.setValue("value");
        Node child = attr.getFirstChild();
        assertThat(child).isNotNull();
        assertThat(child).isInstanceOf(Text.class);
        assertThat(((Text) child).getData()).isEqualTo("value");
    }

    @Test
    public void getNamespaceURIWithNoNamespace() throws Throwable {
        Document doc = dbf.newDocumentBuilder().newDocument();
        Attr attr = doc.createAttributeNS(null, "test");
        assertThat(attr.getNamespaceURI()).isNull();
        attr = doc.createAttributeNS("", "test");
        assertThat(attr.getNamespaceURI()).isNull();
    }

    @Test
    public void getValueWithMultipleChildren() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS(null, "attr");
        attr.appendChild(document.createTextNode("A"));
        attr.appendChild(document.createTextNode("B"));
        attr.appendChild(document.createTextNode("C"));
        assertThat(attr.getValue()).isEqualTo("ABC");
    }

    /**
     * Tests the behavior of {@link Node#lookupNamespaceURI(String)} on an attribute node that has no
     * owner element.
     */
    @Test
    public void lookupNamespaceURIWithoutOwnerElement() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS("urn:test", "p:attr");
        assertThat(attr.lookupNamespaceURI("p")).isNull();
    }

    /**
     * Tests the behavior of {@link Node#setPrefix(String)} on an {@link Attr} if the specified prefix
     * is not null (and not an empty string) and the attribute has a namespace.
     */
    @Test
    public void setPrefixNotNullWithNamespace() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS("urn:ns", "p:test");
        attr.setPrefix("q");
        assertThat(attr.getPrefix()).isEqualTo("q");
        assertThat(attr.getName()).isEqualTo("q:test");
    }

    /**
     * Tests that {@link Node#setPrefix(String)} throws an exception if an attempt is made to set a
     * prefix on an {@link Attr} that has no namespace.
     */
    @Test
    public void setPrefixNotNullWithoutNamespace() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS(null, "test");
        assertThatThrownBy(() -> attr.setPrefix("p"))
                .isInstanceOfSatisfying(
                        DOMException.class, ex -> assertThat(ex.code).isEqualTo(DOMException.NAMESPACE_ERR));
    }

    /**
     * Tests the behavior of {@link Node#setPrefix(String)} when used to remove the prefix on an {@link
     * Attr} that has a namespace. Although this results in an attribute that is invalid with respect to
     * namespaces, no exception is thrown.
     */
    @Test
    public void setPrefixNullWithNamespace() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS("urn:test", "p:test");
        attr.setPrefix(null);
        assertThat(attr.getPrefix()).isNull();
    }
}
