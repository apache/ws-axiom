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
package org.apache.axiom.ts.saaj.element;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.XMLConstants;
import jakarta.xml.soap.SOAPElement;

import org.apache.axiom.ts.saaj.SAAJTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

/** Tests the behavior of {@link SOAPElement#addChildElement(String, String, String)}. */
public class TestAddChildElementLocalNamePrefixAndURI extends SAAJTestCase {
    @Override
    protected void runTest() throws Throwable {
        SOAPElement root = newSOAPFactory().createElement("root", "ns1", "urn:ns1");
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
}
