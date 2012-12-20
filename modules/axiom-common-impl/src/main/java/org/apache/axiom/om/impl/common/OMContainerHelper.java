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

import org.apache.axiom.om.NodeUnavailableException;
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
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class OMContainerHelper {
    private static final Log log = LogFactory.getLog(OMContainerHelper.class);
    
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    private OMContainerHelper() {}
    
    public static XMLStreamReader getXMLStreamReader(IContainer container, boolean cache) {
        return getXMLStreamReader(container, cache, defaultReaderConfiguration);
    }
    
    public static XMLStreamReader getXMLStreamReader(IContainer container, boolean cache, OMXMLStreamReaderConfiguration configuration) {
        OMXMLParserWrapper builder = container.getBuilder();
        if (builder != null && builder instanceof StAXOMBuilder) {
            if (!container.isComplete()) {
                if (((StAXOMBuilder) builder).isLookahead()) {
                    buildNext(container);
                }
            }
        }
        
        // The om tree was built by hand and is already complete
        OMXMLStreamReader reader;
        boolean done = container.isComplete();
        if ((builder == null) && done) {
            reader = new OMStAXWrapper(null, container, false, configuration.isPreserveNamespaceContext());
        } else {
            if ((builder == null) && !cache) {
                throw new UnsupportedOperationException(
                "This element was not created in a manner to be switched");
            }
            if (builder != null && builder.isCompleted() && !cache && !done) {
                throw new UnsupportedOperationException(
                "The parser is already consumed!");
            }
            reader = new OMStAXWrapper(builder, container, cache, configuration.isPreserveNamespaceContext());
        }
        
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
    
    public static void addChild(IContainer container, OMNode omNode, boolean fromBuilder) {
        OMNodeEx child;
        if (fromBuilder) {
            // If the new child was provided by the builder, we know that it was created by
            // the same factory
            child = (OMNodeEx)omNode;
        } else {
            // Careful here: if the child was created by another Axiom implementation, it doesn't
            // necessarily implement OMNodeEx
            if (omNode.getOMFactory().getMetaFactory() == container.getOMFactory().getMetaFactory()) {
                child = (OMNodeEx)omNode;
            } else {
                child = (OMNodeEx)((OMFactoryEx)container.getOMFactory()).importNode(omNode);
            }
            if (!container.isComplete()) {
                container.build();
            }
            if (child.getParent() == container && child == container.getLastKnownOMChild()) {
                // The child is already the last node. 
                // We don't need to detach and re-add it.
                return;
            }
        }
        if (child.getParent() != null) {
            child.detach();
        }
        
        child.setParent(container);

        if (container.getFirstOMChildIfAvailable() == null) {
            container.setFirstChild(child);
        } else {
            OMNode lastChild = container.getLastKnownOMChild();
            child.setPreviousOMSibling(lastChild);
            ((OMNodeEx)lastChild).setNextOMSibling(child);
        }
        container.setLastChild(child);

        // For a normal OMNode, the incomplete status is
        // propogated up the tree.  
        // However, a OMSourcedElement is self-contained 
        // (it has an independent parser source).
        // So only propogate the incomplete setting if this
        // is a normal OMNode
        if (!fromBuilder && !child.isComplete() && 
            !(child instanceof OMSourcedElement)) {
            container.setComplete(false);
        }
    }
    
    public static void build(IContainer container) {
        OMXMLParserWrapper builder = container.getBuilder();
        if (builder != null && builder.isCompleted()) {
            log.debug("Builder is already complete.");
        }
        while (!container.isComplete()) {

            builder.next();    
            if (builder.isCompleted() && !container.isComplete()) {
                log.debug("Builder is complete.  Setting OMObject to complete.");
                container.setComplete(true);
            }
        }
    }
    
    public static void buildNext(IParentNode that) {
        OMXMLParserWrapper builder = that.getBuilder();
        if (builder != null) {
            if (((StAXOMBuilder)builder).isClosed()) {
                throw new OMException("The builder has already been closed");
            } else if (!builder.isCompleted()) {
                builder.next();
            } else {
                // If the builder is suddenly complete, but the completion status of the node
                // doesn't change, then this means that we built the wrong nodes
                throw new IllegalStateException("Builder is already complete");
            }         
        }
    }
    
    public static OMNode getFirstOMChild(IParentNode that) {
        OMNode firstChild = that.getFirstOMChildIfAvailable();
        if (firstChild == null) {
            switch (that.getState()) {
                case IParentNode.DISCARDED:
                    ((StAXBuilder)that.getBuilder()).debugDiscarded(that);
                    throw new NodeUnavailableException();
                case IParentNode.INCOMPLETE:
                    do {
                        buildNext(that);
                    } while (that.getState() == IParentNode.INCOMPLETE
                            && (firstChild = that.getFirstOMChildIfAvailable()) == null);
            }
        }
        return firstChild;
    }
    
    public static void removeChildren(IContainer that) {
        boolean updateState;
        if (that.getState() == IParentNode.INCOMPLETE && that.getBuilder() != null) {
            OMNode lastKnownChild = that.getLastKnownOMChild();
            if (lastKnownChild != null) {
                lastKnownChild.build();
            }
            ((StAXOMBuilder)that.getBuilder()).discard(that);
            updateState = true;
        } else {
            updateState = false;
        }
        IChildNode child = (IChildNode)that.getFirstOMChildIfAvailable();
        while (child != null) {
            IChildNode nextSibling = (IChildNode)child.getNextOMSiblingIfAvailable();
            child.setPreviousOMSibling(null);
            child.setNextOMSibling(null);
            child.setParent(null);
            child = nextSibling;
        }
        that.setFirstChild(null);
        that.setLastChild(null);
        if (updateState) {
            that.setComplete(true);
        }
    }
}
