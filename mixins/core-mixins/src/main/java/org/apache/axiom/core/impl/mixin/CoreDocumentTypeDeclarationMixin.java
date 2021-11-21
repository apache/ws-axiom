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
import org.apache.axiom.core.CoreDocumentTypeDeclaration;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(CoreDocumentTypeDeclaration.class)
public abstract class CoreDocumentTypeDeclarationMixin implements CoreDocumentTypeDeclaration {
    private String rootName;
    private String publicId;
    private String systemId;
    private String internalSubset;
    
    @Override
    public final NodeType coreGetNodeType() {
        return NodeType.DOCUMENT_TYPE_DECLARATION;
    }
    
    @Override
    public final String coreGetRootName() {
        return rootName;
    }
    
    @Override
    public final void coreSetRootName(String rootName) {
        this.rootName = rootName;
    }
    
    @Override
    public final String coreGetPublicId() {
        return publicId;
    }
    
    @Override
    public final void coreSetPublicId(String publicId) {
        this.publicId = publicId;
    }
    
    @Override
    public final String coreGetSystemId() {
        return systemId;
    }
    
    @Override
    public final void coreSetSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    @Override
    public final String coreGetInternalSubset() {
        return internalSubset;
    }
    
    @Override
    public final void coreSetInternalSubset(String internalSubset) {
        this.internalSubset = internalSubset;
    }
    
    @Override
    public final <T> void init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreDocumentTypeDeclaration o = (CoreDocumentTypeDeclaration)other;
        coreSetRootName(o.coreGetRootName());
        coreSetPublicId(o.coreGetPublicId());
        coreSetSystemId(o.coreGetSystemId());
        coreSetInternalSubset(o.coreGetInternalSubset());
    }

    @Override
    public final void internalSerialize(XmlHandler handler, boolean cache) throws CoreModelException, StreamException {
        handler.processDocumentTypeDeclaration(coreGetRootName(), coreGetPublicId(), coreGetSystemId(), coreGetInternalSubset());
    }
}
