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

package org.apache.axiom.soap.impl.common.builder;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.factory.AxiomElementType;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPProcessingException;
import org.w3c.dom.Element;

public class SOAP11BuilderHelper extends SOAPBuilderHelper implements SOAP11Constants {
    private boolean faultcodePresent = false;
    private boolean faultstringPresent = false;

    @Override
    public AxiomElementType<? extends AxiomElement> handleEvent(OMElement parent, int elementLevel,
            String namespaceURI, String localName) throws SOAPProcessingException {
        AxiomElementType<? extends AxiomElement> elementType = null;

        if (elementLevel == 4) {

            if (SOAP_FAULT_CODE_LOCAL_NAME.equals(localName)) {

                elementType = AxiomNodeFactory::createSOAP11FaultCode;
                faultcodePresent = true;
            } else if (SOAP_FAULT_STRING_LOCAL_NAME.equals(localName)) {

                elementType = AxiomNodeFactory::createSOAP11FaultReason;
                faultstringPresent = true;
            } else if (SOAP_FAULT_ACTOR_LOCAL_NAME.equals(localName)) {
                elementType = AxiomNodeFactory::createSOAP11FaultRole;
            } else if (SOAP_FAULT_DETAIL_LOCAL_NAME.equals(localName)) {
                elementType = AxiomNodeFactory::createSOAP11FaultDetail;
            } else {
                elementType = AxiomNodeFactory::createNSAwareElement;
            }

        } else if (elementLevel == 5) {

            String parentTagName = "";
            if (parent instanceof Element) {
                parentTagName = ((Element) parent).getTagName();
            } else {
                parentTagName = parent.getLocalName();
            }

            if (parentTagName.equals(SOAP_FAULT_CODE_LOCAL_NAME)) {
                throw new SOAPProcessingException(
                        "faultcode element should not have children");
            } else if (parentTagName.equals(
                    SOAP_FAULT_STRING_LOCAL_NAME)) {
                throw new SOAPProcessingException(
                        "faultstring element should not have children");
            } else if (parentTagName.equals(
                    SOAP_FAULT_ACTOR_LOCAL_NAME)) {
                throw new SOAPProcessingException(
                        "faultactor element should not have children");
            } else {
                elementType = AxiomNodeFactory::createNSAwareElement;
            }

        } else if (elementLevel > 5) {
            elementType = AxiomNodeFactory::createNSAwareElement;
        }

        return elementType;
    }
}