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

package org.apache.axiom.soap;

import javax.xml.namespace.QName;

import org.apache.axiom.mime.MediaType;

/** Version-specific stuff for SOAP 1.1 */
public class SOAP11Version implements SOAPVersion, SOAP11Constants {
    /**
     * @deprecated Use {@link SOAPVersion#SOAP11} instead.
     */
    public static SOAP11Version getSingleton() {
        return (SOAP11Version) SOAPVersion.SOAP11;
    }

    SOAP11Version() {}

    @Override
    public String getEnvelopeURI() {
        return SOAP_ENVELOPE_NAMESPACE_URI;
    }

    @Override
    public String getEncodingURI() {
        return SOAP_ENCODING_NAMESPACE_URI;
    }

    @Override
    public QName getRoleAttributeQName() {
        return QNAME_ACTOR;
    }

    @Override
    public String getNextRoleURI() {
        return SOAP_ACTOR_NEXT;
    }

    @Override
    public QName getMustUnderstandFaultCode() {
        return QNAME_MU_FAULTCODE;
    }

    @Override
    public QName getSenderFaultCode() {
        return QNAME_SENDER_FAULTCODE;
    }

    @Override
    public QName getReceiverFaultCode() {
        return QNAME_RECEIVER_FAULTCODE;
    }

    @Override
    public QName getFaultReasonQName() {
        return QNAME_FAULT_REASON;
    }

    @Override
    public QName getFaultCodeQName() {
        return QNAME_FAULT_CODE;
    }

    @Override
    public QName getFaultDetailQName() {
        return QNAME_FAULT_DETAIL;
    }

    @Override
    public QName getFaultRoleQName() {
        return QNAME_FAULT_ROLE;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.TEXT_XML;
    }
}
