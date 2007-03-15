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
 * Version-specific stuff for SOAP 1.2
 */
public class SOAP12Version implements SOAPVersion, SOAP12Constants {
    private static final SOAP12Version singleton = new SOAP12Version();
    public static SOAP12Version getSingleton() { return singleton; }

    QName actorQName = new QName(SOAP_ENVELOPE_NAMESPACE_URI, SOAP_ROLE);

    private SOAP12Version() {
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
        return actorQName;
    }

    /** Obtain the "next" role/actor URI */
    public String getNextRoleURI() {
        return SOAP_ROLE_NEXT;
    }
}
