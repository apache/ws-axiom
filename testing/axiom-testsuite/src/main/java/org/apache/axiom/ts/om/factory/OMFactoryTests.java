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
package org.apache.axiom.ts.om.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMText;
import org.apache.axiom.testutils.suite.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.EntityReference;

public class OMFactoryTests {
    @Inject
    private OMFactory factory;

    @Inject
    private OMMetaFactory metaFactory;

    /**
     * Tests that {@link OMFactory#createOMAttribute(String, OMNamespace, String)} generates a prefix if
     * an {@link OMNamespace} object with a null prefix and a non empty namespace URI is given.
     */
    @Test
    public void createOMAttributeGeneratedPrefix() throws Throwable {
        OMAttribute attr = factory.createOMAttribute("attr", factory.createOMNamespace("urn:ns", null), "value");
        OMNamespace ns = attr.getNamespace();
        assertThat(ns.getNamespaceURI()).isEqualTo("urn:ns");
        assertThat(ns.getPrefix()).isNotNull();
        assertThat(ns.getPrefix()).isNotEmpty();
    }

    /**
     * Tests that the {@link OMAttribute} instances created by {@link
     * OMFactory#createOMAttribute(String, OMNamespace, String)} only implement the expected interfaces.
     * An {@link OMAttribute} is neither an {@link OMNode} nor an {@link OMContainer}. For the latter
     * this is in contrast to DOM where an {@link Attr} node is a parent node (containing {@link org.w3c.dom.Text}
     * and {@link EntityReference} nodes).
     */
    @Test
    public void createOMAttributeInterfaces() throws Throwable {
        OMNamespace ns = factory.createOMNamespace("urn:test", "p");
        OMAttribute attr = factory.createOMAttribute("attr", ns, "value");
        assertThat(attr).isNotInstanceOf(OMSerializable.class);
    }

    /**
     * Tests the behavior of {@link OMFactory#createOMAttribute(String, OMNamespace, String)} if an
     * {@link OMNamespace} object with a null prefix and an empty namespace URI is given. Since it is
     * not allowed to bind a prefix to the empty namespace URI and an unprefixed attribute has no
     * namespace, this should give the same result as specifying an empty prefix.
     */
    @Test
    public void createOMAttributeNullPrefixNoNamespace() throws Throwable {
        OMNamespace ns = factory.createOMNamespace("", null);
        OMAttribute attr = factory.createOMAttribute("attr", ns, "value");
        assertThat(attr.getNamespace()).isNull();
    }

