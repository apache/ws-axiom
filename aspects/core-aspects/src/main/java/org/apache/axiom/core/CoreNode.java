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

public interface CoreNode {
    /**
     * The node is a {@link CoreDocument}.
     */
    int DOCUMENT_NODE = 0;
    
    /**
     * The node is a {@link CoreDocumentTypeDeclaration}.
     */
    int DOCUMENT_TYPE_DECLARATION_NODE = 1;
    
    /**
     * The node is a {@link CoreNSUnawareElement}.
     */
    int NS_UNAWARE_ELEMENT_NODE = 2;
    
    /**
     * The node is a {@link CoreNSAwareElement}.
     */
    int NS_AWARE_ELEMENT_NODE = 3;
    
    /**
     * The node is a {@link CoreNSUnawareAttribute}.
     */
    int NS_UNAWARE_ATTRIBUTE_NODE = 4;
    
    /**
     * The node is a {@link CoreNSAwareAttribute}.
     */
    int NS_AWARE_ATTRIBUTE_NODE = 5;
    
    /**
     * The node is a {@link CoreNamespaceDeclaration}.
     */
    int NAMESPACE_DECLARATION_NODE = 6;
    
    /**
     * The node is a {@link CoreProcessingInstruction}.
     */
    int PROCESSING_INSTRUCTION_NODE = 7;
    
    /**
     * The node is a {@link CoreDocumentFragment}.
     */
    int DOCUMENT_FRAGMENT_NODE = 8;
    
    /**
     * The node is a {@link CoreCharacterDataNode}.
     */
    int CHARACTER_DATA_NODE = 9;
    
    /**
     * The node is a {@link CoreComment}.
     */
    int COMMENT_NODE = 10;
    
    /**
     * The node is a {@link CoreCDATASection}.
     */
    int CDATA_SECTION_NODE = 11;
    
    /**
     * The node is a {@link CoreEntityReference}.
     */
    int ENTITY_REFERENCE_NODE = 12;
    
    void coreSetOwnerDocument(CoreDocument document);
    
    NodeFactory coreGetNodeFactory();
    
    /**
     * Get the node type.
     * 
     * @return one of the constants defined by {@link CoreNode} identifying the type of node
     */
    int coreGetNodeType();

    /**
     * Clone this node according to the provided policy.
     * 
     * @param policy
     *            the policy to use when cloning this node (and its children)
     * @return the clone of this node
     */
    CoreNode coreClone(ClonePolicy policy, Object options);
}
