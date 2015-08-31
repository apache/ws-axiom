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

public enum NodeType {
    /**
     * The node is a {@link CoreDocument}.
     */
    DOCUMENT,
    
    /**
     * The node is a {@link CoreDocumentTypeDeclaration}.
     */
    DOCUMENT_TYPE_DECLARATION,
    
    /**
     * The node is a {@link CoreNSUnawareElement}.
     */
    NS_UNAWARE_ELEMENT,
    
    /**
     * The node is a {@link CoreNSAwareElement}.
     */
    NS_AWARE_ELEMENT,
    
    /**
     * The node is a {@link CoreNSUnawareAttribute}.
     */
    NS_UNAWARE_ATTRIBUTE,
    
    /**
     * The node is a {@link CoreNSAwareAttribute}.
     */
    NS_AWARE_ATTRIBUTE,
    
    /**
     * The node is a {@link CoreNamespaceDeclaration}.
     */
    NAMESPACE_DECLARATION,
    
    /**
     * The node is a {@link CoreProcessingInstruction}.
     */
    PROCESSING_INSTRUCTION,
    
    /**
     * The node is a {@link CoreDocumentFragment}.
     */
    DOCUMENT_FRAGMENT,
    
    /**
     * The node is a {@link CoreCharacterDataNode}.
     */
    CHARACTER_DATA,
    
    /**
     * The node is a {@link CoreComment}.
     */
    COMMENT,
    
    /**
     * The node is a {@link CoreCDATASection}.
     */
    CDATA_SECTION,
    
    /**
     * The node is a {@link CoreEntityReference}.
     */
    ENTITY_REFERENCE
}
