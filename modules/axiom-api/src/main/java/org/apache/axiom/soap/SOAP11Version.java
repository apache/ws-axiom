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

/**
 * Version-specific stuff for SOAP 1.1
 */
public class SOAP11Version implements SOAPVersion, SOAP11Constants {
    private static final SOAP11Version singleton = new SOAP11Version();
    public static SOAP11Version getSingleton() { return singleton; }

    private SOAP11Version() {
    }

    /** Obtain the envelope namespace for this version of SOAP */
    public String getEnvelopeURI() {
        return SOAP_ENVELOPE_NAMESPACE_URI;
    }

    /** Obtain the encoding namespace for this version of SOAP */
    public String getEncodingURI() {
        return SOAP_ENCODING_NAMESPACE_URI;
    }

    /** Obtain the QName for the role attribute (actor/role) */
    public QName getRoleAttributeQName() {
        return QNAME_ACTOR;
    }

    /** Obtain the "next" role/actor URI */
    public String getNextRoleURI() {
        return SOAP_ACTOR_NEXT;
    }

    /** Obtain the QName for the MustUnderstand fault code */
    public QName getMustUnderstandFaultCode() {
        return QNAME_MU_FAULTCODE;
    }

    /**
     * Obtain the QName for the Sender fault code
     *
     * @return Sender fault code as a QName
     */
    public QName getSenderFaultCode() {
        return QNAME_SENDER_FAULTCODE;
    }

    /**
     * Obtain the QName for the Receiver fault code
     *
     * @return Receiver fault code as a QName
     */
    public QName getReceiverFaultCode() {
        return QNAME_RECEIVER_FAULTCODE;
    }

    /**
     * Obtain the QName for the fault reason element
     *
     * @return
     */
    public QName getFaultReasonQName() {
        return QNAME_FAULT_REASON;
    }

    /**
     * Obtain the QName for the fault code element
     *
     * @return
     */
    public QName getFaultCodeQName() {
        return QNAME_FAULT_CODE;
    }

    /**
     * Obtain the QName for the fault detail element
     *
     * @return
     */
    public QName getFaultDetailQName() {
        return QNAME_FAULT_DETAIL;
    }

    /**
     * Obtain the QName for the fault role/actor element
     *
     * @return
     */
    public QName getFaultRoleQName() {
        return QNAME_FAULT_ROLE;
    }
}
