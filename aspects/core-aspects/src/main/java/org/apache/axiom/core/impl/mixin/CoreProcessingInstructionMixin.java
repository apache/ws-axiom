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
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreProcessingInstruction;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;

@org.apache.axiom.weaver.annotation.Mixin(CoreProcessingInstruction.class)
public abstract class CoreProcessingInstructionMixin implements CoreProcessingInstruction {
    private String target;

    public final NodeType coreGetNodeType() {
        return NodeType.PROCESSING_INSTRUCTION;
    }
    
    public final String coreGetTarget() {
        return target;
    }
    
    public final void coreSetTarget(String target) {
        this.target = target;
    }
    
    public final <T> void init(ClonePolicy<T> policy, T options, CoreNode other) {
        target = ((CoreProcessingInstruction)other).coreGetTarget();
    }
    
    public final void serializeStartEvent(XmlHandler handler) throws CoreModelException, StreamException {
        handler.startProcessingInstruction(coreGetTarget());
    }

    public final void serializeEndEvent(XmlHandler handler) throws StreamException {
        handler.endProcessingInstruction();
    }
}
