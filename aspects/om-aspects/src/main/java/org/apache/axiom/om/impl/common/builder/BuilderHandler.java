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
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public final class BuilderHandler implements XmlHandler {
    final NodeFactory nodeFactory;
    final Model model;
    final AxiomSourcedElement root;
    final Builder builder;
    final OMNamespaceCache nsCache = new OMNamespaceCache();
    public Context context;
    private int activeContextCount;
    // returns the state of completion
    public boolean done;
    public AxiomDocument document;
    
    /**
     * Tracks the depth of the node identified by {@link #target}. By definition, the document has
     * depth 0. Note that if caching is disabled, then this depth may be different from the actual
     * depth reached by the underlying parser.
     */
    public int depth;
    
    private ArrayList<BuilderListener> listeners;
    private Queue<Runnable> deferredListenerActions;

    public BuilderHandler(NodeFactory nodeFactory, Model model, AxiomSourcedElement root, Builder builder) {
        this.nodeFactory = nodeFactory;
        this.model = model;
        this.root = root;
        this.builder = builder;
        context = new Context(this, null, 0);
        activeContextCount = 1;
    }

    public void addListener(BuilderListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<BuilderListener>();
        }
        listeners.add(listener);
    }
    
    void nodeAdded(CoreNode node) {
        if (listeners != null) {
            for (int i=0, size=listeners.size(); i<size; i++) {
                Runnable action = listeners.get(i).nodeAdded(node, depth);
                if (action != null) {
                    if (deferredListenerActions == null) {
                        deferredListenerActions = new LinkedList<Runnable>();
                    }
                    deferredListenerActions.add(action);
                }
            }
        }
    }

    void executeDeferredListenerActions() {
        if (deferredListenerActions != null) {
            Runnable action;
            while ((action = deferredListenerActions.poll()) != null) {
                action.run();
            }
        }
    }
    
    void incrementActiveContextCount() {
        activeContextCount++;
    }
    
    void decrementActiveContextCount() {
        if (--activeContextCount == 0) {
            builder.close();
        }
    }
    
    public boolean isCompleted() {
        return done;
    }
    
    public AxiomDocument getDocument() {
        if (root != null) {
            throw new UnsupportedOperationException("There is no document linked to this builder");
        } else {
            return document;
        }
    }
    
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) {
        if (root == null) {
            document = nodeFactory.createNode(model.getDocumentType());
            document.coreSetInputEncoding(inputEncoding);
            document.coreSetXmlVersion(xmlVersion);
            document.coreSetXmlEncoding(xmlEncoding);
            document.coreSetStandalone(standalone);
            document.coreSetInputContext(context);
            nodeAdded(document);
            context.target = document;
        }
    }
    
    @Override
    public void startFragment() throws StreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException {
        model.validateEventType(XMLStreamConstants.DTD);
        context.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
    }
    
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        depth++;
        context = context.startElement(namespaceURI, localName, prefix);
    }
    
    public void endElement() throws StreamException {
        context = context.endElement();
        depth--;
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        context.processAttribute(namespaceURI, localName, prefix, value, type, specified);
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        context.processNamespaceDeclaration(prefix, namespaceURI);
    }
    
    public void attributesCompleted() throws StreamException {
        context.attributesCompleted();
    }
    
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        context.processCharacterData(data, ignorable);
    }
    
    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        model.validateEventType(XMLStreamConstants.PROCESSING_INSTRUCTION);
        context = context.startProcessingInstruction(target);
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        context = context.endProcessingInstruction();
    }

    @Override
    public void startComment() throws StreamException {
        model.validateEventType(XMLStreamConstants.COMMENT);
        context = context.startComment();
    }

    @Override
    public void endComment() throws StreamException {
        context = context.endComment();
    }
    
    @Override
    public void startCDATASection() throws StreamException {
        model.validateEventType(XMLStreamConstants.CDATA);
        context = context.startCDATASection();
    }

    @Override
    public void endCDATASection() throws StreamException {
        context = context.endCDATASection();
    }
    
    public void processEntityReference(String name, String replacementText) throws StreamException {
        model.validateEventType(XMLStreamConstants.ENTITY_REFERENCE);
        context.processEntityReference(name, replacementText);
    }
    
    public void completed() throws StreamException {
        if (depth != 0) {
            throw new IllegalStateException();
        }
        context.endDocument();
        context = null;
        done = true;
    }
}