    /**
     * Tests that {@link OMFactory#createOMAttribute(String, OMNamespace, String)} throws an exception
     * if the specified namespace is invalid, i.e. if the {@link OMNamespace} object specifies a prefix
     * for an empty namespace.
     */
    @Test
    public void createOMAttributeWithInvalidNamespace1() throws Throwable {
        OMNamespace ns = factory.createOMNamespace("", "p");
        assertThatThrownBy(() -> factory.createOMAttribute("attr", ns, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create a prefixed attribute with an empty namespace name");
    }

    /**
     * Tests that {@link OMFactory#createOMAttribute(String, OMNamespace, String)} throws an exception
     * if the specified namespace is invalid, i.e. if the {@link OMNamespace} object specifies a
     * non-empty namespace but no prefix.
     */
    @Test
    public void createOMAttributeWithInvalidNamespace2() throws Throwable {
        OMNamespace ns = factory.createOMNamespace("urn:test", "");
        assertThatThrownBy(() -> factory.createOMAttribute("attr", ns, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create an unprefixed attribute with a namespace");
    }

    @Test
    public void createOMCommentWithoutParent() throws Throwable {
        OMComment comment = factory.createOMComment(null, "my comment");
        assertThat(comment.getParent()).isNull();
        assertThat(comment.getValue()).isEqualTo("my comment");
    }

    @Test
    public void createOMDocTypeWithoutParent() throws Throwable {
        OMDocType dtd = factory.createOMDocType(null, "root", "publicId", "systemId", "internalSubset");
        assertThat(dtd.getParent()).isNull();
        assertThat(dtd.getRootName()).isEqualTo("root");
        assertThat(dtd.getPublicId()).isEqualTo("publicId");
        assertThat(dtd.getSystemId()).isEqualTo("systemId");
        assertThat(dtd.getInternalSubset()).isEqualTo("internalSubset");
    }

    @Test
    public void createOMDocument() throws Throwable {
        OMDocument document = factory.createOMDocument();
        assertThat(document).isNotNull();
        assertThat(document.getFirstOMChild()).isNull();

        // OMDocument doesn't extend OMNode. Therefore, the OMDocument implementation
        // should not implement OMNode either. This is a regression test for AXIOM-385.
        assertThat(document).isNotInstanceOf(OMNode.class);
    }

    /**
     * Tests the behavior of {@link OMFactory#createOMElement(OMDataSource, String, OMNamespace)} if the
     * data source is <code>null</code>.
     */
    @Test
    public void createOMElementWithNullOMDataSource1() throws Throwable {
        assertThatThrownBy(() -> factory.createOMElement(null, "test", factory.createOMNamespace("urn:test", "p")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests the behavior of {@link OMFactory#createOMElement(OMDataSource, QName)} if the data source
     * is <code>null</code>.
     */
    @Test
    public void createOMElementWithNullOMDataSource2() throws Throwable {
        assertThatThrownBy(() -> factory.createOMElement(null, new QName("urn:test", "test", "p")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createOMElementWithNullURIAndPrefix() {
        assertThatThrownBy(() -> factory.createOMElement("test", (String) null, (String) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createOMEntityReference() throws Throwable {
        OMElement parent = factory.createOMElement("test", null);
        OMEntityReference entref = factory.createOMEntityReference(parent, "testref");
        assertThat(entref.getParent()).isSameAs(parent);
        assertThat(entref.getName()).isEqualTo("testref");
        assertThat(entref.getReplacementText()).isNull();
    }

    @Test
    public void createOMEntityReferenceWithNullParent() throws Throwable {
        OMEntityReference entref = factory.createOMEntityReference(null, "testref");
        assertThat(entref.getParent()).isNull();
        assertThat(entref.getName()).isEqualTo("testref");
        assertThat(entref.getReplacementText()).isNull();
    }

    @Test
    public void createOMNamespace() throws Throwable {
        OMNamespace ns = factory.createOMNamespace("urn:test", "t");
        assertThat(ns.getNamespaceURI()).isEqualTo("urn:test");
        assertThat(ns.getPrefix()).isEqualTo("t");
    }

    @Test
    public void createOMNamespaceWithNullURI() throws Throwable {
        assertThatThrownBy(() -> factory.createOMNamespace(null, "t")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createOMProcessingInstructionWithoutParent() throws Throwable {
        OMProcessingInstruction pi = factory.createOMProcessingInstruction(null, "mypi", "data");
        assertThat(pi.getParent()).isNull();
        assertThat(pi.getTarget()).isEqualTo("mypi");
        assertThat(pi.getValue()).isEqualTo("data");
    }

    @Test
    public void createOMText() throws Throwable {
        OMNamespace namespace = factory.createOMNamespace("http://www.apache.org/~chinthaka", "myhome");
        OMElement omElement = factory.createOMElement("chinthaka", namespace);
        String text = "sampleText";
        OMText omText = factory.createOMText(omElement, text);
        assertThat(omText.isComplete()).isTrue();
        assertThat(omText.getText()).isEqualTo(text);
    }

    /** Tests {@link OMFactory#createOMText(OMContainer, OMText)}. */
    @Test
    public void createOMTextFromOMText() throws Throwable {
        OMText orgText = factory.createOMText("text");
        OMElement parent = factory.createOMElement("test", null);
        OMText text = factory.createOMText(parent, orgText);
        assertThat(text.getText()).isEqualTo("text");
        assertThat(text.getParent()).isSameAs(parent);
    }

    /**
     * Tests that {@link OMFactory#createOMText(OMContainer, String)} can be used to create an orphaned
     * node by setting <code>parent</code> to <code>null</code>.
     */
    @Test
    public void createOMTextWithNullParent() throws Throwable {
        OMText text = factory.createOMText(null, "text");
        assertThat(text.getParent()).isNull();
    }

    /**
     * Tests that the {@link OMFactory} returned by {@link OMMetaFactory} is a singleton. More precisely
     * this unit test checks that subsequent calls to {@link OMMetaFactory#getOMFactory()} return the
     * same instance.
     */
    @Test
    public void factoryIsSingleton() throws Throwable {
        assertThat(factory).isSameAs(factory);
    }

    /**
     * Tests that {@link OMFactory#getMetaFactory()} returns the reference of the {@link OMMetaFactory}
     * from which the reference to the {@link OMFactory} was obtained.
     */
    @Test
    public void getMetaFactory() throws Throwable {
        assertThat(metaFactory.getOMFactory().getMetaFactory()).isSameAs(metaFactory);
    }
}
