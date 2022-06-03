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
package org.apache.axiom.ts.soap.faultdetail;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

// SOAP Fault Detail Test (With Parser)
public class TestGetAllDetailEntriesWithParser extends SampleBasedSOAPTestCase {
    public TestGetAllDetailEntriesWithParser(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.SIMPLE_FAULT);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPFaultDetail soapFaultDetail = envelope.getBody().getFault().getDetail();
        Iterator<OMElement> iterator = soapFaultDetail.getAllDetailEntries();
        OMElement detailEntry1 = iterator.next();
        assertNotNull(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns an itrator without detail entries",
                detailEntry1);
        assertEquals(
                "SOAP Fault Detail Test With Parser : - detailEntry1 localname mismatch",
                "ErrorCode",
                detailEntry1.getLocalName());
        OMElement detailEntry2 = iterator.next();
        assertNotNull(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns an itrator with only one detail entries",
                detailEntry2);
        assertEquals(
                "SOAP Fault Detail Test With Parser : - detailEntry2 localname mismatch",
                "Message",
                detailEntry2.getLocalName());
        assertFalse(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns an itrator with more than two detail entries",
                iterator.hasNext());
    }
}
