package org.apache.axiom.soap;

import javax.xml.namespace.QName;
/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
}
