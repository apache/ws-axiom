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
package org.apache.axiom.soap.impl.intf.soap11;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public final class SOAP11Helper extends SOAPHelper {
    public static final SOAP11Helper INSTANCE = new SOAP11Helper();

    private SOAP11Helper() {
        super(
                SOAPVersion.SOAP11,
                "SOAP 1.1",
                AxiomNodeFactory::createSOAP11Envelope,
                AxiomNodeFactory::createSOAP11Header,
                AxiomNodeFactory::createSOAP11HeaderBlock,
                AxiomNodeFactory::createSOAP11Body,
                AxiomNodeFactory::createSOAP11Fault,
                AxiomNodeFactory::createSOAP11FaultCode,
                AxiomNodeFactory::createSOAP11FaultReason,
                AxiomNodeFactory::createSOAP11FaultRole,
                AxiomNodeFactory::createSOAP11FaultDetail,
                SOAP11Constants.ATTR_ACTOR,
                null);
    }

    @Override
    public SOAPFactory getSOAPFactory(OMMetaFactory metaFactory) {
        return metaFactory.getSOAP11Factory();
    }

    @Override
    public Boolean parseBoolean(String literal) {
        if (literal.equals("1")) {
            return Boolean.TRUE;
        } else if (literal.equals("0")) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    @Override
    public String formatBoolean(boolean value) {
        return value ? "1" : "0";
    }
}
