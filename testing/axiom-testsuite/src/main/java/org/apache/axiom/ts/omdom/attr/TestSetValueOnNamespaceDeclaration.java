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
package org.apache.axiom.ts.omdom.attr;

import static com.google.common.truth.Truth.assertThat;

import java.util.Iterator;

import javax.xml.XMLConstants;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests that after calling {@link Attr#setValue(String)} on a namespace declaration the new
 * namespace URI is reflected in the result of {@link OMElement#getAllDeclaredNamespaces()}.
 */
public class TestSetValueOnNamespaceDeclaration extends AxiomTestCase {
    public TestSetValueOnNamespaceDeclaration(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc =
                ((DOMMetaFactory) metaFactory)
                        .newDocumentBuilderFactory()
                        .newDocumentBuilder()
                        .newDocument();
        Element element = doc.createElementNS("", "test");
        Attr attr = doc.createAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:attr");
        element.setAttributeNodeNS(attr);
        attr.setValue("urn:test");
        Iterator<OMNamespace> it = ((OMElement) element).getAllDeclaredNamespaces();
        assertThat(it.hasNext()).isTrue();
        OMNamespace ns = it.next();
        assertThat(ns.getNamespaceURI()).isEqualTo("urn:test");
        assertThat(it.hasNext()).isFalse();
    }
}
