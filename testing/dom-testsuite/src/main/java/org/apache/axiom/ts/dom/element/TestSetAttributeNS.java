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
package org.apache.axiom.ts.dom.element;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axiom.ts.dom.DOMUtils;
import org.junit.jupiter.api.function.Executable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class TestSetAttributeNS implements Executable {
    @Inject
    private DocumentBuilderFactory dbf;

    @Inject
    private QName qname;

    @Override
    public void execute() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element element = document.createElementNS("urn:ns1", "p:element");
        element.setAttributeNS(DOMUtils.getNamespaceURI(qname), DOMUtils.getQualifiedName(qname), "value");
        assertThat(element.hasAttributes()).isTrue();
        NamedNodeMap attributes = element.getAttributes();
        assertThat(attributes.getLength()).isEqualTo(1);
        Attr attr = (Attr) attributes.item(0);
        assertThat(attr.getOwnerDocument()).isSameAs(document);
        assertThat(attr.getOwnerElement()).isSameAs(element);
        assertThat(attr.getNamespaceURI()).isEqualTo(DOMUtils.getNamespaceURI(qname));
        assertThat(attr.getPrefix()).isEqualTo(DOMUtils.getPrefix(qname));
        assertThat(attr.getLocalName()).isEqualTo(qname.getLocalPart());
        assertThat(attr.getName()).isEqualTo(DOMUtils.getQualifiedName(qname));
        assertThat(attr.getValue()).isEqualTo("value");
        assertThat(element.getAttributeNodeNS(DOMUtils.getNamespaceURI(qname), qname.getLocalPart()))
                .isSameAs(attr);
    }
}
