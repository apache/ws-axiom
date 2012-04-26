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
package org.apache.axiom.ts.om.sourcedelement;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests functionality of ByteArrayDataSource
 */
public class TestByteArrayDS extends AxiomTestCase {
    public TestByteArrayDS(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        
        String localName = "myPayload";
        String encoding = "utf-8";
        String payload1 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        OMNamespace ns = factory.createOMNamespace("urn://test", "tns");
        ByteArrayDataSource bads1 = new ByteArrayDataSource(payload1.getBytes(encoding), encoding);

        OMElement parent = factory.createOMElement("root", null);
        OMSourcedElement omse = factory.createOMElement(bads1, localName, ns);
        parent.addChild(omse);
        OMNode firstChild = parent.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof OMSourcedElement);
        OMSourcedElement child = (OMSourcedElement) firstChild;
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
        parent.serialize(baos);
        String output = baos.toString(encoding);
//        System.out.println(output);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload1) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // If a consumer calls build or buildWithAttachments on the tree, the 
        // tree should not be expanded.
        parent.build();
        assertTrue("OMSourcedElement is expanded after build().  This is unexpected", !child.isExpanded());
        parent.buildWithAttachments();
        assertTrue("OMSourcedElement is expanded after buildWithAttachments().  This is unexpected", !child.isExpanded());
        
        // Test getting the raw bytes from the ByteArrayDataSource.
        OMDataSourceExt ds = (OMDataSourceExt) child.getDataSource();
        byte[] bytes = ds.getXMLBytes("UTF-16");  // Get the bytes as UTF-16 
        String payload = new String(bytes, "utf-16");
        assertTrue("The obtained bytes did not match the payload",
                   payload1.equals(payload));
        
       
        // Test getting the raw bytes with the default encoding
        OMOutputFormat outputFormat = new OMOutputFormat();
        baos = new ByteArrayOutputStream();
        ds.serialize(baos, outputFormat);
        output = baos.toString(OMOutputFormat.DEFAULT_CHAR_SET_ENCODING);
//        System.out.println(output);
        assertTrue("The obtained bytes did not match the payload",
                   payload1.equals(output));     
    }
}
