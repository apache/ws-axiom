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
package org.apache.axiom.ts.soap.header;

import java.util.Iterator;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Tests the behavior of {@link SOAPHeader#getHeadersToProcess(RolePlayer, String)} */
public class TestGetHeadersToProcessWithNamespace extends SOAPTestCase {
    public TestGetHeadersToProcessWithNamespace(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPHeader header = soapFactory.createSOAPHeader(envelope);
        OMNamespace ns1 = soapFactory.createOMNamespace("urn:ns1", "ns1");
        OMNamespace ns2 = soapFactory.createOMNamespace("urn:ns2", "ns2");
        String myRole = "urn:myRole";
        String otherRole = "urn:otherRole";
        SOAPHeaderBlock headerBlock1 = header.addHeaderBlock("header1", ns1);
        headerBlock1.setRole(myRole);
        SOAPHeaderBlock headerBlock2 = header.addHeaderBlock("header2", ns2);
        headerBlock2.setRole(myRole);
        SOAPHeaderBlock headerBlock3 = header.addHeaderBlock("header3", ns1);
        headerBlock3.setRole(myRole);
        SOAPHeaderBlock headerBlock4 = header.addHeaderBlock("header4", ns1);
        headerBlock4.setRole(otherRole);
        Iterator<SOAPHeaderBlock> it =
                header.getHeadersToProcess(
                        new MyRolePlayer(false, new String[] {myRole}), ns1.getNamespaceURI());
        assertTrue(it.hasNext());
        assertSame(headerBlock1, it.next());
        assertTrue(it.hasNext());
        assertSame(headerBlock3, it.next());
        assertFalse(it.hasNext());
    }
}
