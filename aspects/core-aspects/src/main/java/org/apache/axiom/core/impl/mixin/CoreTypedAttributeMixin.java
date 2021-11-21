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
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreTypedAttribute;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(CoreTypedAttribute.class)
public abstract class CoreTypedAttributeMixin implements CoreTypedAttribute {
    private String type;
    
    @Override
    public final String coreGetType() {
        return type;
    }
    
    @Override
    public final void coreSetType(String type) {
        this.type = type;
    }
    
    @Override
    public final <T> void init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreTypedAttribute o = (CoreTypedAttribute)other;
        initName(o);
        coreSetType(o.coreGetType());
    }
}
