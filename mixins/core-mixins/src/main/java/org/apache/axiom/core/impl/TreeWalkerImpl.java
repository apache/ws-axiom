/*
 * Copyright 2009-2011,2013 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axiom.core.impl;

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreLeafNode;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreModelStreamException;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.InputContext;
import org.apache.axiom.core.NodeConsumedException;
import org.apache.axiom.core.stream.DocumentElementExtractingFilterHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.XmlReader;

public final class TreeWalkerImpl implements XmlReader {
    private static final int STATE_NONE = 0;

    /**
     * Indicates that the serializer is synthesizing a start fragment event. This state can only be
     * reached if the root node is not a document.
     */
    private static final int STATE_START_FRAGMENT = 1;

    /** Indicates that the current node is a leaf node. */
    private static final int STATE_LEAF = 2;

    /**
     * Indicates that the current node is a parent node and that events for child nodes have not yet
     * been generated.
     */
    private static final int STATE_NOT_VISITED = 3;

    /**
     * Indicates that the current node is an element and that events for its attribute nodes have
     * already been generated.
     */
    private static final int STATE_ATTRIBUTES_VISITED = 4;

    /**
     * Indicates that the current node is a parent node and that events for child nodes have already
     * been generated.
     */
    private static final int STATE_VISITED = 5;

    /**
     * Indicates that the current node is a parent node for which the builder has been put into pass
     * through mode. In this state, events are not synthesized from the object model but passed
     * through from the underlying XML source used to build the tree. This state is only reachable
     * if {@link #preserve} is <code>true</code>.
     */
    private static final int STATE_PASS_THROUGH = 6;

    private static final int STATE_STREAMING = 7;

    private static final int STATE_ATTRIBUTE = 8;

    /**
     * Indicates that the current node is a compact parent node and that the event for its content
     * has been generated.
     */
    private static final int STATE_CONTENT_VISITED = 9;

    private final XmlHandler handler;
    private final CoreParentNode root;
    private final boolean preserve;
    private final boolean incremental;
    private CoreNode node;

    /**
     * The stream from which events are included. This is only set if {@link #state} is {@link
     * #STATE_STREAMING}.
     */
    private XmlReader reader;

    private int state = STATE_NONE;

    public TreeWalkerImpl(
            XmlHandler handler, CoreParentNode root, boolean preserve, boolean incremental) {
        this.handler = handler;
        this.root = root;
        this.preserve = preserve;
        this.incremental = incremental;
    }

    @Override
    public boolean proceed() throws StreamException {
        if (incremental && !handler.drain()) {
            return false;
        }
        try {
            // Determine the next node (i.e. the node for which the next event is generated) and
            // update the state
            final CoreNode previousNode = node;
            final CoreNode nextNode;
            if (state == STATE_PASS_THROUGH || state == STATE_STREAMING) {
                nextNode = previousNode;
            } else if (previousNode == null) {
                if (state == STATE_NONE && !(root instanceof CoreDocument)) {
                    nextNode = null;
                    state = STATE_START_FRAGMENT;
                } else {
                    nextNode = root;
                    state = STATE_NOT_VISITED;
                }
            } else if (state == STATE_VISITED && previousNode == root) {
                nextNode = null;
            } else if (state == STATE_NOT_VISITED && previousNode instanceof CoreElement) {
                final CoreElement element = (CoreElement) previousNode;
                // TODO: handle case with preserve == false
                CoreAttribute firstAttribute = element.coreGetFirstAttribute();
                if (firstAttribute == null) {
                    nextNode = element;
                    state = STATE_ATTRIBUTES_VISITED;
                } else {
                    nextNode = firstAttribute;
                    state = STATE_NOT_VISITED;
                }
            } else if (state == STATE_NOT_VISITED || state == STATE_ATTRIBUTES_VISITED) {
                final CoreParentNode parent = (CoreParentNode) previousNode;
                int nodeState = parent.getState();
                if (nodeState == CoreParentNode.COMPACT) {
                    nextNode = previousNode;
                    state = STATE_CONTENT_VISITED;
                } else if (preserve || nodeState == CoreParentNode.COMPLETE) {
                    CoreChildNode child = parent.coreGetFirstChild();
                    if (child == null) {
                        nextNode = parent;
                        state = STATE_VISITED;
                    } else {
                        nextNode = child;
                        state = STATE_NOT_VISITED;
                    }
                } else {
                    CoreChildNode child = parent.coreGetFirstChildIfAvailable();
                    if (child == null) {
                        nextNode = parent;
                        if (nodeState == CoreParentNode.DISCARDING
                                || nodeState == CoreParentNode.DISCARDED) {
                            throw new NodeConsumedException();
                        }
                        parent.coreGetInputContext().setPassThroughHandler(handler);
                        state = STATE_PASS_THROUGH;
                    } else {
                        nextNode = child;
                        state = STATE_NOT_VISITED;
                    }
                }
            } else if (state == STATE_CONTENT_VISITED) {
                nextNode = previousNode;
                state = STATE_VISITED;
            } else if (previousNode instanceof CoreChildNode) {
                final CoreChildNode previousChildNode = (CoreChildNode) previousNode;
                if (preserve) {
                    CoreChildNode sibling = previousChildNode.coreGetNextSibling();
                    if (sibling == null) {
                        nextNode = previousChildNode.coreGetParent();
                        state = STATE_VISITED;
                    } else {
                        nextNode = sibling;
                        state = STATE_NOT_VISITED;
                    }
                } else {
                    CoreChildNode sibling = previousChildNode.coreGetNextSiblingIfAvailable();
                    if (sibling == null) {
                        CoreParentNode parent = previousChildNode.coreGetParent();
                        nextNode = parent;
                        int nodeState = parent.getState();

                        // TODO: <hack>
                        if (nodeState == CoreParentNode.INCOMPLETE
                                && parent.coreGetInputContext() == null) {
                            nodeState = CoreParentNode.COMPLETE;
                        }
                        // </hack>

                        if (nodeState == CoreParentNode.COMPLETE) {
                            state = STATE_VISITED;
                        } else if (nodeState == CoreParentNode.DISCARDING
                                || nodeState == CoreParentNode.DISCARDED) {
                            throw new NodeConsumedException();
                        } else {
                            parent.coreGetInputContext().setPassThroughHandler(handler);
                            state = STATE_PASS_THROUGH;
                        }
                    } else {
                        nextNode = sibling;
                        state = STATE_NOT_VISITED;
                    }
                }
            } else {
                final CoreAttribute attribute = (CoreAttribute) previousNode;
                // TODO: handle case with preserve == false
                CoreAttribute nextAttribute = attribute.coreGetNextAttribute();
                if (nextAttribute == null) {
                    nextNode = attribute.coreGetOwnerElement();
                    state = STATE_ATTRIBUTES_VISITED;
                } else {
                    nextNode = nextAttribute;
                    state = STATE_NOT_VISITED;
                }
            }

            // More closely examine the case where we move to a node that has not
            // been visited yet. It may be a sourced element or a leaf node
            if (state == STATE_NOT_VISITED) {
                if (nextNode instanceof CoreNSAwareElement) {
                    XmlInput input =
                            ((CoreNSAwareElement) nextNode).getXmlInput(preserve, incremental);
                    if (input != null) {
                        reader =
                                input.createReader(
                                        new DocumentElementExtractingFilterHandler(handler));
                        state = STATE_STREAMING;
                    }
                } else if (nextNode instanceof CoreLeafNode) {
                    state = STATE_LEAF;
                } else if (nextNode instanceof CoreAttribute) {
                    state = STATE_ATTRIBUTE;
                }
            }

            switch (state) {
                case STATE_START_FRAGMENT -> handler.startFragment();
                case STATE_LEAF -> ((CoreLeafNode) nextNode).internalSerialize(handler, preserve);
                case STATE_ATTRIBUTE ->
                        ((CoreAttribute) nextNode).internalSerialize(handler, preserve);
                case STATE_NOT_VISITED -> ((CoreParentNode) nextNode).serializeStartEvent(handler);
                case STATE_ATTRIBUTES_VISITED -> handler.attributesCompleted();
                case STATE_VISITED -> {
                    if (nextNode == null) {
                        handler.completed();
                    } else {
                        ((CoreParentNode) nextNode).serializeEndEvent(handler);
                    }
                }
                case STATE_PASS_THROUGH -> {
                    CoreParentNode parent = (CoreParentNode) nextNode;
                    parent.coreGetInputContext().getBuilder().next();
                    if (parent.coreGetInputContext() == null) {
                        state = STATE_VISITED;
                    }
                }
                case STATE_STREAMING -> {
                    if (reader.proceed()) {
                        state = STATE_VISITED;
                        reader = null;
                    }
                }
                case STATE_CONTENT_VISITED ->
                        handler.processCharacterData(
                                ((CoreParentNode) nextNode).internalGetContent(), false);
                default -> throw new IllegalStateException();
            }
            node = nextNode;
            return state == STATE_VISITED && (nextNode == null || nextNode instanceof CoreDocument);
        } catch (CoreModelException ex) {
            throw new CoreModelStreamException(ex);
        }
    }

    @Override
    public void dispose() {
        if (!preserve && node != null) {
            CoreParentNode parent =
                    node instanceof CoreParentNode
                            ? (CoreParentNode) node
                            : ((CoreChildNode) node).coreGetParent();
            while (true) {
                InputContext context = parent.coreGetInputContext();
                if (context != null) {
                    context.discard();
                }
                if (parent == root) {
                    break;
                }
                parent = ((CoreChildNode) parent).coreGetParent();
            }
        }
        if (reader != null) {
            reader.dispose();
        }
    }
}
