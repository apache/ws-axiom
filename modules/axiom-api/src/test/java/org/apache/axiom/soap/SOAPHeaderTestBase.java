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

package org.apache.axiom.soap;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;

public class SOAPHeaderTestBase extends UnifiedSOAPTestCase {
    protected final String roleNextURI;
    
    public SOAPHeaderTestBase(OMMetaFactory omMetaFactory, String envelopeNamespaceURI, String roleNextURI) {
        super(omMetaFactory, envelopeNamespaceURI);
        this.roleNextURI = roleNextURI;
    }

    //SOAP Header Test (Programaticaly Created)--------------------------------------------------------------------------------
    public void testAddHeaderBlock() {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        soapHeader.addHeaderBlock("echoOk1", namespace);
        soapHeader.addHeaderBlock("echoOk2", namespace);
        Iterator iterator = soapHeader.getChildren();
        SOAPHeaderBlock headerBlock1 = (SOAPHeaderBlock) iterator.next();
        assertFalse(
                "SOAP Header Test : - After calling addHeaderBlock method twice, getChildren method returns empty iterator",
                headerBlock1 == null);
        assertTrue("SOAP Header Test : - HeaderBlock1 local name mismatch",
                   headerBlock1.getLocalName().equals("echoOk1"));
        assertTrue(
                "SOAP Header Test : - HeaderBlock1 namespace uri mismatch",
                headerBlock1.getNamespace().getNamespaceURI().equals(
                        "http://www.example.org"));

        SOAPHeaderBlock headerBlock2 = (SOAPHeaderBlock) iterator.next();
        assertFalse(
                "SOAP Header Test : - After calling addHeaderBlock method twice, getChildren method returns an iterator with only one object",
                headerBlock2 == null);
        assertTrue("SOAP Header Test : - HeaderBlock2 local name mismatch",
                   headerBlock2.getLocalName().equals("echoOk2"));
        assertTrue(
                "SOAP Header Test : - HeaderBlock2 namespace uri mismatch",
                headerBlock2.getNamespace().getNamespaceURI().equals(
                        "http://www.example.org"));

        assertTrue(
                "SOAP Header Test : - After calling addHeaderBlock method twice, getChildren method returns an iterator with more than two elements",
                !iterator.hasNext());
    }

    public void testExamineHeaderBlocks() {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        soapHeader.addHeaderBlock("echoOk1", namespace).setRole("urn:test-role");
        soapHeader.addHeaderBlock("echoOk2", namespace).setRole(roleNextURI);
        Iterator iterator = soapHeader.examineHeaderBlocks(roleNextURI);
        iterator.hasNext();
        SOAPHeaderBlock headerBlockWithRole = (SOAPHeaderBlock) iterator.next();
        assertEquals(
                "SOAP Header Test : - headerBlockWithRole local name mismatch",
                "echoOk2", headerBlockWithRole.getLocalName());
        assertEquals(
                "SOAP Header Test : - headerBlockWithRole role value mismatch",
                roleNextURI, headerBlockWithRole.getRole());

        assertFalse(
                "SOAP Header Test : - header has one headerBlock with role, but examineHeaderBlocks(String role) method returns an iterator with more than one object",
                iterator.hasNext());
    }

    public void testExamineAllHeaderBlocks() {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        soapHeader.addHeaderBlock("echoOk1", namespace);
        soapHeader.addHeaderBlock("echoOk2", namespace);
        Iterator iterator = soapHeader.examineAllHeaderBlocks();
        iterator.hasNext();
        SOAPHeaderBlock headerBlock1 = (SOAPHeaderBlock) iterator.next();
        assertFalse(
                "SOAP Header Test : - After calling addHeaderBlock method twice, examineAllHeaderBlocks method returns empty iterator",
                headerBlock1 == null);
        assertTrue("SOAP Header Test : - HeaderBlock1 local name mismatch",
                   headerBlock1.getLocalName().equals("echoOk1"));
        assertTrue(
                "SOAP Header Test : - HeaderBlock1 namespace uri mismatch",
                headerBlock1.getNamespace().getNamespaceURI().equals(
                        "http://www.example.org"));

        assertTrue(iterator.hasNext());
        SOAPHeaderBlock headerBlock2 = (SOAPHeaderBlock) iterator.next();
        assertFalse(
                "SOAP Header Test : - After calling addHeaderBlock method twice, examineAllHeaderBlocks method returns an iterator with only one object",
                headerBlock2 == null);
        assertTrue("SOAP Header Test : - HeaderBlock2 local name mismatch",
                   headerBlock2.getLocalName().equals("echoOk2"));
        assertTrue(
                "SOAP Header Test : - HeaderBlock2 namespace uri mismatch",
                headerBlock2.getNamespace().getNamespaceURI().equals(
                        "http://www.example.org"));

        assertFalse(
                "SOAP Header Test : - After calling addHeaderBlock method twice, examineAllHeaderBlocks method returns an iterator with more than two object",
                iterator.hasNext());
    }

    public void testGetHeaderBlocksWithNSURI() {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        soapHeader.addHeaderBlock("echoOk1", namespace);
        soapHeader.addHeaderBlock("echoOk2",
                                    soapFactory.createOMNamespace("http://www.test1.org", "test1"));
        ArrayList arrayList = soapHeader.getHeaderBlocksWithNSURI(
                "http://www.test1.org");
        assertTrue(
                "SOAP Header Test : - getHeaderBlocksWithNSURI returns an arrayList of incorrect size",
                arrayList.size() == 1);
        assertTrue(
                "SOAP Header Test : - headerBlock of given namespace uri mismatch",
                ((SOAPHeaderBlock) arrayList.get(0)).getNamespace().getNamespaceURI()
                        .equals("http://www.test1.org"));
    }
}
