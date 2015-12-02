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
package org.apache.axiom.datatype.helper.dom;

import static com.google.common.truth.Truth.assertThat;

import java.text.ParseException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.datatype.xsd.XSQNameType;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMHelperTest {
    private static Document newDocument() throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }
    
    @Test
    public void testGetQNameFromElement() throws Exception {
        Document document = newDocument();
        Element element = document.createElementNS("urn:test", "p:elem");
        element.setTextContent("p:value");
        QName qname = DOMHelper.getValue(element, XSQNameType.INSTANCE);
        assertThat(qname.getNamespaceURI()).isEqualTo("urn:test");
        assertThat(qname.getLocalPart()).isEqualTo("value");
        assertThat(qname.getPrefix()).isEqualTo("p");
    }
    
    @Test(expected=ParseException.class)
    public void testGetQNameFromElementUnboundPrefix() throws Exception {
        Document document = newDocument();
        Element element = document.createElementNS(null, "test");
        element.setTextContent("ns:test");
        DOMHelper.getValue(element, XSQNameType.INSTANCE);
    }
    
    @Test
    public void testGetQNameFromElementNoDefaultNamespace() throws Exception {
        Document document = newDocument();
        Element element = document.createElementNS("urn:test", "p:test");
        element.setTextContent("value");
        QName qname = DOMHelper.getValue(element, XSQNameType.INSTANCE);
        assertThat(qname.getNamespaceURI()).isEmpty();
        assertThat(qname.getLocalPart()).isEqualTo("value");
        assertThat(qname.getPrefix()).isEmpty();
    }
}
