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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.BlobOMDataSource;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.junit.jupiter.api.function.Executable;

/** Tests functionality of BlobOMDataSource */
public class TestBlobOMDataSource implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        String localName = "myPayload";
        String encoding = "utf-8";
        String payload = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        OMNamespace ns = soapFactory.createOMNamespace("urn://test", "tns");
        BlobOMDataSource ds = new BlobOMDataSource(Blobs.createBlob(payload.getBytes(encoding)), encoding);

        // Set an empty MustUnderstand property on the data source
        ds.setProperty(SOAPHeaderBlock.MUST_UNDERSTAND_PROPERTY, null);

        OMSourcedElement omse = soapFactory.createSOAPHeaderBlock(localName, ns, ds);
        soapHeader.addChild(omse);
        OMNode firstChild = soapHeader.getFirstOMChild();
        assertThat(firstChild).isInstanceOf(SOAPHeaderBlock.class);
        SOAPHeaderBlock child = (SOAPHeaderBlock) firstChild;
        assertThat(child.isExpanded()).isFalse();
        assertThat(child.getDataSource()).isSameAs(ds);

        // Make sure that getting the MustUnderstand property does not cause expansion.
        assertThat(child.getMustUnderstand()).isFalse();
        assertThat(child.isExpanded()).isFalse();
        assertThat(child.getDataSource()).isSameAs(ds);

        // A BlobOMDataSource does not consume the backing object when read.
        // Thus getting the XMLStreamReader of the BlobOMDataSource should not
        // cause expansion of the OMSourcedElement.
        XMLStreamReader reader = child.getXMLStreamReader();
        reader.next();
        assertThat(child.isExpanded()).isFalse();

        // Likewise, a BlobOMDataSource does not consume the backing object when
        // written.  Thus serializing the OMSourcedElement should not cause the expansion
        // of the OMSourcedElement.
        assertThat(soapHeader.toString()).contains(payload);
        assertThat(child.isExpanded()).isFalse();

        assertThat(child.getDataSource()).isSameAs(ds);
    }
}
