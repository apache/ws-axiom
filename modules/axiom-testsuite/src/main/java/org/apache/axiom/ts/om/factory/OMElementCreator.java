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
package org.apache.axiom.ts.om.factory;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public abstract class OMElementCreator {
    public static final OMElementCreator[] INSTANCES = new OMElementCreator[] {
        new OMElementCreator("QName", false) {
            public OMElement createOMElement(OMFactory factory, String localName,
                    String namespaceURI, String prefix) {
                if (prefix == null) {
                    prefix = "";
                }
                return factory.createOMElement(new QName(namespaceURI, localName, prefix));
            }
        },
        new OMElementCreator("QName,OMContainer", false) {
            public OMElement createOMElement(OMFactory factory, String localName,
                    String namespaceURI, String prefix) {
                if (prefix == null) {
                    prefix = "";
                }
                return factory.createOMElement(new QName(namespaceURI, localName, prefix), null);
            }
        },
        new OMElementCreator("String,OMNamespace", true) {
            public OMElement createOMElement(OMFactory factory, String localName,
                    String namespaceURI, String prefix) {
                return factory.createOMElement(localName,
                        getOMNamespace(factory, namespaceURI, prefix));
            }
        },
        new OMElementCreator("String,OMNamespace,OMContainer", true) {
            public OMElement createOMElement(OMFactory factory, String localName,
                    String namespaceURI, String prefix) {
                return factory.createOMElement(localName,
                        getOMNamespace(factory, namespaceURI, prefix), null);
            }
        },
        new OMElementCreator("String,String,String", true) {
            public OMElement createOMElement(OMFactory factory, String localName,
                    String namespaceURI, String prefix) {
                return factory.createOMElement(localName, namespaceURI, prefix);
            }
        },
    };
    
    private final String name;
    private final boolean supportsDefaultNamespace;
    
    public OMElementCreator(String name, boolean supportsDefaultNamespace) {
        this.name = name;
        this.supportsDefaultNamespace = supportsDefaultNamespace;
    }

    public final String getName() {
        return name;
    }

    public final boolean isSupportsDefaultNamespace() {
        return supportsDefaultNamespace;
    }

    public abstract OMElement createOMElement(OMFactory factory, String localName,
            String namespaceURI, String prefix);
    
    static OMNamespace getOMNamespace(OMFactory factory, String namespaceURI, String prefix) {
        if (prefix == null) {
            return factory.createOMNamespace(namespaceURI, null);
        } else if (prefix.length() == 0 && namespaceURI.length() == 0) {
            return null;
        } else {
            return factory.createOMNamespace(namespaceURI, prefix);
        }
    }
}
