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
package org.apache.axiom.ts.soap.faulttext;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestSetLang extends SOAPTestCase {
    public TestSetLang(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPFaultText faultText = soapFactory.createSOAPFaultText();
        faultText.setLang("en");
        assertEquals(
                "SOAP Fault Text Test : - After calling setLang method, Lang attribute value mismatch",
                "en",
                faultText.getLang());
        OMAttribute langAttribute = faultText.getAllAttributes().next();
        assertEquals(
                "SOAP Fault Text Test : - After calling setLang method, Lang attribute local name mismaatch",
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME,
                langAttribute.getLocalName());
        assertEquals(
                "SOAP Fault Text Test : - After calling setLang method, Lang attribute namespace prefix mismatch",
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX,
                langAttribute.getNamespace().getPrefix());
        assertEquals(
                "SOAP Fault Text Test : - After calling setLang method, Lang attribute namespace uri mismatch",
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_URI,
                langAttribute.getNamespace().getNamespaceURI());
    }
}
