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

public aspect CoreCharacterDataNodeSupport {
    /**
     * Either a {@link String} or a {@link CharacterData} object.
     */
    private Object CoreCharacterDataNode.data;
    
    public final int CoreCharacterDataNode.coreGetNodeType() {
        return CHARACTER_DATA_NODE;
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
    
    public final void CoreCharacterDataNode.coreSetCharacterData(Object data, DetachPolicy detachPolicy) {
        this.data = data;
    }
}
