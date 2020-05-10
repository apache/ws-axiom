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
package org.apache.axiom.soap.impl.intf.soap12;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public final class SOAP12Helper extends SOAPHelper {
    public static final SOAP12Helper INSTANCE = new SOAP12Helper();

    private SOAP12Helper() {
        super(SOAPVersion.SOAP12, "SOAP 1.2",
            AxiomSOAP12Envelope.class,
            AxiomSOAP12Header.class,
            AxiomSOAP12HeaderBlock.class,
            AxiomSOAP12Body.class,
            AxiomSOAP12Fault.class,
            AxiomSOAP12FaultCode.class,
            AxiomSOAP12FaultReason.class,
            AxiomSOAP12FaultRole.class,
            AxiomSOAP12FaultDetail.class,
            SOAP12Constants.SOAP_ROLE, SOAP12Constants.SOAP_RELAY);
    }

    @Override
    public SOAPFactory getSOAPFactory(OMMetaFactory metaFactory) {
        return metaFactory.getSOAP12Factory();
    }

    @Override
    public Boolean parseBoolean(String literal) {
        if (literal.equals("true") || literal.equals("1")) {
            return Boolean.TRUE;
        } else if (literal.equals("false") || literal.equals("0")) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    @Override
    public String formatBoolean(boolean value) {
        return String.valueOf(value);
    }
}
