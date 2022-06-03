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
package org.apache.axiom.ts.soap12.faulttext;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

public class TestGetLangWithParser extends SampleBasedSOAPTestCase {
    public TestGetLangWithParser(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSpec.SOAP12, SOAPSampleSet.SIMPLE_FAULT);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPFaultText faultText = envelope.getBody().getFault().getReason().getFirstSOAPText();
        assertTrue(
                "SOAP 1.2 Fault Text Test With Parser : - getLang method returns incorrect string",
                faultText.getLang().equals("en"));
        OMAttribute langAttribute = faultText.getAllAttributes().next();
        assertTrue(
                "SOAP 1.2 Fault Text Test With Parser : - Lang attribute local name mismaatch",
                langAttribute
                        .getLocalName()
                        .equals(SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 Fault Text Test With Parser : - Lang attribute namespace prefix mismatch",
                langAttribute
                        .getNamespace()
                        .getPrefix()
                        .equals(SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX));
        assertTrue(
                "SOAP 1.2 Fault Text Test With Parser : - Lang attribute namespace uri mismatch",
                langAttribute
                        .getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_URI));
    }
}
