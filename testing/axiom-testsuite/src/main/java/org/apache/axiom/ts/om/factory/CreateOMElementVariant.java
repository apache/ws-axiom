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

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public abstract class CreateOMElementVariant {
    public static final CreateOMElementVariant[] INSTANCES = {
        new CreateOMElementVariant("QName", false, false) {
            @Override
            public OMElement createOMElement(
                    OMFactory factory,
                    OMContainer parent,
                    String localName,
                    String namespaceURI,
                    String prefix) {
                if (prefix == null) {
                    prefix = "";
                }
                return factory.createOMElement(new QName(namespaceURI, localName, prefix));
            }
        },
        new CreateOMElementVariant("QName,OMContainer", false, true) {
            @Override
            public OMElement createOMElement(
                    OMFactory factory,
                    OMContainer parent,
                    String localName,
                    String namespaceURI,
                    String prefix) {
                if (prefix == null) {
                    prefix = "";
                }
                return factory.createOMElement(new QName(namespaceURI, localName, prefix), parent);
            }
        },
        new CreateOMElementVariant("String,OMNamespace", true, false) {
            @Override
            public OMElement createOMElement(
                    OMFactory factory,
                    OMContainer parent,
                    String localName,
                    String namespaceURI,
                    String prefix) {
                return factory.createOMElement(
                        localName, getOMNamespace(factory, namespaceURI, prefix));
            }
        },
        new CreateOMElementVariant("String,OMNamespace,OMContainer", true, true) {
            @Override
            public OMElement createOMElement(
                    OMFactory factory,
                    OMContainer parent,
                    String localName,
                    String namespaceURI,
                    String prefix) {
                return factory.createOMElement(
                        localName, getOMNamespace(factory, namespaceURI, prefix), parent);
            }
        },
        new CreateOMElementVariant("String,String,String", true, false) {
            @Override
            public OMElement createOMElement(
                    OMFactory factory,
                    OMContainer parent,
                    String localName,
                    String namespaceURI,
                    String prefix) {
                return factory.createOMElement(localName, namespaceURI, prefix);
            }
        },
    };

    private final String name;
    private final boolean supportsDefaultNamespace;
    private final boolean supportsContainer;

    public CreateOMElementVariant(
            String name, boolean supportsDefaultNamespace, boolean supportsContainer) {
        this.name = name;
        this.supportsDefaultNamespace = supportsDefaultNamespace;
        this.supportsContainer = supportsContainer;
    }

    public final String getName() {
        return name;
    }

    /**
     * Determines whether this strategy can be used to create an {@link OMElement} in the default
     * namespace, i.e. with an empty prefix.
     *
     * @return <code>true</code> if the strategy supports default namespaces, <code>false</code>
     *     otherwise
     */
    public final boolean isSupportsDefaultNamespace() {
        return supportsDefaultNamespace;
    }

    /**
     * Determines whether this strategy can be used to create an {@link OMElement} as a child of
     * another container.
     *
     * @return <code>true</code> if a {@link OMContainer} object can be passed to {@link
     *     #createOMElement(OMFactory, OMContainer, String, String, String)}
     */
    public final boolean isSupportsContainer() {
        return supportsContainer;
    }

    /**
     * Create an {@link OMElement}.
     *
     * @param factory the factory used to create the element
     * @param parent the parent of the element to be created or <code>null</code> to create an
     *     orphaned element; this parameter can only be used if {@link #isSupportsContainer()}
     *     returns <code>true</code>
     * @param localName the local name of the element
     * @param namespaceURI the namespace URI of the element
     * @param prefix the prefix of the element, the empty string if the element is to be created in
     *     the default namespace (only supported if {@link #isSupportsDefaultNamespace()} returns
     *     <code>true</code>), or <code>null</code> if a prefix should be generated or chosen based
     *     on the namespace context of the parent
     * @return the created element
     */
    public abstract OMElement createOMElement(
            OMFactory factory,
            OMContainer parent,
            String localName,
            String namespaceURI,
            String prefix);

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
