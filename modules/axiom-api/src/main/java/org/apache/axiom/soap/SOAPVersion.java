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
 * A generic way to get at SOAP-version-specific values.  As long as we can get
 * one of these from a SOAP element, we can get at the right 
 */
public interface SOAPVersion {
    /**
     * Obtain the envelope namespace for this version of SOAP
     */
    public String getEnvelopeURI();

    /**
     * Obtain the encoding namespace for this version of SOAP
     */
    public String getEncodingURI();

    /**
     * Obtain the QName for the role attribute (actor/role)
     */
    public QName getRoleAttributeQName();

    /**
     * Obtain the "next" role/actor URI
     */
    public String getNextRoleURI();
}
