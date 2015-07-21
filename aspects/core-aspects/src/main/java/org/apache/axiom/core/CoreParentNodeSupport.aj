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
package org.apache.axiom.core;

import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

public aspect CoreParentNodeSupport {
    private Object CoreParentNode.content;
    
    // TODO: rename & make final
    public int CoreParentNode.getState() {
        return flags & Flags.STATE_MASK;
    }
    
    public final void CoreParentNode.coreSetState(int state) {
        flags = (flags & ~Flags.STATE_MASK) | state;
    }
    
    public void CoreParentNode.forceExpand() {}
    
    final Content CoreParentNode.getContent(boolean create) {
        if (getState() == COMPACT) {
            Content content = new Content();
            CoreCharacterDataNode cdata = coreGetNodeFactory().createCharacterDataNode();
            cdata.internalSetParent(this);
            cdata.coreSetData((String)this.content);
            content.firstChild = cdata;
            content.lastChild = cdata;
            this.content = content;
            coreSetState(COMPLETE);
            return content;
        } else {
            Content content = (Content)this.content;
            if (content == null && create) {
                content = new Content();
                this.content = content;
            }
            return content;
        }
    }
    
    /**
     * Get the first child if it is available. The child is available if it is complete or
     * if the builder has started building the node. In the latter case,
     * {@link OMNode#isComplete()} may return <code>false</code> when called on the child. 
     * In contrast to {@link OMContainer#getFirstOMChild()}, this method will never modify
     * the state of the underlying parser.
     * 
     * @return the first child or <code>null</code> if the container has no children or
     *         the builder has not yet started to build the first child
     */
    public final CoreChildNode CoreParentNode.coreGetFirstChildIfAvailable() {
        forceExpand();
        Content content = getContent(false);
        return content == null ? null : content.firstChild;
    }

    public CoreChildNode CoreParentNode.coreGetLastKnownChild() {
        Content content = getContent(false);
        return content == null ? null : content.lastChild;
    }

    public void CoreParentNode.buildNext() {
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
    
    public CoreChildNode CoreParentNode.coreGetFirstChild() {
        CoreChildNode firstChild = coreGetFirstChildIfAvailable();
        if (firstChild == null) {
            switch (getState()) {
                case CoreParentNode.DISCARDED:
                    ((StAXBuilder)getBuilder()).debugDiscarded(this);
                    throw new NodeUnavailableException();
                case CoreParentNode.INCOMPLETE:
                    do {
                        buildNext();
                    } while (getState() == CoreParentNode.INCOMPLETE
                            && (firstChild = coreGetFirstChildIfAvailable()) == null);
            }
        }
        return firstChild;
    }

    public final CoreChildNode CoreParentNode.coreGetLastChild() {
        build();
        return coreGetLastKnownChild();
    }

    public final void CoreParentNode.coreAppendChild(CoreChildNode child, boolean fromBuilder) {
        CoreParentNode parent = child.coreGetParent();
        if (!fromBuilder) {
            build();
        }
        Content content = getContent(true);
        if (parent == this && child == content.lastChild) {
            // The child is already the last node. 
            // We don't need to detach and re-add it.
            return;
        }
        child.internalDetach(null, this);
        if (content.firstChild == null) {
            content.firstChild = child;
        } else {
            child.previousSibling = content.lastChild;
            content.lastChild.nextSibling = child;
        }
        content.lastChild = child;
    }

    public final void CoreParentNode.coreAppendChildren(CoreDocumentFragment fragment) {
        fragment.build();
        Content fragmentContent = fragment.getContent(false);
        if (fragmentContent == null || fragmentContent.firstChild == null) {
            // Fragment is empty; nothing to do
            return;
        }
        build();
        CoreChildNode child = fragmentContent.firstChild;
        while (child != null) {
            child.internalSetParent(this);
            child = child.nextSibling;
        }
        Content content = getContent(true);
        if (content.firstChild == null) {
            content.firstChild = fragmentContent.firstChild;
        } else {
            fragmentContent.firstChild.previousSibling = content.lastChild;
            content.lastChild.nextSibling = fragmentContent.firstChild;
        }
        content.lastChild = fragmentContent.lastChild;
        fragmentContent.firstChild = null;
        fragmentContent.lastChild = null;
    }

    public final void CoreParentNode.coreRemoveChildren(DetachPolicy detachPolicy) {
        if (getState() == COMPACT) {
            coreSetState(COMPLETE);
            content = null;
        } else {
            // We need to call this first because if may modify the state (applies to OMSourcedElements)
            CoreChildNode child = coreGetFirstChildIfAvailable();
            boolean updateState;
            if (getState() == INCOMPLETE && getBuilder() != null) {
                CoreChildNode lastChild = coreGetLastKnownChild();
                if (lastChild instanceof CoreParentNode) {
                    ((CoreParentNode)lastChild).build();
                }
                ((StAXOMBuilder)getBuilder()).discard((OMContainer)this);
                updateState = true;
            } else {
                updateState = false;
            }
            if (child != null) {
                CoreDocument newOwnerDocument = detachPolicy.getNewOwnerDocument(this);
                do {
                    CoreChildNode nextSibling = child.nextSibling;
                    child.previousSibling = null;
                    child.nextSibling = null;
                    child.internalUnsetParent(newOwnerDocument);
                    child = nextSibling;
                } while (child != null);
            }
            content = null;
            if (updateState) {
                coreSetState(COMPLETE);
            }
        }
    }
    
    public final String CoreParentNode.coreGetTextContent(ElementAction elementAction) {
        if (getState() == COMPACT) {
            return (String)content;
        } else {
            String textContent = null;
            StringBuilder buffer = null;
            int depth = 0;
            CoreChildNode child = coreGetFirstChild();
            boolean visited = false;
            while (child != null) {
                if (visited) {
                    visited = false;
                } else if (child instanceof CoreElement) {
                    switch (elementAction) {
                        case FAIL:
                            return null;
                        case RECURSE:
                            CoreChildNode firstChild = ((CoreElement)child).coreGetFirstChild();
                            if (firstChild != null) {
                                child = firstChild;
                                depth++;
                                continue;
                            }
                            // Fall through
                        case SKIP:
                            // Just continue
                    }
                } else {
                    String textValue;
                    if (child instanceof CoreCharacterDataNode) {
                        textValue = ((CoreCharacterDataNode)child).coreGetData();
                    } else if (child instanceof CoreCDATASection) {
                        textValue = ((CoreCDATASection)child).coreGetData();
                    } else {
                        textValue = null;
                    }
                    if (textValue != null && textValue.length() != 0) {
                        if (textContent == null) {
                            // This is the first non empty text node. Just save the string.
                            textContent = textValue;
                        } else {
                            // We've already seen a non empty text node before. Concatenate using
                            // a StringBuilder.
                            if (buffer == null) {
                                // This is the first text node we need to append. Initialize the
                                // StringBuilder.
                                buffer = new StringBuilder(textContent);
                            }
                            buffer.append(textValue);
                        }
                    }
                }
                CoreChildNode nextSibling = child.coreGetNextSibling();
                if (depth > 0 && nextSibling == null) {
                    depth--;
                    child = (CoreChildNode)child.coreGetParent();
                    visited = true;
                } else {
                    child = nextSibling;
                }
            }
            if (textContent == null) {
                // We didn't see any text nodes. Return an empty string.
                return "";
            } else if (buffer != null) {
                return buffer.toString();
            } else {
                return textContent;
            }
        }
    }
    
    public final void CoreParentNode.coreSetTextContent(String text, DetachPolicy detachPolicy) {
        coreRemoveChildren(detachPolicy);
        if (text != null && text.length() > 0) {
            coreSetState(COMPACT);
            content = text;
        }
    }
}
