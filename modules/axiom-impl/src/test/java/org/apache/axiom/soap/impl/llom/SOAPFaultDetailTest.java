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

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultDetailTestBase;

public class SOAPFaultDetailTest extends SOAPFaultDetailTestBase {
    public SOAPFaultDetailTest() {
        super(new OMLinkedListMetaFactory());
    }

    // TODO: this should also work for DOOM, but the SOAPFactory#createSOAPxxx methods
    //       are not implemented or don't work correctly
    public void testWSCommons202() {
        SOAPFactory factory = omMetaFactory.getSOAP12Factory();
        SOAPFaultDetail soapFaultDetail = factory.createSOAPFaultDetail();
        soapFaultDetail.setText("a");

        assertTrue(soapFaultDetail.getText().trim().equals("a"));
        assertTrue("Text serialization has problems. It had serialized same text twice", soapFaultDetail.toString().indexOf("aa") == -1);

        OMElement omElement = factory.createOMElement("DummyElement", null);
        soapFaultDetail.addChild(omElement);
        omElement.setText("Some text is here");

        assertTrue("Children of SOAP Fault Detail element are not serialized properly", soapFaultDetail.toString().indexOf("Some text is here") != -1);
    }
}
