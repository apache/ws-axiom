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
package org.apache.axiom.datatype;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.datatype.xsd.XSQNameType;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMHelperTest {
    @Test
    public void testGetQNameFromElement() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = document.createElementNS("urn:test", "p:elem");
        element.setTextContent("p:value");
        QName qname = DOMHelper.getValue(element, XSQNameType.INSTANCE);
        assertThat(qname.getNamespaceURI()).isEqualTo("urn:test");
        assertThat(qname.getLocalPart()).isEqualTo("value");
        assertThat(qname.getPrefix()).isEqualTo("p");
    }
}
