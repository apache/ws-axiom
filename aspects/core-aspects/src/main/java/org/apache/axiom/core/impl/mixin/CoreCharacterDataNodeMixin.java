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
package org.apache.axiom.core.impl.mixin;

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CloneableCharacterData;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.impl.Flags;
import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;

@org.apache.axiom.weaver.annotation.Mixin(CoreCharacterDataNode.class)
public abstract class CoreCharacterDataNodeMixin implements CoreCharacterDataNode {
    /**
     * Either a {@link String} or a {@link CharacterData} object.
     */
    private Object data;
    
    public final NodeType coreGetNodeType() {
        return NodeType.CHARACTER_DATA;
    }
    
    public final boolean coreIsIgnorable() {
        return internalGetFlag(Flags.IGNORABLE);
    }
    
    public final void coreSetIgnorable(boolean ignorable) {
        internalSetFlag(Flags.IGNORABLE, ignorable);
    }
    
    public final Object coreGetCharacterData() {
        return data == null ? "" : data;
    }
    
    public final void coreSetCharacterData(Object data) {
        this.data = data;
    }
    
    public final void coreSetCharacterData(Object data, Semantics semantics) {
        this.data = data;
    }
    
    public final <T> void init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreCharacterDataNode o = (CoreCharacterDataNode)other;
        Object otherData = o.coreGetCharacterData();
        data = otherData instanceof CloneableCharacterData ? ((CloneableCharacterData)otherData).clone(policy, options) : otherData;
        coreSetIgnorable(o.coreIsIgnorable());
    }

    public final void internalSerialize(XmlHandler handler, boolean cache) throws StreamException {
        handler.processCharacterData(coreGetCharacterData(), coreIsIgnorable());
    }
}
