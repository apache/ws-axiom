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
package org.apache.axiom.om.impl.common;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.serializer.pull.OMXMLStreamReaderExAdapter;
import org.apache.axiom.om.impl.common.serializer.pull.PullSerializer;
import org.apache.axiom.om.impl.common.serializer.push.sax.XMLReaderImpl;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

public aspect OMContainerSupport {
    declare parents: (InformationItem+ && OMContainer+) implements IContainer;
    
    private static final Log log = LogFactory.getLog(OMContainerSupport.class);
    
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    public boolean IContainer.isComplete() {
        return getState() == COMPLETE;
    }

    public final void IContainer.discarded() {
        coreSetState(DISCARDED);
    }

    public XMLStreamReader IContainer.getXMLStreamReader() {
        return getXMLStreamReader(true);
    }
    
    public XMLStreamReader IContainer.getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public XMLStreamReader IContainer.getXMLStreamReader(boolean cache) {
        return getXMLStreamReader(cache, defaultReaderConfiguration);
    }
    
    public XMLStreamReader IContainer.getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        OMXMLParserWrapper builder = getBuilder();
        if (builder != null && builder.isCompleted() && !cache && !isComplete()) {
            throw new UnsupportedOperationException("The parser is already consumed!");
        }
        OMXMLStreamReader reader = new OMXMLStreamReaderExAdapter(new PullSerializer(this, cache, configuration.isPreserveNamespaceContext()));
        
        if (configuration.isNamespaceURIInterning()) {
            reader = new NamespaceURIInterningXMLStreamReaderWrapper(reader);
        }
        
        // If debug is enabled, wrap the OMXMLStreamReader in a validator.
        // The validator will check for mismatched events to help determine if the OMStAXWrapper
        // is functioning correctly.  All problems are reported as debug.log messages
        
        if (log.isDebugEnabled()) {
            reader = 
                new OMXMLStreamReaderValidator(reader, // delegate to actual reader
                     false); // log problems (true will cause exceptions to be thrown)
        }
        
        return reader;
    }
    
    public void IContainer.addChild(OMNode omNode) {
        addChild(omNode, false);
    }

    public final INode IContainer.prepareNewChild(OMNode omNode) {
        INode child;
        // Careful here: if the child was created by another Axiom implementation, it doesn't
        // necessarily implement INode
        if (omNode.getOMFactory().getMetaFactory().equals(getOMFactory().getMetaFactory())) {
            child = (INode)omNode;
        } else {
            child = (INode)((OMFactoryEx)getOMFactory()).importNode(omNode);
        }
        checkChild(omNode);
        if (child.getParent() != null) {
            child.detach();
        }
        return child;
    }

    public void IContainer.addChild(OMNode omNode, boolean fromBuilder) {
        INode child;
        if (fromBuilder) {
            // If the new child was provided by the builder, we know that it was created by
            // the same factory
            child = (INode)omNode;
        } else {
            if (!isComplete()) {
                build();
            }
            if (omNode.getParent() == this && omNode == coreGetLastKnownChild()) {
                // The child is already the last node. 
                // We don't need to detach and re-add it.
                return;
            }
            child = prepareNewChild(omNode);
        }
        
        child.internalSetParent(this);

        if (coreGetFirstChildIfAvailable() == null) {
            coreSetFirstChild(child);
        } else {
            CoreChildNode lastChild = coreGetLastKnownChild();
            child.coreSetPreviousSibling(lastChild);
            lastChild.coreSetNextSibling(child);
        }
        coreSetLastChild(child);

        // For a normal OMNode, the incomplete status is
        // propogated up the tree.  
        // However, a OMSourcedElement is self-contained 
        // (it has an independent parser source).
        // So only propogate the incomplete setting if this
        // is a normal OMNode
        if (!fromBuilder && !child.isComplete() && 
            !(child instanceof OMSourcedElement)) {
            setComplete(false);
        }
    }
    
    public void IContainer.defaultBuild() {
        OMXMLParserWrapper builder = getBuilder();
        if (getState() == IContainer.DISCARDED) {
            if (builder != null) {
                ((StAXBuilder)builder).debugDiscarded(this);
            }
            throw new NodeUnavailableException();
        }
        if (builder != null && builder.isCompleted()) {
            log.debug("Builder is already complete.");
        }
        while (!isComplete()) {

            builder.next();    
            if (builder.isCompleted() && !isComplete()) {
                log.debug("Builder is complete.  Setting OMObject to complete.");
                setComplete(true);
            }
        }
    }
    
    public OMNode IContainer.getFirstOMChildIfAvailable() {
        return (OMNode)coreGetFirstChildIfAvailable();
    }
    
    public OMNode IContainer.getFirstOMChild() {
        return (OMNode)coreGetFirstChild();
    }
    
    public void IContainer.removeChildren() {
        // We need to call this first because if may modify the state (applies to OMSourcedElements)
        CoreChildNode child = coreGetFirstChildIfAvailable();
        boolean updateState;
        if (getState() == CoreParentNode.INCOMPLETE && getBuilder() != null) {
            CoreChildNode lastKnownChild = coreGetLastKnownChild();
            if (lastKnownChild instanceof IContainer) {
                ((IContainer)lastKnownChild).build();
            }
            ((StAXOMBuilder)getBuilder()).discard(this);
            updateState = true;
        } else {
            updateState = false;
        }
        while (child != null) {
            CoreChildNode nextSibling = child.coreGetNextSiblingIfAvailable();
            child.coreSetPreviousSibling(null);
            child.coreSetNextSibling(null);
            child.internalUnsetParent(null); // NOTE: only valid for OM
            child = nextSibling;
        }
        coreSetFirstChild(null);
        coreSetLastChild(null);
        if (updateState) {
            setComplete(true);
        }
    }
    
    public Iterator OMContainer.getChildren() {
        return new OMChildrenIterator(getFirstOMChild());
    }

    public Iterator OMContainer.getChildrenWithLocalName(String localName) {
        return new OMChildrenLocalNameIterator(getFirstOMChild(), localName);
    }

    public Iterator OMContainer.getChildrenWithNamespaceURI(String uri) {
        return new OMChildrenNamespaceIterator(getFirstOMChild(), uri);
    }

    public Iterator OMContainer.getChildrenWithName(QName elementQName) {
        OMNode firstChild = getFirstOMChild();
        Iterator it =  new OMChildrenQNameIterator(firstChild, elementQName);
        
        // The getChidrenWithName method used to tolerate an empty namespace
        // and interpret that as getting any element that matched the local
        // name.  There are custmers of axiom that have hard-coded dependencies
        // on this semantic.
        // The following code falls back to this legacy behavior only if
        // (a) elementQName has no namespace, (b) the new iterator finds no elements
        // and (c) there are children.
        // TODO: DOOM actually supported elementQName == null; need to test and document this
        if (elementQName != null && elementQName.getNamespaceURI().length() == 0 &&
            firstChild != null &&
            !it.hasNext()) {
            if (log.isTraceEnabled()) {
                log.trace("There are no child elements that match the unqualifed name: " +
                          elementQName);
                log.trace("Now looking for child elements that have the same local name.");
            }
            it = new OMChildrenLegacyQNameIterator(getFirstOMChild(), elementQName);
        }
        
        return it;
    }
    
    public Iterator OMContainer.getDescendants(boolean includeSelf) {
        return new OMDescendantsIterator(this, includeSelf);
    }

    public OMElement IContainer.getFirstChildWithName(QName elementQName) throws OMException {
        OMChildrenQNameIterator omChildrenQNameIterator =
                new OMChildrenQNameIterator(getFirstOMChild(),
                                            elementQName);
        OMNode omNode = null;
        if (omChildrenQNameIterator.hasNext()) {
            omNode = (OMNode) omChildrenQNameIterator.next();
        }

        return ((omNode != null) && (OMNode.ELEMENT_NODE == omNode.getType())) ?
                (OMElement) omNode : null;
    }

    public final SAXSource IContainer.getSAXSource(boolean cache) {
        return new SAXSource(new XMLReaderImpl(this, cache), new InputSource());
    }

    public SAXResult IContainer.getSAXResult() {
        SAXResultContentHandler handler = new SAXResultContentHandler(this);
        SAXResult result = new SAXResult();
        result.setHandler(handler);
        result.setLexicalHandler(handler);
        return result;
    }
}
