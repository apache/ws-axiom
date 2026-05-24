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
    DOCUMENT(NodeFactory2::createDocument),

    /** The node is a {@link CoreDocumentTypeDeclaration}. */
    DOCUMENT_TYPE_DECLARATION(NodeFactory2::createDocumentTypeDeclaration),

    /** The node is a {@link CoreNSUnawareElement}. */
    NS_UNAWARE_ELEMENT(NodeFactory2::createNSUnawareElement),

    /** The node is a {@link CoreNSAwareElement}. */
    NS_AWARE_ELEMENT(NodeFactory2::createNSAwareElement),

    /** The node is a {@link CoreNSUnawareAttribute}. */
    NS_UNAWARE_ATTRIBUTE(NodeFactory2::createNSUnawareAttribute),

    /** The node is a {@link CoreNSAwareAttribute}. */
    NS_AWARE_ATTRIBUTE(NodeFactory2::createNSAwareAttribute),

    /** The node is a {@link CoreNamespaceDeclaration}. */
    NAMESPACE_DECLARATION(NodeFactory2::createNamespaceDeclaration),

    /** The node is a {@link CoreProcessingInstruction}. */
    PROCESSING_INSTRUCTION(NodeFactory2::createProcessingInstruction),

    /** The node is a {@link CoreDocumentFragment}. */
    DOCUMENT_FRAGMENT(NodeFactory2::createDocumentFragment),

    /** The node is a {@link CoreCharacterDataNode}. */
    CHARACTER_DATA(NodeFactory2::createCharacterDataNode),

    /** The node is a {@link CoreComment}. */
    COMMENT(NodeFactory2::createComment),

    /** The node is a {@link CoreCDATASection}. */
    CDATA_SECTION(NodeFactory2::createCDATASection),

    /** The node is a {@link CoreEntityReference}. */
    ENTITY_REFERENCE(NodeFactory2::createEntityReference);

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

    private final Function<NodeFactory2, CoreNode> factory2Function;
    private EnumSet<NodeType> allowedChildTypes;

    private NodeType(Function<NodeFactory2, CoreNode> factory2Function) {
        this.factory2Function = factory2Function;
    }

    public CoreNode newInstance(NodeFactory factory) {
        return factory2Function.apply(factory.getFactory2());
    }

    public boolean isChildTypeAllowed(NodeType childType) {
        if (allowedChildTypes == null) {
            throw new UnsupportedOperationException();
        }
        return allowedChildTypes.contains(childType);
    }
}
