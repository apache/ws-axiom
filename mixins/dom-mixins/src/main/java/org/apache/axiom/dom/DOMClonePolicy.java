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
package org.apache.axiom.dom;

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.NodeFactoryException;
import org.apache.axiom.core.NodeType;

abstract class DOMClonePolicy implements ClonePolicy<Void> {
    @Override
    public CoreNode createTargetNode(Void options, CoreNode node, NodeFactory factory) {
        // This is not specified by the API, but it's compatible with versions before
        // 1.2.14
        try {
            return (CoreNode) node.getClass().getConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new NodeFactoryException("Failed to clone node", ex);
        }
    }

    @Override
    public boolean repairNamespaces(Void options) {
        return false;
    }

    @Override
    public boolean cloneAttributes(Void options) {
        return true;
    }

    @Override
    public abstract boolean cloneChildren(Void options, NodeType nodeType);

    @Override
    public void postProcess(Void options, CoreNode clone) {}
}
