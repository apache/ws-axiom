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

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreCDATASection;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.InputContext;
import org.apache.axiom.core.stream.NullXmlHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;

public final class Context implements InputContext {
    private static final OMNamespace DEFAULT_NS = new OMNamespaceImpl("", "");
    
    private final BuilderHandler builderHandler;
    final Context parentContext;
    private final int depth;
    private Context nestedContext;

    public CoreParentNode target;
    
    private Object pendingCharacterData;
    
    /**
     * The {@link XmlHandler} object to send events to if pass-through is enabled. See
     * {@link InputContext#setPassThroughHandler(XmlHandler)} for more details.
     */
    private XmlHandler passThroughHandler;
    
    /**
     * Tracks the nesting depth when pass-through is enabled.
     */
    private int passThroughDepth;

    public Context(BuilderHandler builderHandler, Context parentContext, int depth) {
        this.builderHandler = builderHandler;
        this.parentContext = parentContext;
        this.depth = depth;
    }

    @Override
    public Builder getBuilder() {
        return builderHandler.builder;
    }

    public void setPassThroughHandler(XmlHandler passThroughHandler) {
        if (this.passThroughHandler != null) {
            throw new IllegalStateException("A pass-through handler has already been set for this context");
        }
        target.coreSetState(CoreParentNode.DISCARDING);
        this.passThroughHandler = passThroughHandler;
        if (passThroughHandler == NullXmlHandler.INSTANCE) {
            builderHandler.decrementActiveContextCount();
        }
    }
    
    @Override
    public void discard() {
        target.coreSetState(CoreParentNode.DISCARDING);
        passThroughHandler = NullXmlHandler.INSTANCE;
        builderHandler.decrementActiveContextCount();
    }

    private Context newContext(CoreParentNode target) {
        if (nestedContext == null) {
            nestedContext = new Context(builderHandler, this, depth+1);
        }
        nestedContext.target = target;
        target.coreSetInputContext(nestedContext);
        builderHandler.incrementActiveContextCount();
        return nestedContext;
    }
    
    private Context endContext() {
        target.coreSetState(CoreParentNode.COMPLETE);
        target.coreSetInputContext(null);
        if (pendingCharacterData != null) {
            target.coreSetCharacterData(pendingCharacterData, null);
            pendingCharacterData = null;
        }
        target = null;
        builderHandler.decrementActiveContextCount();
        return parentContext;
    }
    
    private Context decrementPassThroughDepth() {
        if (passThroughDepth == 0) {
            if (passThroughHandler != NullXmlHandler.INSTANCE) {
                builderHandler.decrementActiveContextCount();
            }
            // TODO: handle this in a better way
            boolean updateState = target.getState() == CoreParentNode.DISCARDING;
            target.coreSetInputContext(null);
            if (updateState) {
                target.coreSetState(CoreParentNode.DISCARDED);
            }
            passThroughHandler = null;
            target = null;
            return parentContext;
        } else {
            passThroughDepth--;
            return this;
        }
    }
    
