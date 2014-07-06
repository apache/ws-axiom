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

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXResult;

import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.serializer.pull.OMXMLStreamReaderExAdapter;
import org.apache.axiom.om.impl.common.serializer.pull.PullSerializer;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public aspect OMContainerSupport {
    private static final Log log = LogFactory.getLog(OMContainerSupport.class);
    
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    public XMLStreamReader OMContainer.getXMLStreamReader() {
        return getXMLStreamReader(true);
    }
    
    public XMLStreamReader OMContainer.getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public XMLStreamReader OMContainer.getXMLStreamReader(boolean cache) {
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
    
    public void IContainer.addChild(OMNode omNode, boolean fromBuilder) {
        OMNodeEx child;
        if (fromBuilder) {
            // If the new child was provided by the builder, we know that it was created by
            // the same factory
            child = (OMNodeEx)omNode;
        } else {
            // Careful here: if the child was created by another Axiom implementation, it doesn't
            // necessarily implement OMNodeEx
            if (omNode.getOMFactory().getMetaFactory().equals(getOMFactory().getMetaFactory())) {
                child = (OMNodeEx)omNode;
            } else {
                child = (OMNodeEx)((OMFactoryEx)getOMFactory()).importNode(omNode);
            }
            if (!isComplete()) {
                build();
            }
            if (child.getParent() == this && child == getLastKnownOMChild()) {
                // The child is already the last node. 
                // We don't need to detach and re-add it.
                return;
            }
            checkChild(omNode);
        }
        if (child.getParent() != null) {
            child.detach();
        }
        
        child.setParent(this);

        if (getFirstOMChildIfAvailable() == null) {
            setFirstChild(child);
        } else {
            OMNode lastChild = getLastKnownOMChild();
            child.setPreviousOMSibling(lastChild);
            ((OMNodeEx)lastChild).setNextOMSibling(child);
        }
        setLastChild(child);

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
    
    public void IParentNode.buildNext() {
        OMXMLParserWrapper builder = getBuilder();
        if (builder == null) {
            throw new IllegalStateException("The node has no builder");
        } else if (((StAXOMBuilder)builder).isClosed()) {
            throw new OMException("The builder has already been closed");
        } else if (!builder.isCompleted()) {
            builder.next();
        } else {
            // If the builder is suddenly complete, but the completion status of the node
            // doesn't change, then this means that we built the wrong nodes
            throw new IllegalStateException("Builder is already complete");
        }         
    }
    
    public OMNode IParentNode.getFirstOMChild() {
        OMNode firstChild = getFirstOMChildIfAvailable();
        if (firstChild == null) {
            switch (getState()) {
                case IParentNode.DISCARDED:
                    ((StAXBuilder)getBuilder()).debugDiscarded(this);
                    throw new NodeUnavailableException();
                case IParentNode.INCOMPLETE:
                    do {
                        buildNext();
                    } while (getState() == IParentNode.INCOMPLETE
                            && (firstChild = getFirstOMChildIfAvailable()) == null);
            }
        }
        return firstChild;
    }
    
    public void IContainer.removeChildren() {
        boolean updateState;
        if (getState() == IParentNode.INCOMPLETE && getBuilder() != null) {
            OMNode lastKnownChild = getLastKnownOMChild();
            if (lastKnownChild != null) {
                lastKnownChild.build();
            }
            ((StAXOMBuilder)getBuilder()).discard(this);
            updateState = true;
        } else {
            updateState = false;
        }
        IChildNode child = (IChildNode)getFirstOMChildIfAvailable();
        while (child != null) {
            IChildNode nextSibling = (IChildNode)child.getNextOMSiblingIfAvailable();
            child.setPreviousOMSibling(null);
            child.setNextOMSibling(null);
            child.setParent(null);
            child = nextSibling;
        }
        setFirstChild(null);
        setLastChild(null);
        if (updateState) {
            setComplete(true);
        }
    }
    
    public SAXResult OMContainer.getSAXResult() {
        SAXResultContentHandler handler = new SAXResultContentHandler(this);
        SAXResult result = new SAXResult();
        result.setHandler(handler);
        result.setLexicalHandler(handler);
        return result;
    }
}
