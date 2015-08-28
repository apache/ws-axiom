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

public aspect CoreDocumentTypeDeclarationSupport {
    private String CoreDocumentTypeDeclaration.rootName;
    private String CoreDocumentTypeDeclaration.publicId;
    private String CoreDocumentTypeDeclaration.systemId;
    private String CoreDocumentTypeDeclaration.internalSubset;
    
    public final int CoreDocumentTypeDeclaration.coreGetNodeType() {
        return DOCUMENT_TYPE_DECLARATION_NODE;
    }
    
    public final String CoreDocumentTypeDeclaration.coreGetRootName() {
        return rootName;
    }
    
    public final void CoreDocumentTypeDeclaration.coreSetRootName(String rootName) {
        this.rootName = rootName;
    }
    
    public final String CoreDocumentTypeDeclaration.coreGetPublicId() {
        return publicId;
    }
    
    public final void CoreDocumentTypeDeclaration.coreSetPublicId(String publicId) {
        this.publicId = publicId;
    }
    
    public final String CoreDocumentTypeDeclaration.coreGetSystemId() {
        return systemId;
    }
    
    public final void CoreDocumentTypeDeclaration.coreSetSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public final String CoreDocumentTypeDeclaration.coreGetInternalSubset() {
        return internalSubset;
    }
    
    public final void CoreDocumentTypeDeclaration.coreSetInternalSubset(String internalSubset) {
        this.internalSubset = internalSubset;
    }
    
    public final CoreNode CoreDocumentTypeDeclaration.shallowClone(ClonePolicy policy, Object options) {
        CoreDocumentTypeDeclaration clone = coreGetNodeFactory().createDocumentTypeDeclaration();
        clone.coreSetRootName(coreGetRootName());
        clone.coreSetPublicId(coreGetPublicId());
        clone.coreSetSystemId(coreGetSystemId());
        clone.coreSetInternalSubset(coreGetInternalSubset());
        return clone;
    }
}
