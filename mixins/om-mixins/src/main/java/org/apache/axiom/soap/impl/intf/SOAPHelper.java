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
package org.apache.axiom.soap.impl.intf;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.factory.AxiomElementType;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;

/**
 * Encapsulates certain SOAP version specific behaviors. This API defines methods that could also be
 * added to {@link SOAPVersion}, but that are not relevant for application code and should therefore
 * not be part of the public API.
 */
public interface SOAPHelper {
    SOAPVersion getVersion();

    SOAPFactory getSOAPFactory(OMMetaFactory metaFactory);

    String getEnvelopeURI();

    OMNamespace getNamespace();

    String getSpecName();

    AxiomElementType<? extends AxiomSOAPEnvelope> getEnvelopeType();

    AxiomElementType<? extends AxiomSOAPHeader> getHeaderType();

    QName getHeaderQName();

    AxiomElementType<? extends AxiomSOAPHeaderBlock> getHeaderBlockType();

    AxiomElementType<? extends AxiomSOAPBody> getBodyType();

    QName getBodyQName();

    AxiomElementType<? extends AxiomSOAPFault> getFaultType();

    QName getFaultQName();

    AxiomElementType<? extends AxiomSOAPFaultCode> getFaultCodeType();

    QName getFaultCodeQName();

    AxiomElementType<? extends AxiomSOAPFaultReason> getFaultReasonType();

    QName getFaultReasonQName();

    AxiomElementType<? extends AxiomSOAPFaultRole> getFaultRoleType();

    QName getFaultRoleQName();

    AxiomElementType<? extends AxiomSOAPFaultDetail> getFaultDetailType();

    QName getFaultDetailQName();

    QName getMustUnderstandAttributeQName();

    QName getRoleAttributeQName();

    QName getRelayAttributeQName();

    Boolean parseBoolean(String literal);

    String formatBoolean(boolean value);
}
