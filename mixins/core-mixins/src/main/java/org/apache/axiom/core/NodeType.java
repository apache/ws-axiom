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

public enum NodeType {
    /** The node is a {@link CoreDocument}. */
    DOCUMENT(CoreDocument.class),

    /** The node is a {@link CoreDocumentTypeDeclaration}. */
    DOCUMENT_TYPE_DECLARATION(CoreDocumentTypeDeclaration.class),

    /** The node is a {@link CoreNSUnawareElement}. */
    NS_UNAWARE_ELEMENT(CoreNSUnawareElement.class),

    /** The node is a {@link CoreNSAwareElement}. */
    NS_AWARE_ELEMENT(CoreNSAwareElement.class),

    /** The node is a {@link CoreNSUnawareAttribute}. */
    NS_UNAWARE_ATTRIBUTE(CoreNSUnawareAttribute.class),

    /** The node is a {@link CoreNSAwareAttribute}. */
    NS_AWARE_ATTRIBUTE(CoreNSAwareAttribute.class),

    /** The node is a {@link CoreNamespaceDeclaration}. */
    NAMESPACE_DECLARATION(CoreNamespaceDeclaration.class),

    /** The node is a {@link CoreProcessingInstruction}. */
    PROCESSING_INSTRUCTION(CoreProcessingInstruction.class),

    /** The node is a {@link CoreDocumentFragment}. */
    DOCUMENT_FRAGMENT(CoreDocumentFragment.class),

    /** The node is a {@link CoreCharacterDataNode}. */
    CHARACTER_DATA(CoreCharacterDataNode.class),

    /** The node is a {@link CoreComment}. */
    COMMENT(CoreComment.class),

    /** The node is a {@link CoreCDATASection}. */
    CDATA_SECTION(CoreCDATASection.class),

    /** The node is a {@link CoreEntityReference}. */
    ENTITY_REFERENCE(CoreEntityReference.class);

    static {
        // TODO: add missing node types here (once we have tests that exercise the code)
        COMMENT.allowedChildTypes = EnumSet.of(CHARACTER_DATA);
        DOCUMENT.allowedChildTypes =
                EnumSet.of(
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
        s =
                EnumSet.of(
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

    private final Class<? extends CoreNode> iface;
    private EnumSet<NodeType> allowedChildTypes;

    private NodeType(Class<? extends CoreNode> iface) {
        this.iface = iface;
    }

    public Class<? extends CoreNode> getInterface() {
        return iface;
    }

    public boolean isChildTypeAllowed(NodeType childType) {
        if (allowedChildTypes == null) {
            throw new UnsupportedOperationException();
        }
        return allowedChildTypes.contains(childType);
    }
}
