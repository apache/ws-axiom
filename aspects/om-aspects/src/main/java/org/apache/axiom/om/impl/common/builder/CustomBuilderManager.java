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
package org.apache.axiom.om.impl.common.builder;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11HeaderBlock;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12HeaderBlock;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class CustomBuilderManager implements BuilderListener {
    private static final Log log = LogFactory.getLog(CustomBuilderManager.class);
    
    private final ArrayList<CustomBuilderRegistration> registrations = new ArrayList<CustomBuilderRegistration>();
    
    void register(CustomBuilder.Selector selector, CustomBuilder customBuilder) {
        registrations.add(new CustomBuilderRegistration(selector, customBuilder));
    }
    
    @Override
    public Runnable nodeAdded(CoreNode node, int depth) {
        if (node instanceof AxiomElement && (node instanceof AxiomSOAPHeaderBlock || !(node instanceof AxiomSOAPElement))) {
            final AxiomElement element = (AxiomElement)node;
            for (int i=0; i<registrations.size(); i++) {
                CustomBuilderRegistration registration = registrations.get(i);
                final String namespaceURI = element.coreGetNamespaceURI();
                final String localName = element.coreGetLocalName();
                if (registration.getSelector().accepts(element.getParent(), depth, namespaceURI, localName)) {
                    final CustomBuilder customBuilder = registration.getCustomBuilder();
                    if (log.isDebugEnabled()) {
                        log.debug("Custom builder " + customBuilder + " accepted element {" + namespaceURI + "}" + localName + " at depth " + depth);
                    }
                    return new Runnable() {
                        @Override
                        public void run() {
                            if (log.isDebugEnabled()) {
                                log.debug("Invoking custom builder " + customBuilder);
                            }
                            XMLStreamReader reader = element.getXMLStreamReader(false);
                            // Advance the reader to the START_ELEMENT event of the root element
                            try {
                                reader.next();
                            } catch (XMLStreamException ex) {
                                // We should never get here
                                throw new OMException(ex);
                            }
                            OMDataSource dataSource = customBuilder.create(reader);
                            try {
                                reader.close();
                            } catch (XMLStreamException ex) {
                                // We should never get here
                                throw new OMException(ex);
                            }
                            Class<? extends AxiomSourcedElement> type;
                            if (element instanceof AxiomSOAP11HeaderBlock) {
                                type = AxiomSOAP11HeaderBlock.class;
                            } else if (element instanceof AxiomSOAP12HeaderBlock) {
                                type = AxiomSOAP12HeaderBlock.class;
                            } else {
                                type = AxiomSourcedElement.class;
                            }
                            if (log.isDebugEnabled()) {
                                log.debug("Replacing element with new sourced element of type " + type);
                            }
                            AxiomSourcedElement newElement = element.coreCreateNode(type);
                            newElement.init(localName, new OMNamespaceImpl(namespaceURI, null), dataSource);
                            element.coreReplaceWith(newElement, AxiomSemantics.INSTANCE);
                        }
                    };
                }
            }
        }
        return null;
    }
}
