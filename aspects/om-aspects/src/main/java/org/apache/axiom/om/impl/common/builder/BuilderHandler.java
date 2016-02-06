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
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomCDATASection;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class BuilderHandler {
    private static final Log log = LogFactory.getLog(BuilderHandler.class);
    
    private final NodeFactory nodeFactory;
    private final Model model;
    private final AxiomSourcedElement root;
    private final OMXMLParserWrapper builder;
    public AxiomContainer target;
    // returns the state of completion
    public boolean done;
    // keeps the state of the cache
    public boolean cache = true;
    public AxiomDocument document;
    
    /**
     * Tracks the depth of the node identified by {@link #target}. By definition, the level of the
     * root element is defined as 1. Note that if caching is disabled, then this depth may be
     * different from the actual depth reached by the underlying parser.
     */
    public int elementLevel;
    
    /**
     * Stores the stack trace of the code that caused a node to be discarded or consumed. This is
     * only used if debug logging was enabled when builder was created.
     */
    public Map<OMContainer,Throwable> discardTracker = log.isDebugEnabled() ? new LinkedHashMap<OMContainer,Throwable>() : null;
    
    private ArrayList<NodePostProcessor> nodePostProcessors;

    public BuilderHandler(NodeFactory nodeFactory, Model model, AxiomSourcedElement root, OMXMLParserWrapper builder) {
        this.nodeFactory = nodeFactory;
        this.model = model;
        this.root = root;
        this.builder = builder;
    }

    public void addNodePostProcessor(NodePostProcessor nodePostProcessor) {
        if (nodePostProcessors == null) {
            nodePostProcessors = new ArrayList<NodePostProcessor>();
        }
        nodePostProcessors.add(nodePostProcessor);
    }
    
    public void postProcessNode(OMSerializable node) {
        if (nodePostProcessors != null) {
            for (int i=0, size=nodePostProcessors.size(); i<size; i++) {
                nodePostProcessors.get(i).postProcessNode(node);
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
        postProcessNode(node);
    }
    
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) {
        if (root == null) {
            document = nodeFactory.createNode(model.getDocumentType());
            document.coreSetInputEncoding(inputEncoding);
            document.coreSetXmlVersion(xmlVersion);
            document.coreSetXmlEncoding(xmlEncoding);
            document.coreSetStandalone(standalone);
            document.coreSetBuilder(builder);
            postProcessNode(document);
            target = document;
        }
    }
    
    public void createDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) {
        model.validateEventType(XMLStreamConstants.DTD);
        AxiomDocType node = nodeFactory.createNode(AxiomDocType.class);
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        addChild(node);
    }
    
    public AxiomElement startElement(String namespaceURI, String localName, String prefix) {
        elementLevel++;
        AxiomElement element;
        if (elementLevel == 1 && root != null) {
            root.validateName(prefix, localName, namespaceURI);
            element = root;
        } else {
            element = nodeFactory.createNode(model.determineElementType(
                    target, elementLevel, namespaceURI, localName));
            element.coreSetBuilder(builder);
            element.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
            element.initName(localName, /*ns*/ null, false);
            addChild(element);
        }
        target = element;
        return element;
    }
    
    public void endElement() {
        elementLevel--;
        target.setComplete(true);
        if (elementLevel == 0) {
            // This is relevant for OMSourcedElements and for the case where the document has been discarded
            // using getDocumentElement(true). In these cases, this will actually set target to null. In all
            // other cases, this will have the same effect as the instruction in the else clause.
            target = document;
        } else {
            target = (AxiomContainer)((AxiomElement)target).getParent();
        }
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
    
    public void createProcessingInstruction(String piTarget, String piData) {
        model.validateEventType(XMLStreamConstants.PROCESSING_INSTRUCTION);
        AxiomProcessingInstruction node = nodeFactory.createNode(AxiomProcessingInstruction.class);
        node.coreSetTarget(piTarget);
        node.coreSetCharacterData(piData, AxiomSemantics.INSTANCE);
        addChild(node);
    }

    public void createComment(String content) {
        model.validateEventType(XMLStreamConstants.COMMENT);
        AxiomComment node = nodeFactory.createNode(AxiomComment.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
    
    public void createCDATASection(String content) {
        model.validateEventType(XMLStreamConstants.CDATA);
        AxiomCDATASection node = nodeFactory.createNode(AxiomCDATASection.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
    
    public void createEntityReference(String name, String replacementText) {
        model.validateEventType(XMLStreamConstants.ENTITY_REFERENCE);
        AxiomEntityReference node = nodeFactory.createNode(AxiomEntityReference.class);
        node.coreSetName(name);
        node.coreSetReplacementText(replacementText);
        addChild(node);
    }
    
    public void endDocument() {
        if (elementLevel != 0) {
            throw new IllegalStateException();
        }
        if (document != null) {
            document.setComplete(true);
        }
        target = null;
        done = true;
    }
}
