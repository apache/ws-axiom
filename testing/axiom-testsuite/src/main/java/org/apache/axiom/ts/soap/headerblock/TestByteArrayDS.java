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

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests functionality of ByteArrayDataSource
 */
public class TestByteArrayDS extends SOAPTestCase {
    public TestByteArrayDS(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    protected void runTest() throws Throwable {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        String localName = "myPayload";
        String encoding = "utf-8";
        String payload1 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        OMNamespace ns = soapFactory.createOMNamespace("urn://test", "tns");
        ByteArrayDataSource bads1 = new ByteArrayDataSource(payload1.getBytes(encoding), encoding);
        
        // Set an empty MustUnderstand property on the data source
        bads1.setProperty(SOAPHeaderBlock.MUST_UNDERSTAND_PROPERTY, null);
        
        OMSourcedElement omse = soapFactory.createSOAPHeaderBlock(localName, ns, bads1);
        soapHeader.addChild(omse);
        OMNode firstChild = soapHeader.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof SOAPHeaderBlock);
        SOAPHeaderBlock child = (SOAPHeaderBlock) firstChild;
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertTrue("OMSourcedElement should be backed by a ByteArrayDataSource",
                   child.getDataSource() instanceof ByteArrayDataSource);
        
        // Make sure that getting the MustUnderstand property does not cause expansion.
        assertTrue(!child.getMustUnderstand());
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertTrue("OMSourcedElement should be backed by a ByteArrayDataSource",
                   child.getDataSource() instanceof ByteArrayDataSource);
        
        // A ByteArrayDataSource does not consume the backing object when read.
        // Thus getting the XMLStreamReader of the ByteArrayDataSource should not 
        // cause expansion of the OMSourcedElement.
        XMLStreamReader reader = child.getXMLStreamReader();
        reader.next();
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // Likewise, a ByteArrayDataSource does not consume the backing object when 
        // written.  Thus serializing the OMSourcedElement should not cause the expansion
        // of the OMSourcedElement.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapHeader.serialize(baos);
        String output = baos.toString(encoding);
//        System.out.println(output);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload1) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // Test getting the raw bytes from the ByteArrayDataSource.
        OMDataSourceExt ds = (OMDataSourceExt) child.getDataSource();
        byte[] bytes = ds.getXMLBytes("UTF-16");  // Get the bytes as UTF-16 
        String payload = new String(bytes, "utf-16");
        assertTrue("The obtained bytes did not match the payload",
                   payload1.equals(payload));
    }
}
