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
package org.apache.axiom.om.impl.dom;

import org.apache.axiom.dom.DOMChildNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.common.AxiomChildNode;

public abstract class ChildNode extends NodeImpl implements DOMChildNode, AxiomChildNode {
    public ChildNode(OMFactory factory) {
        super(factory);
    }

    final NodeImpl clone(OMCloneOptions options, ParentNode targetParent, boolean deep, boolean namespaceRepairing) {
        beforeClone(options);
        ChildNode clone = createClone();
        if (targetParent != null) {
            targetParent.coreAppendChild(clone, false);
        }
        return clone;
    }
    
    void beforeClone(OMCloneOptions options) {
        // By default, do nothing
    }
    
    abstract ChildNode createClone();
}
