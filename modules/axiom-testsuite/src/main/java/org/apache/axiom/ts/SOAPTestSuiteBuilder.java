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
package org.apache.axiom.ts;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;

public class SOAPTestSuiteBuilder extends AxiomTestSuiteBuilder {
    public SOAPTestSuiteBuilder(OMMetaFactory metaFactory) {
        super(metaFactory);
    }
    
    private void addTests(String envelopeNamespaceURI) {
        addTest(new org.apache.axiom.ts.soap.body.TestAddFault1(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.body.TestAddFault2(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFault(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFaultWithParser(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFault(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultWithParser(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestAddHeaderToIncompleteEnvelope(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestBodyHeaderOrder(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestDiscardHeader(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBody(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyOnEmptyEnvelope(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyOnEnvelopeWithHeaderOnly(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyWithParser(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetHeader(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetHeaderWithParser(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestAddDetailEntry(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntries(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntriesWithParser(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestSerialization(metaFactory, envelopeNamespaceURI));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestWSCommons202(metaFactory, envelopeNamespaceURI));
    }
    
    protected void addTests() {
        addTests(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        addTests(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        addTest(new org.apache.axiom.ts.soap11.envelope.TestAddElementAfterBody(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.envelope.TestAddElementAfterBody(metaFactory));
    }
}
