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
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPProcessingException;

import java.util.Vector;

public class SOAP12BuilderHelper extends SOAPBuilderHelper {
    private boolean codePresent = false;
    private boolean reasonPresent = false;
    private boolean nodePresent = false;
    private boolean rolePresent = false;
    private boolean detailPresent = false;
    private boolean subcodeValuePresent = false;
    private boolean subSubcodePresent = false;
    private boolean valuePresent = false;
    private boolean subcodePresent = false;
    private boolean codeprocessing = false;
    private boolean subCodeProcessing = false;
    private boolean reasonProcessing = false;
    private boolean processingDetailElements = false;
    private Vector<String> detailElementNames;

    @Override
    public AxiomElementType<? extends AxiomElement> handleEvent(
            OMElement parent, int elementLevel, String namespaceURI, String localName)
            throws SOAPProcessingException {
        AxiomElementType<? extends AxiomElement> elementType = null;

        if (elementLevel == 4) {
            if (localName.equals(SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME)) {
                if (codePresent) {
                    throw new SOAPProcessingException("Multiple Code element encountered");
                } else {
                    elementType = AxiomNodeFactory::createSOAP12FaultCode;
                    codePresent = true;
                    codeprocessing = true;
                }
            } else if (localName.equals(SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME)) {
                if (!codeprocessing && !subCodeProcessing) {
                    if (codePresent) {
                        if (reasonPresent) {
                            throw new SOAPProcessingException(
                                    "Multiple Reason Element encountered");
                        } else {
                            elementType = AxiomNodeFactory::createSOAP12FaultReason;
                            reasonPresent = true;
                            reasonProcessing = true;
                        }
                    } else {
                        throw new SOAPProcessingException(
                                "Wrong element order encountred at " + localName);
                    }
                } else {
                    if (codeprocessing) {
                        throw new SOAPProcessingException("Code doesn't have a value");
                    } else {
                        throw new SOAPProcessingException("A subcode doesn't have a Value");
                    }
                }

            } else if (localName.equals(SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME)) {
                if (!reasonProcessing) {
                    if (reasonPresent && !rolePresent && !detailPresent) {
                        if (nodePresent) {
                            throw new SOAPProcessingException("Multiple Node element encountered");
                        } else {
                            elementType = AxiomNodeFactory::createSOAP12FaultNode;
                            nodePresent = true;
                        }
                    } else {
                        throw new SOAPProcessingException(
                                "wrong element order encountered at " + localName);
                    }
                } else {
                    throw new SOAPProcessingException("Reason element Should have a text");
                }
            } else if (localName.equals(SOAP12Constants.SOAP_FAULT_ROLE_LOCAL_NAME)) {
                if (!reasonProcessing) {
                    if (reasonPresent && !detailPresent) {
                        if (rolePresent) {
                            throw new SOAPProcessingException("Multiple Role element encountered");
                        } else {
                            elementType = AxiomNodeFactory::createSOAP12FaultRole;
                            rolePresent = true;
                        }
                    } else {
                        throw new SOAPProcessingException(
                                "Wrong element order encountered at " + localName);
                    }
                } else {
                    throw new SOAPProcessingException("Reason element should have a text");
                }
            } else if (localName.equals(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME)) {
                if (!reasonProcessing) {
                    if (reasonPresent) {
                        if (detailPresent) {
                            throw new SOAPProcessingException(
                                    "Multiple detail element encountered");
                        } else {
                            elementType = AxiomNodeFactory::createSOAP12FaultDetail;
                            detailPresent = true;
                        }
                    } else {
                        throw new SOAPProcessingException(
                                "wrong element order encountered at " + localName);
                    }
                } else {
                    throw new SOAPProcessingException("Reason element should have a text");
                }
            } else {
                throw new SOAPProcessingException(
                        localName + " unsupported element in SOAPFault element");
            }

        } else if (elementLevel == 5) {
            if (parent.getLocalName().equals(SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME)) {
                if (localName.equals(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME)) {
                    if (!valuePresent) {
                        elementType = AxiomNodeFactory::createSOAP12FaultValue;
                        valuePresent = true;
                        codeprocessing = false;
                    } else {
                        throw new SOAPProcessingException(
                                "Multiple value Encountered in code element");
                    }

                } else if (localName.equals(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME)) {
                    if (!subcodePresent) {
                        if (valuePresent) {
                            elementType = AxiomNodeFactory::createSOAP12FaultSubCode;
                            subcodePresent = true;
                            subCodeProcessing = true;
                        } else {
                            throw new SOAPProcessingException(
                                    "Value should present before the subcode");
                        }

                    } else {
                        throw new SOAPProcessingException(
                                "multiple subcode Encountered in code element");
                    }
                } else {
                    throw new SOAPProcessingException(
                            localName + " is not supported inside the code element");
                }

            } else if (parent.getLocalName().equals(SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME)) {
                if (localName.equals(SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME)) {
                    elementType = AxiomNodeFactory::createSOAP12FaultText;
                    reasonProcessing = false;
                } else {
                    throw new SOAPProcessingException(
                            localName + " is not supported inside the reason");
                }
            } else if (parent.getLocalName().equals(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME)) {
                elementType = AxiomNodeFactory::createNSAwareElement;
                processingDetailElements = true;
                detailElementNames = new Vector<String>();
                detailElementNames.add(localName);

            } else {
                throw new SOAPProcessingException(
                        parent.getLocalName() + " should not have child element");
            }

        } else if (elementLevel > 5) {
            if (parent.getLocalName().equals(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME)) {
                if (localName.equals(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME)) {
                    if (subcodeValuePresent) {
                        throw new SOAPProcessingException("multiple subCode value encountered");
                    } else {
                        elementType = AxiomNodeFactory::createSOAP12FaultValue;
                        subcodeValuePresent = true;
                        subSubcodePresent = false;
                        subCodeProcessing = false;
                    }
                } else if (localName.equals(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME)) {
                    if (subcodeValuePresent) {
                        if (!subSubcodePresent) {
                            elementType = AxiomNodeFactory::createSOAP12FaultSubCode;
                            subcodeValuePresent = false;
                            subSubcodePresent = true;
                            subCodeProcessing = true;
                        } else {
                            throw new SOAPProcessingException("multiple subcode encountered");
                        }
                    } else {
                        throw new SOAPProcessingException(
                                "Value should present before the subcode");
                    }
                } else {
                    throw new SOAPProcessingException(
                            localName + " is not supported inside the subCode element");
                }
            } else if (processingDetailElements) {
                int detailElementLevel = 0;
                boolean localNameExist = false;
                for (int i = 0; i < detailElementNames.size(); i++) {
                    if (parent.getLocalName().equals(detailElementNames.get(i))) {
                        localNameExist = true;
                        detailElementLevel = i + 1;
                    }
                }
                if (localNameExist) {
                    detailElementNames.setSize(detailElementLevel);
                    elementType = AxiomNodeFactory::createNSAwareElement;
                    detailElementNames.add(localName);
                }

            } else {
                throw new SOAPProcessingException(
                        parent.getLocalName()
                                + " should not have child at element level "
                                + elementLevel);
            }
        }
        return elementType;
    }
}
