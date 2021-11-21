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
package org.apache.axiom.dom.impl.mixin;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMChildNode;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DocumentWhitespaceFilter;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.Node;

@Mixin(DOMChildNode.class)
public abstract class DOMChildNodeMixin implements DOMChildNode {
    @Override
    public final Node getParentNode() {
        return (Node)coreGetParent();
    }
    
    @Override
    public final Node getNextSibling() {
        try {
            return (Node)coreGetNextSibling(DocumentWhitespaceFilter.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }
    
    @Override
    public final Node getPreviousSibling() {
        return (Node)coreGetPreviousSibling(DocumentWhitespaceFilter.INSTANCE);
    }
}
