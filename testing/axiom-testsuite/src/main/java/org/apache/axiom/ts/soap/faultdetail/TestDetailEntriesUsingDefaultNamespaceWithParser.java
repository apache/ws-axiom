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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

/** Tests parsing of a SOAP fault that has detail entries that use a default namespace. */
public class TestDetailEntriesUsingDefaultNamespaceWithParser extends SampleBasedSOAPTestCase {
    public TestDetailEntriesUsingDefaultNamespaceWithParser(
            OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.FAULT_DETAIL_DEFAULT_NAMESPACE);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPFaultDetail soapFaultDetail = envelope.getBody().getFault().getDetail();
        OMElement detailElement = soapFaultDetail.getFirstElement();
        assertEquals("AddNumbersHandlerFault", detailElement.getLocalName());
        OMNamespace ns = detailElement.getNamespace();
        // At some point, there was a bug in Axiom that caused the prefix to be null
        assertEquals("", ns.getPrefix());
        assertEquals("http://www.example.org/addnumbershandler", ns.getNamespaceURI());
    }
}
