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
package org.apache.axiom.ts.soap.headerblock;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.BlobOMDataSource;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Tests functionality of BlobOMDataSource */
public class TestBlobOMDataSource extends SOAPTestCase {
    public TestBlobOMDataSource(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        String localName = "myPayload";
        String encoding = "utf-8";
        String payload = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        OMNamespace ns = soapFactory.createOMNamespace("urn://test", "tns");
        BlobOMDataSource ds =
                new BlobOMDataSource(Blobs.createBlob(payload.getBytes(encoding)), encoding);

        // Set an empty MustUnderstand property on the data source
        ds.setProperty(SOAPHeaderBlock.MUST_UNDERSTAND_PROPERTY, null);

        OMSourcedElement omse = soapFactory.createSOAPHeaderBlock(localName, ns, ds);
        soapHeader.addChild(omse);
        OMNode firstChild = soapHeader.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof SOAPHeaderBlock);
        SOAPHeaderBlock child = (SOAPHeaderBlock) firstChild;
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertThat(child.getDataSource()).isSameInstanceAs(ds);

        // Make sure that getting the MustUnderstand property does not cause expansion.
        assertTrue(!child.getMustUnderstand());
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertThat(child.getDataSource()).isSameInstanceAs(ds);

        // A BlobOMDataSource does not consume the backing object when read.
        // Thus getting the XMLStreamReader of the BlobOMDataSource should not
        // cause expansion of the OMSourcedElement.
        XMLStreamReader reader = child.getXMLStreamReader();
        reader.next();
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());

        // Likewise, a BlobOMDataSource does not consume the backing object when
        // written.  Thus serializing the OMSourcedElement should not cause the expansion
        // of the OMSourcedElement.
        assertTrue(
                "The payload was not present in the output",
                soapHeader.toString().indexOf(payload) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());

        assertThat(child.getDataSource()).isSameInstanceAs(ds);
    }
}
