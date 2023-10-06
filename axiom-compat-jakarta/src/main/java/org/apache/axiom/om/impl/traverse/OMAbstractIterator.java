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

package org.apache.axiom.om.impl.traverse;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;

/**
 * Abstract base class for iterators over sets of OM nodes.
 * 
 * @deprecated
 */
@SuppressWarnings("rawtypes")
public abstract class OMAbstractIterator implements Iterator {
    private OMNode currentNode;
    
    /**
     * The parent of the current node. This is used to detect concurrent modifications.
     */
    private OMContainer currentParent;
    
    private OMNode nextNode;
    private boolean noMoreNodes;
    private boolean nextCalled;

    public OMAbstractIterator(OMNode firstNode) {
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
    protected abstract OMNode getNextNode(OMNode currentNode);

    @Override
    public boolean hasNext() {
        if (noMoreNodes) {
            return false;
        } else if (nextNode != null) {
            return true;
        } else {
            if (currentNode.getParent() != currentParent) {
                throw new ConcurrentModificationException("The current node has been removed using a method other than Iterator#remove()");
            }
            nextNode = getNextNode(currentNode);
            noMoreNodes = nextNode == null;
            return !noMoreNodes;
        }
    }

    @Override
    public Object next() {
        if (hasNext()) {
            currentNode = nextNode;
            currentParent = currentNode.getParent();
            nextNode = null;
            nextCalled = true;
            return currentNode;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    @SuppressWarnings("ReturnValueIgnored")
    public void remove() {
        if (!nextCalled) {
            throw new IllegalStateException("next() has not yet been called");
        }
        // Make sure that we know the next node before removing the current one
        hasNext();
        currentNode.detach();
        nextCalled = false;
    }
}
