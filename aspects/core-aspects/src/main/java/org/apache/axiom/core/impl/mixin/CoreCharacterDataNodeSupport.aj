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

public aspect CoreCharacterDataNodeSupport {
    /**
     * Either a {@link String} or a {@link CharacterData} object.
     */
    private Object CoreCharacterDataNode.data;
    
    public final NodeType CoreCharacterDataNode.coreGetNodeType() {
        return NodeType.CHARACTER_DATA;
    }
    
    public final boolean CoreCharacterDataNode.coreIsIgnorable() {
        return getFlag(Flags.IGNORABLE);
    }
    
    public final void CoreCharacterDataNode.coreSetIgnorable(boolean ignorable) {
        setFlag(Flags.IGNORABLE, ignorable);
    }
    
    public final Object CoreCharacterDataNode.coreGetCharacterData() {
        return data == null ? "" : data;
    }
    
    public final void CoreCharacterDataNode.coreSetCharacterData(Object data) {
        this.data = data;
    }
    
    public final void CoreCharacterDataNode.coreSetCharacterData(Object data, Semantics semantics) {
        this.data = data;
    }
    
    public final <T> void CoreCharacterDataNode.init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreCharacterDataNode o = (CoreCharacterDataNode)other;
        data = o.data instanceof CloneableCharacterData ? ((CloneableCharacterData)o.data).clone(policy, options) : o.data;
        coreSetIgnorable(o.coreIsIgnorable());
    }

    public final void CoreCharacterDataNode.internalSerialize(XmlHandler handler, boolean cache) throws StreamException {
        handler.processCharacterData(coreGetCharacterData(), coreIsIgnorable());
    }
}
