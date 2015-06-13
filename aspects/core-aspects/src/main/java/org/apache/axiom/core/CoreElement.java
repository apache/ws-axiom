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

public interface CoreElement extends CoreChildNode, CoreParentNode {
    /**
     * Get the first attribute of this element.
     * 
     * @return the first attribute, or <code>null</code> if this element has no attributes
     */
    CoreAttribute coreGetFirstAttribute();

    /**
     * Get the last attribute of this element.
     * 
     * @return the last attribute, or <code>null</code> if this element has no attributes
     */
    CoreAttribute coreGetLastAttribute();

    /**
     * Append an attribute to this element. The attribute is simply added at the end of the list of
     * attributes for this element. This method should be used with care because no provisions are
     * made to ensure uniqueness of attribute names.
     * 
     * @param attr
     *            the attribute to append
     * @param policy
     *            the policy to apply if the attribute already has an owner element or belongs to a
     *            different document
     * @throws NodeMigrationException
     *             if appending the attribute was rejected by the policy
     */
    void coreAppendAttribute(CoreAttribute attr, NodeMigrationPolicy policy) throws NodeMigrationException;
}
