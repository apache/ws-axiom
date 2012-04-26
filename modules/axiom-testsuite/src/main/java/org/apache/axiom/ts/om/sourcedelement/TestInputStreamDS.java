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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.InputStreamDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests functionality of InputStreamDataSource
 */
public class TestInputStreamDS extends AxiomTestCase {
    public TestInputStreamDS(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        
        String localName = "myPayload";
        String encoding = "utf-8";
        String payload1 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        OMNamespace ns = factory.createOMNamespace("urn://test", "tns");
        ByteArrayInputStream bais1 = new ByteArrayInputStream(payload1.getBytes(encoding));
        InputStreamDataSource isds1 = new InputStreamDataSource(bais1, encoding);
        
        OMElement parent = factory.createOMElement("root", null);
        OMSourcedElement omse = factory.createOMElement(isds1, localName, ns);
        parent.addChild(omse);
        OMNode firstChild = parent.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof OMSourcedElement);
        OMSourcedElement child = (OMSourcedElement) firstChild;
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertTrue("OMSourcedElement should be backed by a InputStreamDataSource",
                   child.getDataSource() instanceof InputStreamDataSource);
        
        // A InputStreamDataSource consumes the backing object when read.
        // Thus getting the XMLStreamReader of the ByteArrayDataSource should  
        // cause expansion of the OMSourcedElement.
        XMLStreamReader reader = child.getXMLStreamReader();
        reader.next();
        assertTrue("OMSourcedElement is not expanded.  This is unexpected", 
                   child.isExpanded());
        
        child.detach();
        
        // Reset the tree
        isds1 = new InputStreamDataSource(
            new ByteArrayInputStream(payload1.getBytes(encoding)), 
            encoding);
        omse = factory.createOMElement(isds1, localName, ns);
        parent.addChild(omse);
        firstChild = parent.getFirstOMChild();
        child = (OMSourcedElement) firstChild;
        
        // Likewise, an InputStreamDataSource consumes the backing object when 
        // written.  Thus serializing the OMSourcedElement should cause the expansion
        // of the OMSourcedElement.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        parent.serialize(baos);
        String output = baos.toString(encoding);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload1) > 0);
        assertTrue("OMSourcedElement is not expanded.  This is unexpected", child.isExpanded());
        
        // Reset the tree
        child.detach();
        isds1 = new InputStreamDataSource(
            new ByteArrayInputStream(payload1.getBytes(encoding)), 
            encoding);
        omse = factory.createOMElement(isds1, localName, ns);
        parent.addChild(omse);
        firstChild = parent.getFirstOMChild();
        child = (OMSourcedElement) firstChild;
        
        // Test getting the raw bytes from the ByteArrayDataSource.
        OMDataSourceExt ds = (OMDataSourceExt) child.getDataSource();
        byte[] bytes = ds.getXMLBytes(encoding);  // Get the bytes as UTF-16 
        String payload = new String(bytes, encoding);
        assertTrue("The obtained bytes did not match the payload",
                   payload1.equals(payload));
    }
}
