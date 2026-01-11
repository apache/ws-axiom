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
package org.apache.axiom.core.impl;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.core.NodeIterator;
import org.apache.axiom.core.Semantics;

public abstract class AbstractNodeIterator<T extends CoreNode, S> implements NodeIterator<S> {
    private final CoreParentNode startNode;
    private final Axis axis;
    private final Class<T> type;
    private final Mapper<S, ? super T> mapper;
    private final Semantics semantics;
    private T currentNode;

    /** The parent of the current node. This is used to detect concurrent modifications. */
    private CoreParentNode currentParent;

    private T nextNode;
    private boolean hasNext;
    private int depth;

    public AbstractNodeIterator(
            CoreParentNode startNode,
            Axis axis,
            Class<T> type,
            Mapper<S, ? super T> mapper,
            Semantics semantics) {
        this.startNode = startNode;
        this.axis = axis;
        this.type = type;
        this.mapper = mapper;
        this.semantics = semantics;
    }

    protected abstract boolean matches(T node) throws CoreModelException;

    private void computeNext(Axis axis) {
        CoreNode node = currentNode;
        if (node instanceof CoreChildNode
                && ((CoreChildNode) node).coreGetParent() != currentParent) {
            throw new ConcurrentModificationException(
                    "The current node has been removed using a method other than Iterator#remove()");
        }
        try {
            while (true) {
                // Get to the next node
                switch (axis) {
                    case CHILDREN -> {
                        if (node == null) {
                            node = startNode.coreGetFirstChild();
                        } else {
                            node = ((CoreChildNode) node).coreGetNextSibling();
                        }
                    }
                    case DESCENDANTS, DESCENDANTS_OR_SELF -> {
                        if (node == null) {
                            if (axis == Axis.DESCENDANTS) {
                                node = startNode.coreGetFirstChild();
                                depth++;
                            } else {
                                node = startNode;
                            }
                        } else {
                            boolean visitChildren = true;
                            while (true) {
                                if (visitChildren
                                        && node instanceof CoreParentNode
                                        && semantics.isParentNode(node.coreGetNodeType())) {
                                    CoreChildNode firstChild =
                                            ((CoreParentNode) node).coreGetFirstChild();
                                    if (firstChild != null) {
                                        depth++;
                                        node = firstChild;
                                        break;
                                    }
                                }
                                if (depth == 0) {
                                    node = null;
                                    break;
                                }
                                CoreChildNode nextSibling =
                                        ((CoreChildNode) node).coreGetNextSibling();
                                if (nextSibling != null) {
                                    node = nextSibling;
                                    break;
                                }
                                depth--;
                                node = ((CoreChildNode) node).coreGetParent();
                                visitChildren = false;
                            }
                        }
                    }
                }
                if (node == null) {
                    nextNode = null;
                    break;
                }
                if (type.isInstance(node)) {
                    T candidate = type.cast(node);
                    if (matches(candidate)) {
                        nextNode = candidate;
                        break;
                    }
                }
            }
        } catch (CoreModelException ex) {
            throw semantics.toUncheckedException(ex);
        }
        hasNext = true;
    }

    @Override
    public final boolean hasNext() {
        if (!hasNext) {
            computeNext(axis);
        }
        return nextNode != null;
    }

    @Override
    public final S next() {
        if (hasNext()) {
            currentNode = nextNode;
            currentParent =
                    currentNode instanceof CoreChildNode
                            ? ((CoreChildNode) currentNode).coreGetParent()
                            : null;
            hasNext = false;
            S mapped = mapper.map(currentNode);
            if (mapped != currentNode && mapped instanceof CoreNode) {
                currentNode = type.cast(mapped);
            }
            return mapped;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public final void remove() {
        if (currentNode == null) {
            throw new IllegalStateException();
        }
        // Move to next node before replacing the current one. Note that we need to always move to
        // the next sibling or parent, even if axis is DESCENDANTS or DESCENDANTS_OR_SELF.
        computeNext(Axis.CHILDREN);
        if (currentNode instanceof CoreChildNode) {
            //            try {
            ((CoreChildNode) currentNode).coreDetach(semantics);
            //            } catch (CoreModelException ex) {
            //                throw exceptionTranslator.toUncheckedException(ex);
            //            }
        }
        currentNode = null;
    }

    @Override
    public final void replace(CoreChildNode newNode) throws CoreModelException {
        // Move to next node before replacing the current one
        // TODO: this may not be the right thing to do in all cases
        computeNext(Axis.CHILDREN);
        ((CoreChildNode) currentNode).coreReplaceWith(newNode, semantics);
    }
}
