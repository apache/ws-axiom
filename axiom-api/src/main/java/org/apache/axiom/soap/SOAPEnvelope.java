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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;

/** Interface SOAPEnvelope */
public interface SOAPEnvelope extends OMElement {
    /**
     * Returns the {@link SOAPHeader} object for this envelope. This method takes advantage of the
     * fact that the {@link SOAPHeader} must be the first child element (if it exists) to avoid
     * building the {@link SOAPBody} if it is still incomplete.
     *
     * @return the {@link SOAPHeader} object or <code>null</code> if there is none
     */
    SOAPHeader getHeader();

    /**
     * Returns the existing {@link SOAPHeader} object for this envelope or creates a new one if
     * there is none. Similarly to {@link #getHeader()}, this method avoids building the {@link
     * SOAPBody}.
     *
     * @return the existing or newly created {@link SOAPHeader} object for this envelope
     */
    SOAPHeader getOrCreateHeader();

    /**
     * Returns the <CODE>SOAPBody</CODE> object associated with this <CODE>SOAPEnvelope</CODE>
     * object.
     *
     * <p>This SOAPBody will just be a container for all the BodyElements in the <CODE>OMMessage
     * </CODE>
     *
     * @return the <CODE>SOAPBody</CODE> object for this <CODE> SOAPEnvelope</CODE> object or <CODE>
     *     null</CODE> if there is none
     * @throws OMException if there is a problem obtaining the <CODE>SOAPBody</CODE> object
     */
    SOAPBody getBody() throws OMException;

    SOAPVersion getVersion();

    /**
     * Returns true if there is a SOAPFault in the body. The implementation may choose to get this
     * information by building the OM tree or use parser provided information.
     *
     * @return true if SOAPFault in the body
     */
    boolean hasFault();

    /**
     * Retrieves the OMNamespace of the first element in the body. The implementation might build
     * the OMElement or it may obtain this information from the builder/parser without building the
     * OMElement. Use this method in the situations where you need to know the OMNamespace, but
     * don't necessarily need the OMElement.
     *
     * @return the namespace of first element in the body, or <code>null</code> if the element has
     *     no namespace or the body is empty
     */
    public OMNamespace getSOAPBodyFirstElementNS();

    /**
     * Retrieves the local name of the first element in the body. The implementation might build the
     * OMElement or it may obtain this information from the builder/parser without building the
     * OMElement. Use this method in the situations where you need to know the name, but don't
     * necessarily need the OMElement.
     *
     * @return local name of first element in the body or null
     */
    public String getSOAPBodyFirstElementLocalName();
}
