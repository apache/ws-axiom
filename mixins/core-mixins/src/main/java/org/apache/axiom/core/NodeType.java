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

import java.util.EnumSet;
import java.util.function.Function;

public enum NodeType {
    /** The node is a {@link CoreDocument}. */
    DOCUMENT(NodeFactory::createDocument),

    /** The node is a {@link CoreDocumentTypeDeclaration}. */
    DOCUMENT_TYPE_DECLARATION(NodeFactory::createDocumentTypeDeclaration),

    /** The node is a {@link CoreNSUnawareElement}. */
    NS_UNAWARE_ELEMENT(NodeFactory::createNSUnawareElement),

    /** The node is a {@link CoreNSAwareElement}. */
    NS_AWARE_ELEMENT(NodeFactory::createNSAwareElement),

    /** The node is a {@link CoreNSUnawareAttribute}. */
    NS_UNAWARE_ATTRIBUTE(NodeFactory::createNSUnawareAttribute),

    /** The node is a {@link CoreNSAwareAttribute}. */
    NS_AWARE_ATTRIBUTE(NodeFactory::createNSAwareAttribute),

    /** The node is a {@link CoreNamespaceDeclaration}. */
    NAMESPACE_DECLARATION(NodeFactory::createNamespaceDeclaration),

    /** The node is a {@link CoreProcessingInstruction}. */
    PROCESSING_INSTRUCTION(NodeFactory::createProcessingInstruction),

    /** The node is a {@link CoreDocumentFragment}. */
    DOCUMENT_FRAGMENT(NodeFactory::createDocumentFragment),

    /** The node is a {@link CoreCharacterDataNode}. */
    CHARACTER_DATA(NodeFactory::createCharacterDataNode),

    /** The node is a {@link CoreComment}. */
    COMMENT(NodeFactory::createComment),

    /** The node is a {@link CoreCDATASection}. */
    CDATA_SECTION(NodeFactory::createCDATASection),

    /** The node is a {@link CoreEntityReference}. */
    ENTITY_REFERENCE(NodeFactory::createEntityReference);

    static {
        // TODO: add missing node types here (once we have tests that exercise the code)
        COMMENT.allowedChildTypes = EnumSet.of(CHARACTER_DATA);
        DOCUMENT.allowedChildTypes = EnumSet.of(
                CHARACTER_DATA,
                COMMENT,
                DOCUMENT_TYPE_DECLARATION,
                NS_AWARE_ELEMENT,
                NS_UNAWARE_ELEMENT,
                PROCESSING_INSTRUCTION);
        DOCUMENT_FRAGMENT.allowedChildTypes = EnumSet.allOf(NodeType.class);
        EnumSet<NodeType> s = EnumSet.of(CHARACTER_DATA, ENTITY_REFERENCE);
        NS_AWARE_ATTRIBUTE.allowedChildTypes = s;
        NS_UNAWARE_ATTRIBUTE.allowedChildTypes = s;
        NAMESPACE_DECLARATION.allowedChildTypes = s;
        s = EnumSet.of(
                CDATA_SECTION,
                CHARACTER_DATA,
                COMMENT,
                ENTITY_REFERENCE,
                NS_AWARE_ELEMENT,
                NS_UNAWARE_ELEMENT,
                PROCESSING_INSTRUCTION);
        NS_AWARE_ELEMENT.allowedChildTypes = s;
        NS_UNAWARE_ELEMENT.allowedChildTypes = s;
    }

    private final Function<NodeFactory, CoreNode> factory2Function;
    private EnumSet<NodeType> allowedChildTypes;

    private NodeType(Function<NodeFactory, CoreNode> factory2Function) {
        this.factory2Function = factory2Function;
    }

    public CoreNode newInstance(NodeFactory factory) {
        return factory2Function.apply(factory);
    }

    public boolean isChildTypeAllowed(NodeType childType) {
        if (allowedChildTypes == null) {
            throw new UnsupportedOperationException();
        }
        return allowedChildTypes.contains(childType);
    }
}