    private void addChild(CoreChildNode node) {
        if (pendingCharacterData != null) {
            AxiomCharacterDataNode cdataNode = builderHandler.nodeFactory.createNode(AxiomCharacterDataNode.class);
            cdataNode.coreSetCharacterData(pendingCharacterData);
            target.internalAppendChildWithoutBuild(cdataNode);
            pendingCharacterData = null;
        }
        target.internalAppendChildWithoutBuild(node);
        if (!(node instanceof CoreCharacterDataNode)) {
            builderHandler.nodeAdded(node);
        }
    }
    
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
        } else {
            AxiomDocType node = builderHandler.nodeFactory.createNode(AxiomDocType.class);
            node.coreSetRootName(rootName);
            node.coreSetPublicId(publicId);
            node.coreSetSystemId(systemId);
            node.coreSetInternalSubset(internalSubset);
            addChild(node);
        }
    }
    
    public Context startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startElement(namespaceURI, localName, prefix);
            return this;
        } else {
            AxiomElement element;
            OMNamespace ns = builderHandler.nsCache.getOMNamespace(namespaceURI, prefix);
            if (depth == 0 && builderHandler.root != null) {
                builderHandler.root.validateName(prefix, localName, namespaceURI);
                builderHandler.root.initName(localName, ns, false);
                builderHandler.root.coreSetInputContext(this);
                builderHandler.root.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
                element = builderHandler.root;
            } else {
                element = builderHandler.nodeFactory.createNode(builderHandler.model.determineElementType(
                        (AxiomContainer)target, depth+1, namespaceURI, localName));
                element.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
                element.initName(localName, ns, false);
                addChild(element);
            }
            return newContext(element);
        }
    }
    
    public Context endElement() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endElement();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        } else {
            OMNamespace ns = builderHandler.nsCache.getOMNamespace(namespaceURI, prefix);
            AxiomAttribute attr = builderHandler.nodeFactory.createNode(AxiomAttribute.class);
            attr.internalSetLocalName(localName);
            attr.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
            attr.internalSetNamespace(ns);
            attr.coreSetType(type);
            attr.coreSetSpecified(specified);
            ((AxiomElement)target).coreAppendAttribute(attr);
        }
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processNamespaceDeclaration(prefix, namespaceURI);
        } else {
            OMNamespace ns = builderHandler.nsCache.getOMNamespace(namespaceURI, prefix);
            if (ns == null) {
                ns = DEFAULT_NS;
            }
            AxiomNamespaceDeclaration decl = builderHandler.nodeFactory.createNode(AxiomNamespaceDeclaration.class);
            decl.setDeclaredNamespace(ns);
            ((AxiomElement)target).coreAppendAttribute(decl);
        }
    }
    
    public void attributesCompleted() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.attributesCompleted();
        } else {
            target.coreSetState(CoreParentNode.INCOMPLETE);
        }
    }
    
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processCharacterData(data, ignorable);
        } else if (!ignorable && pendingCharacterData == null && target.coreGetFirstChildIfAvailable() == null) {
            pendingCharacterData = data;
        } else {
            AxiomCharacterDataNode node = builderHandler.nodeFactory.createNode(AxiomCharacterDataNode.class);
            node.coreSetCharacterData(data);
            node.coreSetIgnorable(ignorable);
            addChild(node);
        }
    }
    
    public Context startProcessingInstruction(String piTarget) throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startProcessingInstruction(piTarget);
            return this;
        } else {
            AxiomProcessingInstruction node = builderHandler.nodeFactory.createNode(AxiomProcessingInstruction.class);
            node.coreSetTarget(piTarget);
            addChild(node);
            return newContext(node);
        }
    }

    public Context endProcessingInstruction() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endProcessingInstruction();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }

    public Context startComment() throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startComment();
            return this;
        } else {
            AxiomComment node = builderHandler.nodeFactory.createNode(AxiomComment.class);
            addChild(node);
            return newContext(node);
        }
    }
    
    public Context endComment() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endComment();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }
    
    public Context startCDATASection() throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startCDATASection();
            return this;
        } else {
            CoreCDATASection node = builderHandler.nodeFactory.createNode(CoreCDATASection.class);
            addChild(node);
            return newContext(node);
        }
    }
    
    public Context endCDATASection() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endCDATASection();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }
    
    public void processEntityReference(String name, String replacementText) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processEntityReference(name, replacementText);
        } else {
            AxiomEntityReference node = builderHandler.nodeFactory.createNode(AxiomEntityReference.class);
            node.coreSetName(name);
            node.coreSetReplacementText(replacementText);
            addChild(node);
        }
    }
    
    public void completed() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.completed();
            decrementPassThroughDepth();
        } else {
            if (depth != 0) {
                throw new IllegalStateException();
            }
            if (target != null) {
                target.coreSetState(CoreParentNode.COMPLETE);
                target.coreSetInputContext(null);
            }
            target = null;
        }
    }
}
