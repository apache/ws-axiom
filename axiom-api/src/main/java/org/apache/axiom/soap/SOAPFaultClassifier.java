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

import org.apache.axiom.om.OMElement;

/**
 * Common superinterface for {@link SOAPFaultCode} and {@link SOAPFaultSubCode}. This interface
 * enables manipulation of SOAP fault codes and SOAP fault sub codes using common code.
 */
public interface SOAPFaultClassifier extends OMElement {

    /**
     * Fault SubCode contain only one mandatory Value child. This value child contains a QName
     *
     * @param value
     */
    void setValue(SOAPFaultValue value) throws SOAPProcessingException;

    /**
     * Get the {@link SOAPFaultValue} for this fault code or subcode. Note that for SOAP 1.1, the
     * return value will always be <code>null</code>. Use {@link #getValueAsQName()} as a SOAP
     * version independent way to extract the value of a fault code or subcode.
     *
     * @return the {@link SOAPFaultValue} object or <code>null</code> if there is no {@link
     *     SOAPFaultValue}
     */
    SOAPFaultValue getValue();

    /**
     * Set the value of this fault code or subcode. The effect of this method depends on the SOAP
     * version. For SOAP 1.1, the method sets the text content of the {@link SOAPFaultCode} element.
     * For SOAP 1.2, the method ensures that the {@link SOAPFaultCode} or {@link SOAPFaultSubCode}
     * element has a child of type {@link SOAPFaultValue} child (creating one if necessary) and sets
     * the text content of that child. In both cases, the method adds an appropriate namespace
     * declaration if necessary.
     *
     * @param value the QName for the fault code or subcode value
     */
    void setValue(QName value);

    /**
     * Get the value of this fault code or subcode. This method can be consistently used for all
     * SOAP versions. For SOAP 1.1, it gets the text content of this element (which is necessarily a
     * {@link SOAPFaultCode}) and resolves it as a {@link QName}. For SOAP 1.2, it locates the
     * {@link SOAPFaultValue} child, extracts the text content from that element and resolves it as
     * a {@link QName}.
     *
     * <p>The method returns <code>null</code> if it fails to extract the value. Note that invalid
     * SOAP faults are very common (especially with SOAP 1.1). Therefore the caller must be prepared
     * to get a <code>null</code> value.
     *
     * @return the QName for the fault code or subcode value, or <code>null</code> if the value
     *     could not be determined
     */
    QName getValueAsQName();

    /**
     * Fault SubCode can contain an optional SubCode
     *
     * @param subCode
     */
    void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException;

    /**
     * @return Returns SOAPFaultSubCode.
     */
    SOAPFaultSubCode getSubCode();
}
