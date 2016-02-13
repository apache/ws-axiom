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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomCDATASection;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.stream.StreamException;
import org.apache.axiom.om.impl.stream.XmlHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class BuilderHandler implements XmlHandler {
    private static final Log log = LogFactory.getLog(BuilderHandler.class);
    
    private static final OMNamespace DEFAULT_NS = new OMNamespaceImpl("", "");
    
    private final NodeFactory nodeFactory;
    private final Model model;
    private final AxiomSourcedElement root;
    private final Builder builder;
    private final OMNamespaceCache nsCache = new OMNamespaceCache();
    public AxiomContainer target;
    // returns the state of completion
    public boolean done;
    // keeps the state of the cache
    public boolean cache = true;
    public AxiomDocument document;
    
    /**
     * Tracks the depth of the node identified by {@link #target}. By definition, the document has
     * depth 0. Note that if caching is disabled, then this depth may be different from the actual
     * depth reached by the underlying parser.
     */
    public int depth;
    
    /**
     * Stores the stack trace of the code that caused a node to be discarded or consumed. This is
     * only used if debug logging was enabled when builder was created.
     */
    public Map<CoreParentNode,Throwable> discardTracker = log.isDebugEnabled() ? new LinkedHashMap<CoreParentNode,Throwable>() : null;
    
    private ArrayList<BuilderListener> listeners;
    private Queue<Runnable> deferredListenerActions;

    public BuilderHandler(NodeFactory nodeFactory, Model model, AxiomSourcedElement root, Builder builder) {
        this.nodeFactory = nodeFactory;
        this.model = model;
        this.root = root;
        this.builder = builder;
    }

    public void addListener(BuilderListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<BuilderListener>();
        }
        listeners.add(listener);
    }
    
    private void nodeAdded(CoreNode node) {
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
    
    private void addChild(AxiomChildNode node) {
        target.addChild(node, true);
        nodeAdded(node);
    }
    
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) {
        if (root == null) {
            document = nodeFactory.createNode(model.getDocumentType());
            document.coreSetInputEncoding(inputEncoding);
            document.coreSetXmlVersion(xmlVersion);
            document.coreSetXmlEncoding(xmlEncoding);
            document.coreSetStandalone(standalone);
            document.coreSetBuilder(builder);
            nodeAdded(document);
            target = document;
        }
    }
    
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) {
        model.validateEventType(XMLStreamConstants.DTD);
        AxiomDocType node = nodeFactory.createNode(AxiomDocType.class);
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        addChild(node);
    }
    
    public void startElement(String namespaceURI, String localName, String prefix) {
        depth++;
        AxiomElement element;
        OMNamespace ns = nsCache.getOMNamespace(namespaceURI, prefix);
        if (depth == 1 && root != null) {
            root.validateName(prefix, localName, namespaceURI);
            root.initName(localName, ns, false);
            element = root;
        } else {
            element = nodeFactory.createNode(model.determineElementType(
                    target, depth, namespaceURI, localName));
            element.coreSetBuilder(builder);
            element.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
            element.initName(localName, ns, false);
            addChild(element);
        }
        target = element;
    }
    
    public void endElement() {
        depth--;
        target.setComplete(true);
        if (depth == 0) {
            // This is relevant for OMSourcedElements and for the case where the document has been discarded
            // using getDocumentElement(true). In these cases, this will actually set target to null. In all
            // other cases, this will have the same effect as the instruction in the else clause.
            target = document;
        } else {
            target = (AxiomContainer)((AxiomElement)target).getParent();
        }
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) {
        OMNamespace ns = nsCache.getOMNamespace(namespaceURI, prefix);
        AxiomAttribute attr = nodeFactory.createNode(AxiomAttribute.class);
        attr.internalSetLocalName(localName);
        attr.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
        attr.internalSetNamespace(ns);
        attr.coreSetType(type);
        attr.coreSetSpecified(specified);
        ((AxiomElement)target).coreAppendAttribute(attr);
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) {
        OMNamespace ns = nsCache.getOMNamespace(namespaceURI, prefix);
        if (ns == null) {
            ns = DEFAULT_NS;
        }
        AxiomNamespaceDeclaration decl = nodeFactory.createNode(AxiomNamespaceDeclaration.class);
        decl.setDeclaredNamespace(ns);
        ((AxiomElement)target).coreAppendAttribute(decl);
    }
    
    public void attributesCompleted() {
        target.coreSetState(CoreParentNode.INCOMPLETE);
    }
    
    public void processCharacterData(Object data, boolean ignorable) {
        AxiomCharacterDataNode node = nodeFactory.createNode(AxiomCharacterDataNode.class);
        node.coreSetCharacterData(data);
        node.coreSetIgnorable(ignorable);
        addChild(node);
    }
    
    public void processProcessingInstruction(String piTarget, String piData) {
        model.validateEventType(XMLStreamConstants.PROCESSING_INSTRUCTION);
        AxiomProcessingInstruction node = nodeFactory.createNode(AxiomProcessingInstruction.class);
        node.coreSetTarget(piTarget);
        node.coreSetCharacterData(piData, AxiomSemantics.INSTANCE);
        addChild(node);
    }

    public void processComment(String content) {
        model.validateEventType(XMLStreamConstants.COMMENT);
        AxiomComment node = nodeFactory.createNode(AxiomComment.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
    
    public void processCDATASection(String content) {
        model.validateEventType(XMLStreamConstants.CDATA);
        AxiomCDATASection node = nodeFactory.createNode(AxiomCDATASection.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
    
    public void processEntityReference(String name, String replacementText) {
        model.validateEventType(XMLStreamConstants.ENTITY_REFERENCE);
        AxiomEntityReference node = nodeFactory.createNode(AxiomEntityReference.class);
        node.coreSetName(name);
        node.coreSetReplacementText(replacementText);
        addChild(node);
    }
    
    public void endDocument() {
        if (depth != 0) {
            throw new IllegalStateException();
        }
        if (document != null) {
            document.setComplete(true);
        }
        target = null;
        done = true;
    }

    @Override
    public void processOMDataSource(String namespaceURI, String localName, OMDataSource dataSource) throws StreamException {
        Class<? extends AxiomElement> elementType = model.determineElementType(target, depth+1, namespaceURI, localName);
        Class<? extends AxiomSourcedElement> sourcedElementType;
        if (elementType == AxiomElement.class) {
            sourcedElementType = AxiomSourcedElement.class;
        } else if (AxiomSourcedElement.class.isAssignableFrom(elementType)) {
            sourcedElementType = elementType.asSubclass(AxiomSourcedElement.class);
        } else {
            throw new OMException("Cannot build an OMSourcedElement where a " + elementType.getName() + " is expected");
        }
        AxiomSourcedElement element = nodeFactory.createNode(sourcedElementType);
        element.init(localName, new OMNamespaceImpl(namespaceURI, null), dataSource);
        addChild(element);
    }
}
