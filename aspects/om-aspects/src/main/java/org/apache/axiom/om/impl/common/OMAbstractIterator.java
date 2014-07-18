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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;

/**
 * Abstract base class for iterators over sets of OM nodes.
 */
public abstract class OMAbstractIterator implements Iterator {
    private OMSerializable currentNode;
    
    /**
     * The parent of the current node. This is used to detect concurrent modifications.
     */
    private OMContainer currentParent;
    
    private OMSerializable nextNode;
    private boolean noMoreNodes;
    private boolean nextCalled;

    public OMAbstractIterator(OMSerializable firstNode) {
        if (firstNode == null) {
            noMoreNodes = true;
        } else {
            nextNode = firstNode;
        }
    }

    /**
     * Get the next node.
     * 
     * @param currentNode the predecessor of the node to retrieve
     * @return the next node
     */
    protected abstract OMSerializable getNextNode(OMSerializable currentNode);

    public boolean hasNext() {
        if (noMoreNodes) {
            return false;
        } else if (nextNode != null) {
            return true;
        } else {
            if (currentNode instanceof OMNode && ((OMNode)currentNode).getParent() != currentParent) {
                throw new ConcurrentModificationException("The current node has been removed using a method other than Iterator#remove()");
            }
            nextNode = getNextNode(currentNode);
            noMoreNodes = nextNode == null;
            return !noMoreNodes;
        }
    }

    public Object next() {
        if (hasNext()) {
            currentNode = nextNode;
            currentParent = currentNode instanceof OMNode ? ((OMNode)currentNode).getParent() : null;
            nextNode = null;
            nextCalled = true;
            return currentNode;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        if (!nextCalled) {
            throw new IllegalStateException("next() has not yet been called");
        }
        // Make sure that we know the next node before removing the current one
        hasNext();
        if (currentNode instanceof OMNode) {
            ((OMNode)currentNode).detach();
        }
        nextCalled = false;
    }
}
