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
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.InputContext;
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

    public AxiomContainer target;
    
    private Object pendingCharacterData;
    
    public Context(BuilderHandler builderHandler, Context parentContext, int depth) {
        this.builderHandler = builderHandler;
        this.parentContext = parentContext;
        this.depth = depth;
    }

    @Override
    public Builder getBuilder() {
        return builderHandler.builder;
    }

    private Context newContext(AxiomContainer target) {
        if (nestedContext == null) {
            nestedContext = new Context(builderHandler, this, depth+1);
        }
        nestedContext.target = target;
        return nestedContext;
    }
    
    private void addChild(AxiomChildNode node) {
        if (pendingCharacterData != null) {
            AxiomCharacterDataNode cdataNode = builderHandler.nodeFactory.createNode(AxiomCharacterDataNode.class);
            cdataNode.coreSetCharacterData(pendingCharacterData);
            target.coreAppendChild(cdataNode, true);
            pendingCharacterData = null;
        }
        target.coreAppendChild(node, true);
        if (!(node instanceof CoreCharacterDataNode)) {
            builderHandler.nodeAdded(node);
        }
    }
    
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) {
        AxiomDocType node = builderHandler.nodeFactory.createNode(AxiomDocType.class);
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        addChild(node);
    }
    
    public Context startElement(String namespaceURI, String localName, String prefix) {
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
                    target, depth+1, namespaceURI, localName));
            element.coreSetInputContext(this);
            element.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
            element.initName(localName, ns, false);
            addChild(element);
        }
        return newContext(element);
    }
    
    public Context endElement() {
        target.setComplete(true);
        target.coreSetInputContext(null);
        if (pendingCharacterData != null) {
            target.coreSetCharacterData(pendingCharacterData, null);
            pendingCharacterData = null;
        }
        target = null;
        return parentContext;
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) {
        OMNamespace ns = builderHandler.nsCache.getOMNamespace(namespaceURI, prefix);
        AxiomAttribute attr = builderHandler.nodeFactory.createNode(AxiomAttribute.class);
        attr.internalSetLocalName(localName);
        attr.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
        attr.internalSetNamespace(ns);
        attr.coreSetType(type);
        attr.coreSetSpecified(specified);
        ((AxiomElement)target).coreAppendAttribute(attr);
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) {
        OMNamespace ns = builderHandler.nsCache.getOMNamespace(namespaceURI, prefix);
        if (ns == null) {
            ns = DEFAULT_NS;
        }
        AxiomNamespaceDeclaration decl = builderHandler.nodeFactory.createNode(AxiomNamespaceDeclaration.class);
        decl.setDeclaredNamespace(ns);
        ((AxiomElement)target).coreAppendAttribute(decl);
    }
    
    public void attributesCompleted() {
        target.coreSetState(CoreParentNode.INCOMPLETE);
    }
    
    public void processCharacterData(Object data, boolean ignorable) {
        if (!ignorable && pendingCharacterData == null && target.coreGetFirstChildIfAvailable() == null) {
            pendingCharacterData = data;
        } else {
            AxiomCharacterDataNode node = builderHandler.nodeFactory.createNode(AxiomCharacterDataNode.class);
            node.coreSetCharacterData(data);
            node.coreSetIgnorable(ignorable);
            addChild(node);
        }
    }
    
    public void processProcessingInstruction(String piTarget, String piData) {
        AxiomProcessingInstruction node = builderHandler.nodeFactory.createNode(AxiomProcessingInstruction.class);
        node.coreSetTarget(piTarget);
        node.coreSetCharacterData(piData, AxiomSemantics.INSTANCE);
        addChild(node);
    }

    public void processComment(String content) {
        AxiomComment node = builderHandler.nodeFactory.createNode(AxiomComment.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
    
    public void processCDATASection(String content) {
        AxiomCDATASection node = builderHandler.nodeFactory.createNode(AxiomCDATASection.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
    
    public void processEntityReference(String name, String replacementText) {
        AxiomEntityReference node = builderHandler.nodeFactory.createNode(AxiomEntityReference.class);
        node.coreSetName(name);
        node.coreSetReplacementText(replacementText);
        addChild(node);
    }
    
    public void endDocument() {
        if (depth != 0) {
            throw new IllegalStateException();
        }
        if (target != null) {
            target.setComplete(true);
            target.coreSetInputContext(null);
        }
        target = null;
    }
}
