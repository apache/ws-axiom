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

import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;
import org.apache.axiom.om.OMMetaFactory;

public class TestGetHeadersToProcessWithParser extends SampleBasedSOAPTestCase {
    public TestGetHeadersToProcessWithParser(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.HEADERS);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPHeader soapHeader = envelope.getHeader();

        String roles[] = {"http://example.org/myCustomRole"};
        RolePlayer rp = new MyRolePlayer(true, roles);

        Iterator<SOAPHeaderBlock> headers = soapHeader.getHeadersToProcess(rp);
        assertTrue("No headers!", headers.hasNext());

        int numHeaders = 0;
        while (headers.hasNext()) {
            headers.next();
            numHeaders++;
        }

        assertEquals("Didn't get right number of headers (with custom role)", 6, numHeaders);

        rp = new MyRolePlayer(true);

        headers = soapHeader.getHeadersToProcess(rp);
        assertTrue(headers.hasNext());

        numHeaders = 0;
        while (headers.hasNext()) {
            headers.next();
            numHeaders++;
        }

        assertEquals("Didn't get right number of headers (no custom role)", 5, numHeaders);

        // Intermediary test
        rp = new MyRolePlayer(false);

        headers = soapHeader.getHeadersToProcess(rp);
        assertTrue(headers.hasNext());

        numHeaders = 0;
        while (headers.hasNext()) {
            headers.next();
            numHeaders++;
        }

        assertEquals("Didn't get right number of headers (no custom role)", 2, numHeaders);
    }
}
