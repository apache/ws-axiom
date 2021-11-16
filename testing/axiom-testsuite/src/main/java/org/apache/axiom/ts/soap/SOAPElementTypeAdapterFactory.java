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
package org.apache.axiom.ts.soap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultClassifier;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.testing.multiton.AdapterFactory;
import org.apache.axiom.testing.multiton.Adapters;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter.Getter;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter.Setter;

import com.google.auto.service.AutoService;

@AutoService(AdapterFactory.class)
public class SOAPElementTypeAdapterFactory implements AdapterFactory<SOAPElementType> {
    @Override
    public void createAdapters(SOAPElementType type, Adapters adapters) {
        if (type == SOAPElementType.ENVELOPE) {
            adapters.add(new SOAPElementTypeAdapter(SOAPEnvelope.class, null, null) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPEnvelope();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    throw new UnsupportedOperationException();
                }
            });
        } else if (type == SOAPElementType.HEADER) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPEnvelope)parent).getHeader();
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPHeader.class, getter, null) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPHeader();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPHeader((SOAPEnvelope)parent);
                }
            });
        } else if (type == SOAPElementType.BODY) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPEnvelope)parent).getBody();
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPBody.class, getter, null) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPBody();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPBody((SOAPEnvelope)parent);
                }
            });
        } else if (type == SOAPElementType.FAULT) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPBody)parent).getFault();
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFault.class, getter, null) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFault();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFault((SOAPBody)parent);
                }
            });
        } else if (type == SOAPFaultChild.CODE) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFault)parent).getCode();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFault)parent).setCode((SOAPFaultCode)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultCode.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultCode();
                }
                
                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultCode((SOAPFault)parent);
                }
            });
        } else if (type == SOAPElementType.VALUE) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFaultClassifier)parent).getValue();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFaultClassifier)parent).setValue((SOAPFaultValue)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultValue.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultValue();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    if (parentType == SOAPFaultChild.CODE) {
                        return factory.createSOAPFaultValue((SOAPFaultCode)parent);
                    } else {
                        return factory.createSOAPFaultValue((SOAPFaultSubCode)parent);
                    }
                }
            });
        } else if (type == SOAPElementType.SUB_CODE) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFaultClassifier)parent).getSubCode();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFaultClassifier)parent).setSubCode((SOAPFaultSubCode)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultSubCode.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultSubCode();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    if (parentType == SOAPFaultChild.CODE) {
                        return factory.createSOAPFaultSubCode((SOAPFaultCode)parent);
                    } else {
                        return factory.createSOAPFaultSubCode((SOAPFaultSubCode)parent);
                    }
                }
            });
        } else if (type == SOAPFaultChild.REASON) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFault)parent).getReason();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFault)parent).setReason((SOAPFaultReason)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultReason.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultReason();
                }
                
                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultReason((SOAPFault)parent);
                }
            });
        } else if (type == SOAPElementType.TEXT) {
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultText.class, null, null) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultText();
                }

                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultText((SOAPFaultReason)parent);
                }
            });
        } else if (type == SOAPFaultChild.NODE) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFault)parent).getNode();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFault)parent).setNode((SOAPFaultNode)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultNode.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultNode();
                }
                
                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultNode((SOAPFault)parent);
                }
            });
        } else if (type == SOAPFaultChild.ROLE) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFault)parent).getRole();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFault)parent).setRole((SOAPFaultRole)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultRole.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultRole();
                }
                
                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultRole((SOAPFault)parent);
                }
            });
        } else if (type == SOAPFaultChild.DETAIL) {
            Getter getter = new Getter() {
                @Override
                public OMElement invoke(OMElement parent) {
                    return ((SOAPFault)parent).getDetail();
                }
            };
            Setter setter = new Setter() {
                @Override
                public void invoke(OMElement parent, OMElement child) {
                    ((SOAPFault)parent).setDetail((SOAPFaultDetail)child);
                }
            };
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultDetail.class, getter, setter) {
                @Override
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultDetail();
                }
                
                @Override
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultDetail((SOAPFault)parent);
                }
            });
        }
    }
}
