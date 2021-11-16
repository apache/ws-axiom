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
package org.apache.axiom.core.impl.builder;

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreCDATASection;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreComment;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreDocumentTypeDeclaration;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreModelStreamException;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNSUnawareAttribute;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreProcessingInstruction;
import org.apache.axiom.core.InputContext;
import org.apache.axiom.core.stream.NullXmlHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;

final class BuildableContext extends Context implements InputContext {
    private final Context parentContext;

    private CoreParentNode target;
    
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

    BuildableContext(BuilderHandler builderHandler, Context parentContext, int depth) {
        super(builderHandler, depth);
        this.parentContext = parentContext;
    }

    void init(CoreParentNode target) {
        this.target = target;
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
    public void setTarget(CoreParentNode target) {
        this.target = target;
    }

    @Override
    public void discard() {
        target.coreSetState(CoreParentNode.DISCARDING);
        passThroughHandler = NullXmlHandler.INSTANCE;
        builderHandler.decrementActiveContextCount();
    }

    private Context endContext() throws StreamException {
        target.coreSetInputContext(null);
        if (pendingCharacterData != null) {
            try {
                target.coreSetCharacterData(pendingCharacterData, null);
            } catch (CoreModelException ex) {
                throw new CoreModelStreamException(ex);
            }
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
            target.coreSetInputContext(null);
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
            CoreCharacterDataNode cdataNode = builderHandler.nodeFactory.createNode(CoreCharacterDataNode.class);
            cdataNode.coreSetCharacterData(pendingCharacterData);
            target.internalAppendChildWithoutBuild(cdataNode);
            pendingCharacterData = null;
        }
        target.internalAppendChildWithoutBuild(node);
        if (!(node instanceof CoreCharacterDataNode)) {
            builderHandler.nodeAdded(node);
        }
    }
    
    @Override
    void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            Boolean standalone) {
        CoreDocument document = builderHandler.nodeFactory.createNode(builderHandler.model.getDocumentType());
        document.coreSetInputEncoding(inputEncoding);
        document.coreSetXmlVersion(xmlVersion);
        document.coreSetXmlEncoding(xmlEncoding);
        document.coreSetStandalone(standalone);
        document.coreSetInputContext(this);
        builderHandler.nodeAdded(document);
        target = document;
    }

    @Override
    void startFragment() {
        startDocument(null, "1.0", null, true);
    }

    void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
        } else {
            CoreDocumentTypeDeclaration node = builderHandler.nodeFactory.createNode(CoreDocumentTypeDeclaration.class);
            node.coreSetRootName(rootName);
            node.coreSetPublicId(publicId);
            node.coreSetSystemId(systemId);
            node.coreSetInternalSubset(internalSubset);
            addChild(node);
        }
    }
    
    @Override
    Context startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startElement(namespaceURI, localName, prefix);
            return this;
        } else {
            CoreNSAwareElement element = builderHandler.nodeFactory.createNode(builderHandler.model.determineElementType(
                    target, depth+1, namespaceURI, localName));
            element.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
            element.initName(namespaceURI, localName, prefix, builderHandler.namespaceHelper);
            addChild(element);
            return newContext(element);
        }
    }
    
    @Override
    Context endElement() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endElement();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }

    @Override
    void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        } else {
            CoreNSAwareAttribute attr = builderHandler.nodeFactory.createNode(CoreNSAwareAttribute.class);
            attr.initName(namespaceURI, localName, prefix, builderHandler.namespaceHelper);
            try {
                attr.coreSetCharacterData(value, null);
            } catch (CoreModelException ex) {
                throw new CoreModelStreamException(ex);
            }
            attr.coreSetType(type);
            attr.coreSetSpecified(specified);
            ((CoreElement)target).coreAppendAttribute(attr);
        }
    }
    
    @Override
    void processAttribute(String name, String value, String type, boolean specified) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processAttribute(name, value, type, specified);
        } else {
            CoreNSUnawareAttribute attr = builderHandler.nodeFactory.createNode(CoreNSUnawareAttribute.class);
            attr.coreSetName(name);
            try {
                attr.coreSetCharacterData(value, null);
            } catch (CoreModelException ex) {
                throw new CoreModelStreamException(ex);
            }
            attr.coreSetType(type);
            attr.coreSetSpecified(specified);
            ((CoreElement)target).coreAppendAttribute(attr);
        }
    }
    
    @Override
    void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processNamespaceDeclaration(prefix, namespaceURI);
        } else {
            CoreNamespaceDeclaration decl = builderHandler.nodeFactory.createNode(CoreNamespaceDeclaration.class);
            decl.init(prefix, namespaceURI, builderHandler.namespaceHelper);
            ((CoreElement)target).coreAppendAttribute(decl);
        }
    }
    
    @Override
    void attributesCompleted() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.attributesCompleted();
        } else {
            target.coreSetState(CoreParentNode.INCOMPLETE);
        }
    }
    
    @Override
    void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processCharacterData(data, ignorable);
        } else if (!ignorable && pendingCharacterData == null && target.coreGetFirstChildIfAvailable() == null) {
            pendingCharacterData = data;
        } else {
            CoreCharacterDataNode node = builderHandler.nodeFactory.createNode(CoreCharacterDataNode.class);
            node.coreSetCharacterData(data);
            node.coreSetIgnorable(ignorable);
            addChild(node);
        }
    }
    
    @Override
    Context startProcessingInstruction(String piTarget) throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startProcessingInstruction(piTarget);
            return this;
        } else {
            CoreProcessingInstruction node = builderHandler.nodeFactory.createNode(CoreProcessingInstruction.class);
            node.coreSetTarget(piTarget);
            addChild(node);
            return newContext(node);
        }
    }

    @Override
    Context endProcessingInstruction() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endProcessingInstruction();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }

    @Override
    Context startComment() throws StreamException {
        if (passThroughHandler != null) {
            passThroughDepth++;
            passThroughHandler.startComment();
            return this;
        } else {
            CoreComment node = builderHandler.nodeFactory.createNode(CoreComment.class);
            addChild(node);
            return newContext(node);
        }
    }
    
    @Override
    Context endComment() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endComment();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }
    
    @Override
    Context startCDATASection() throws StreamException {
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
    
    @Override
    Context endCDATASection() throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.endCDATASection();
            return decrementPassThroughDepth();
        } else {
            return endContext();
        }
    }
    
    @Override
    void processEntityReference(String name, String replacementText) throws StreamException {
        if (passThroughHandler != null) {
            passThroughHandler.processEntityReference(name, replacementText);
        } else {
            CoreEntityReference node = builderHandler.nodeFactory.createNode(CoreEntityReference.class);
            node.coreSetName(name);
            node.coreSetReplacementText(replacementText);
            addChild(node);
        }
    }
    
    @Override
    void completed() throws StreamException {
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
