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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;

import javax.xml.namespace.QName;

/** 
 * Class OMChildrenQNameIterator
 * 
 * This iterator returns the elements that have a matching QName.
 * This class can be extended to customize the QName equality.
 *
 */
public class OMChildrenQNameIterator extends OMChildrenIterator {
    /** Field givenQName */
    private final QName givenQName;

    /** Field needToMoveForward */
    private boolean needToMoveForward = true;

    /** Field isMatchingNodeFound */
    private boolean isMatchingNodeFound = false;

    /**
     * Constructor OMChildrenQNameIterator.
     *
     * @param currentChild
     * @param givenQName
     */
    public OMChildrenQNameIterator(OMNode currentChild, QName givenQName) {
        super(currentChild);
        this.givenQName = givenQName;
        findNextElementWithQName();
    }
    
    /**
     * Returns true if the qnames are equal.
     * The default algorithm is to use the QName equality (which examines the namespace and localPart).
     * You can extend this class to provide your own equality algorithm.
     * @param searchQName
     * @param currentQName
     * @return true if qnames are equal.
     */
    public boolean isEqual(QName searchQName, QName currentQName) {
        return searchQName.equals(currentQName);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns
     * <tt>true</tt> if <tt>next</tt> would return an element rather than throwing an exception.)
     *
     * @return Returns <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        while (needToMoveForward) {
            findNextElementWithQName();
        }
        return isMatchingNodeFound;
    }

    private void findNextElementWithQName()
    {
        if (currentChild != null) {
            // check the current node for the criteria
            if (currentChild instanceof OMElement) {
                QName thisQName = ((OMElement)currentChild).getQName();
                // A null givenName is an indicator to return all elements
                if (givenQName == null || isEqual(givenQName, thisQName)) {
                    isMatchingNodeFound = true;
                    needToMoveForward = false;
                    return;
                }
            }

            // get the next named node
            currentChild = currentChild.getNextOMSibling();
            isMatchingNodeFound = needToMoveForward = !(currentChild == null);
        } else {
            needToMoveForward = false;
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return Returns the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    public Object next() {

        // reset the flags
        needToMoveForward = true;
        isMatchingNodeFound = false;
        nextCalled = true;
        removeCalled = false;
        lastChild = currentChild;
        currentChild = currentChild.getNextOMSibling();
        return lastChild;
    }
    
    /**
     * Prior versions of the OMChildrenQNameIterator used the following
     * logic to check equality.  This algorithm is incorrect; however some customers
     * have dependency on this behavior.  This method is retained (but deprecated) to allow
     * them an opportunity to use the old algorithm.
     * 
     * @param searchQName
     * @param currentQName
     * @return true using legacy equality match
     * @deprecated
     */
    public static boolean isEquals_Legacy(QName searchQName, QName currentQName) {
        
        // if the given localname is null, whatever value this.qname has, its a match. But can one give a QName without a localName ??
        String localPart = searchQName.getLocalPart();
        boolean localNameMatch =(localPart == null) || (localPart.equals("")) ||
            ((currentQName != null) && currentQName.getLocalPart().equals(localPart));
        String namespaceURI = searchQName.getNamespaceURI();
        boolean namespaceURIMatch = (namespaceURI == null) || (namespaceURI.equals(""))||
            ((currentQName != null) && currentQName.getNamespaceURI().equals(namespaceURI));
        return localNameMatch && namespaceURIMatch;
    }
    
    
}
