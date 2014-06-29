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
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;

public class SOAPElementTypeAdapterFactory implements AdapterFactory<SOAPElementType> {
    public void createAdapters(SOAPElementType type, Adapters adapters) {
        if (type == SOAPElementType.ENVELOPE) {
            adapters.add(new SOAPElementTypeAdapter(SOAPEnvelope.class) {
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
            adapters.add(new SOAPElementTypeAdapter(SOAPHeader.class) {
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
            adapters.add(new SOAPElementTypeAdapter(SOAPBody.class) {
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
            adapters.add(new SOAPElementTypeAdapter(SOAPFault.class) {
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
            adapters.add(new SOAPFaultChildAdapter(SOAPFaultCode.class) {
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultCode();
                }
                
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultCode((SOAPFault)parent);
                }
                
                @Override
                public OMElement get(SOAPFault fault) {
                    return fault.getCode();
                }

                public void set(SOAPFault fault, OMElement element) {
                    fault.setCode((SOAPFaultCode)element);
                }
            });
        } else if (type == SOAPElementType.VALUE) {
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultValue.class) {
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
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultSubCode.class) {
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
            adapters.add(new SOAPFaultChildAdapter(SOAPFaultReason.class) {
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultReason();
                }
                
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultReason((SOAPFault)parent);
                }
                
                @Override
                public OMElement get(SOAPFault fault) {
                    return fault.getReason();
                }

                public void set(SOAPFault fault, OMElement element) {
                    fault.setReason((SOAPFaultReason)element);
                }
            });
        } else if (type == SOAPElementType.TEXT) {
            adapters.add(new SOAPElementTypeAdapter(SOAPFaultText.class) {
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
            adapters.add(new SOAPFaultChildAdapter(SOAPFaultNode.class) {
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultNode();
                }
                
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultNode((SOAPFault)parent);
                }
                
                @Override
                public OMElement get(SOAPFault fault) {
                    return fault.getNode();
                }

                public void set(SOAPFault fault, OMElement element) {
                    fault.setNode((SOAPFaultNode)element);
                }
            });
        } else if (type == SOAPFaultChild.ROLE) {
            adapters.add(new SOAPFaultChildAdapter(SOAPFaultRole.class) {
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultRole();
                }
                
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultRole((SOAPFault)parent);
                }
                
                @Override
                public OMElement get(SOAPFault fault) {
                    return fault.getRole();
                }

                public void set(SOAPFault fault, OMElement element) {
                    fault.setRole((SOAPFaultRole)element);
                }
            });
        } else if (type == SOAPFaultChild.DETAIL) {
            adapters.add(new SOAPFaultChildAdapter(SOAPFaultDetail.class) {
                public OMElement create(SOAPFactory factory) {
                    return factory.createSOAPFaultDetail();
                }
                
                public OMElement create(SOAPFactory factory, SOAPElementType parentType,
                        OMElement parent) {
                    return factory.createSOAPFaultDetail((SOAPFault)parent);
                }
                
                @Override
                public OMElement get(SOAPFault fault) {
                    return fault.getDetail();
                }

                public void set(SOAPFault fault, OMElement element) {
                    fault.setDetail((SOAPFaultDetail)element);
                }
            });
        }
    }
}
