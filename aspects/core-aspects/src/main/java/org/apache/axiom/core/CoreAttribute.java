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

public interface CoreAttribute extends CoreCharacterDataContainingParentNode {
    /**
     * Get the owner element of this attribute.
     * 
     * @return the owner element of the attribute
     */
    CoreElement coreGetOwnerElement();
    
    /**
     * Check if this node has an owner element.
     * 
     * @return <code>true</code> if and only if this node currently has an owner element
     */
    boolean coreHasOwnerElement();
    
    /**
     * Remove this attribute from its owner element.
     * 
     * @param semantics
     *            the API semantics to use; determines the new owner document for the attribute
     * @return <code>true</code> if the attribute had an owner element and has been removed from
     *         that element; <code>false</code> if the attribute didn't have an owner element and no
     *         changes have been made
     */
    boolean coreRemove(Semantics semantics);
    
    /**
     * Get the attribute immediately following the current attribute.
     * 
     * @return the next attribute, or <code>null</code> if the attribute is the last attribute of
     *         its owner element or if the attribute has no owner element
     */
    CoreAttribute coreGetNextAttribute();
    
    /**
     * Get the attribute immediately preceding the current attribute.
     * 
     * @return the previous attribute, or <code>null</code> if the attribute is the first attribute
     *         of its owner element or if the attribute has no owner element
     */
    CoreAttribute coreGetPreviousAttribute();

    boolean coreGetSpecified();

    void coreSetSpecified(boolean specified);

    void internalSetOwnerElement(CoreElement element);
    void internalUnsetOwnerElement(CoreDocument newOwnerDocument);
    void internalSetNextAttribute(CoreAttribute nextAttribute);
    void internalInsertAttributeAfter(CoreAttribute attr);
    boolean internalRemove(Semantics semantics, CoreElement newOwner);
}
