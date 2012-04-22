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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.om.ds.InputStreamDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Verifies that a OMDataSource can be replaced with another one
 */
public class TestSetDataSource extends AxiomTestCase {
    public TestSetDataSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        // TODO Auto-generated method stub
        
    }

    public void testOMSEReplacement() throws Exception {
        String ENCODING = "utf-8";
        String payload1 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload One</tns:myPayload>";
        String payload2 = "<tns:myPayload xmlns:tns=\"urn://test\">Payload Two</tns:myPayload>";
        ByteArrayDataSource bads1 = new ByteArrayDataSource(payload1.getBytes(ENCODING), ENCODING);
        ByteArrayDataSource bads2 = new ByteArrayDataSource(payload2.getBytes(ENCODING), ENCODING);
        InputStreamDataSource isds1 = new InputStreamDataSource(new ByteArrayInputStream(payload1.getBytes(ENCODING)), ENCODING);
        InputStreamDataSource isds2 = new InputStreamDataSource(new ByteArrayInputStream(payload2.getBytes(ENCODING)), ENCODING);
        
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", null);
        OMSourcedElement omse = factory.createOMElement(bads1, "myPayload", factory.createOMNamespace("urn://test", "tns"));
        parent.addChild(omse);
        OMNode firstChild = parent.getFirstOMChild();
        assertTrue("Expected OMSourcedElement child", firstChild instanceof OMSourcedElement);
        OMSourcedElement child = (OMSourcedElement) firstChild;
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        assertTrue("OMSourcedElement should be backed by a ByteArrayDataSource",
                   child.getDataSource() instanceof ByteArrayDataSource);
        
        // Write out the body
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        parent.serialize(baos);
        String output = baos.toString(ENCODING);
//        System.out.println(output);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload1) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // Replace with payload2.  
        // Important note, it is legal to replace the OMDataSource, but
        // the namespace and local name of the OMSourcedElement cannot be changed.
        child.setDataSource(bads2);
        
        // Write out the body
        baos = new ByteArrayOutputStream();
        parent.serialize(baos);
        output = baos.toString(ENCODING);
//        System.out.println(output);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload2) > 0);
        assertTrue("OMSourcedElement is expanded.  This is unexpected", !child.isExpanded());
        
        // Now Replace with payload1 from an InputStreamDataSource
        child.setDataSource(isds1);
        baos = new ByteArrayOutputStream();
        parent.serialize(baos);
        output = baos.toString(ENCODING);
//        System.out.println(output);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload1) > 0);
        
        // Now Replace with payload2 from an InputStreamDataSource.
        // Note at this point, the child's tree is expanded.
        child.setDataSource(isds2);
        baos = new ByteArrayOutputStream();
        parent.serialize(baos);
        output = baos.toString(ENCODING);
//        System.out.println(output);
        assertTrue("The payload was not present in the output",
                   output.indexOf(payload2) > 0);
    }
}
