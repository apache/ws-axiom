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
package org.apache.axiom.ts.soap.envelope;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;

public class TestCloneOMElementWithSourcedElement2 extends CloneOMElementTestCase {
    public TestCloneOMElementWithSourcedElement2(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    protected void runTest() throws Throwable {
        SOAPEnvelope sourceEnv = soapFactory.getDefaultEnvelope();
        SOAPBody body = sourceEnv.getBody();
        SOAPHeader header = sourceEnv.getHeader();
        String encoding = "UTF-8";
        
        // Create a header OMSE
        String hdrText = "<hdr:myheader xmlns:hdr=\"urn://test\">Hello World</hdr:myheader>";
        ByteArrayDataSource badsHdr = 
            new ByteArrayDataSource(hdrText.getBytes(encoding), encoding);
        OMNamespace hdrNS = header.getOMFactory().createOMNamespace("urn://test", "hdr");
        SOAPFactory sf = (SOAPFactory) header.getOMFactory();
        SOAPHeaderBlock shb = sf.createSOAPHeaderBlock("myheader", hdrNS, badsHdr);
        shb.setProcessed();  // test setting processing flag
        header.addChild(shb);
        
        // Create a payload
        String text = "<tns:payload xmlns:tns=\"urn://test\">Hello World</tns:payload>";
        ByteArrayDataSource bads = new ByteArrayDataSource(text.getBytes(encoding), encoding);
        OMNamespace ns = body.getOMFactory().createOMNamespace("urn://test", "tns");
        OMSourcedElement omse =body.getOMFactory().createOMElement(bads, "payload", ns);
        body.addChild(omse);
        
        copyAndCheck(sourceEnv);
        
        // The source SOAPHeaderBlock should not be expanded in the process
        assertFalse(shb.isExpanded());
    }
}
