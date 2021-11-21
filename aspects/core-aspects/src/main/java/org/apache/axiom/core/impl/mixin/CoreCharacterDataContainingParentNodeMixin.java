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

import org.apache.axiom.core.CoreCharacterDataContainingParentNode;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.ElementAction;

@org.apache.axiom.weaver.annotation.Mixin(CoreCharacterDataContainingParentNode.class)
public abstract class CoreCharacterDataContainingParentNodeMixin implements CoreCharacterDataContainingParentNode {
    public final Object coreGetCharacterData() throws CoreModelException {
        Object characterData = internalGetCharacterData(ElementAction.RETURN_NULL);
        if (characterData == null) {
            throw new IllegalStateException();
        }
        return characterData;
    }
}
