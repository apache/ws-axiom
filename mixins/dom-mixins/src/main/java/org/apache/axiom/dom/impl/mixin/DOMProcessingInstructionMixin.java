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
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMProcessingInstruction;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.Node;

@Mixin
public abstract class DOMProcessingInstructionMixin implements DOMProcessingInstruction {
    @Override
    public final short getNodeType() {
        return Node.PROCESSING_INSTRUCTION_NODE;
    }

    @Override
    public final String getTarget() {
        return coreGetTarget();
    }

    @Override
    public final String getData() {
        try {
            return coreGetCharacterData().toString();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final void setData(String data) {
        try {
            coreSetCharacterData(data, DOMSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final String getNodeName() {
        return coreGetTarget();
    }

    @Override
    public final String getNodeValue() {
        return getData();
    }

    @Override
    public final void setNodeValue(String nodeValue) {
        setData(nodeValue);
    }
}
