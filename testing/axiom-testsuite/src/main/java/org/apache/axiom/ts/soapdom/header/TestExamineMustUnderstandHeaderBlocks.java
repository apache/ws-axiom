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

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestExamineMustUnderstandHeaderBlocks extends SOAPTestCase {
    public TestExamineMustUnderstandHeaderBlocks(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeader header = soapFactory.createSOAPHeader();

        // Add header blocks using DOM
        Element domHeader = (Element) header;
        Document document = domHeader.getOwnerDocument();
        Element[] headerBlocks = new Element[3];
        for (int i = 0; i < 3; i++) {
            headerBlocks[i] = document.createElementNS("urn:test", "p:h" + i);
        }
        headerBlocks[1].setAttributeNS(spec.getEnvelopeNamespaceURI(), "S:mustUnderstand", "1");
        for (int i = 0; i < 3; i++) {
            // Clone the nodes because conversion to SOAPHeaderBlock is destructive
            domHeader.appendChild(headerBlocks[i].cloneNode(true));
        }

        // Use the Axiom API to iterate over the header blocks
        Iterator<SOAPHeaderBlock> it = header.examineMustUnderstandHeaderBlocks(null);
        assertThat(it.hasNext()).isTrue();
        SOAPHeaderBlock headerBlock = it.next();
        assertAbout(xml())
                .that(xml(OMElement.class, headerBlock))
                .hasSameContentAs(xml(Element.class, headerBlocks[1]));
        assertThat(it.hasNext()).isFalse();
    }
}
