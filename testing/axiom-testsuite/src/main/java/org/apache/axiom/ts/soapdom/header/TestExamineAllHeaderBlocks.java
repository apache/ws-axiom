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
package org.apache.axiom.ts.soapdom.header;

import static com.google.common.truth.Truth.assertThat;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests that child elements of a {@link SOAPHeader} created using the DOM API are converted on the
 * fly and returned as {@link SOAPHeaderBlock} instances by {@link
 * SOAPHeader#examineAllHeaderBlocks()}.
 */
public class TestExamineAllHeaderBlocks extends SOAPTestCase {
    public TestExamineAllHeaderBlocks(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeader header = soapFactory.createSOAPHeader();

        // Add header blocks using DOM
        Element domHeader = (Element) header;
        Document document = domHeader.getOwnerDocument();
        Element headerBlock1 = document.createElementNS("urn:test", "p:h1");
        headerBlock1.setAttributeNS(spec.getEnvelopeNamespaceURI(), "S:mustUnderstand", "1");
        headerBlock1.setTextContent("test");
        Element headerBlock2 = document.createElementNS("urn:test", "p:h2");
        headerBlock2.appendChild(document.createElementNS(null, "test"));
        domHeader.appendChild(headerBlock1);
        domHeader.appendChild(headerBlock2);

        // Use the Axiom API to iterate over the header blocks
        Iterator<SOAPHeaderBlock> it = header.examineAllHeaderBlocks();
        assertThat(it.hasNext()).isTrue();
        SOAPHeaderBlock headerBlock = it.next();
        assertThat(headerBlock.getQName()).isEqualTo(new QName("urn:test", "h1"));
        assertThat(headerBlock.getMustUnderstand()).isTrue();
        assertThat(headerBlock.getText()).isEqualTo("test");
        assertThat(it.hasNext()).isTrue();
        headerBlock = it.next();
        assertThat(headerBlock.getQName()).isEqualTo(new QName("urn:test", "h2"));
        assertThat(headerBlock.getFirstOMChild()).isInstanceOf(OMElement.class);
        assertThat(it.hasNext()).isFalse();
    }
}
