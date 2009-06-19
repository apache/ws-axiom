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

package org.apache.axiom.om;

import javax.xml.namespace.QName;
import java.util.Iterator;

/**
 * Captures the operations related to containment shared by both a document and an element.
 * <p/>
 * <p>Exposes the ability to add, find, and iterate over the children of a document or element.</p>
 */
public interface OMContainer extends OMSerializable {

    /**
     * Adds the given node as the last child. One must preserve the order of children, in this
     * operation. Tip : appending the new child is preferred.
     *
     * @param omNode
     */
    // TODO: specify whether the node is removed from its original location
    // TODO: specify what happens if the node has been created by a foreign OMFactory
    void addChild(OMNode omNode);

    /**
     * Returns an iterator for child nodes matching the given QName.
     * <p/>
     *
     * @param elementQName The QName specifying namespace and local name to match.
     * @return Returns an iterator of {@link OMElement} items that match the given QName
     */
    Iterator getChildrenWithName(QName elementQName);
    
    /**
     * Returns an iterator for child nodes matching the local name.
     * <p/>
     *
     * @param localName 
     * @return Returns an iterator of {@link OMElement} items that match the given localName
     */
    Iterator getChildrenWithLocalName(String localName);
    
    /**
     * Returns an iterator for child nodes matching the namespace uri.
     * <p/>
     *
     * @param uri 
     * @return Returns an iterator of {@link OMElement} items that match the given uri
     */
    Iterator getChildrenWithNamespaceURI(String uri);
    

    /**
     * Returns the first child in document order that matches the given QName
     * <p/>
     * <p>The QName filter is applied as in the function {@link #getChildrenWithName}.</p>
     *
     * @param elementQName The QName to use for matching.
     * @return Returns the first element in document order that matches the <tt>elementQName</tt>
     *         criteria.
     * @throws OMException Could indirectly trigger building of child nodes.
     * @see #getChildrenWithName
     */
    OMElement getFirstChildWithName(QName elementQName) throws OMException;

    /**
     * Returns an iterator for the children of the container.
     *
     * @return Returns a {@link Iterator} of children, all of which implement {@link OMNode}.
     * @see #getFirstChildWithName
     * @see #getChildrenWithName
     */
    Iterator getChildren();

    /**
     * Gets the first child.
     *
     * @return Returns the first child.  May return null if the container has no children.
     */
    OMNode getFirstOMChild();

    void buildNext();
}
