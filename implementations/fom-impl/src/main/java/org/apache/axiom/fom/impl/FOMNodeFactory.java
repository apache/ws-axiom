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
package org.apache.axiom.fom.impl;

import org.apache.abdera.parser.stax.FOMAttribute;
import org.apache.abdera.parser.stax.FOMCDATASection;
import org.apache.abdera.parser.stax.FOMCategories;
import org.apache.abdera.parser.stax.FOMCategory;
import org.apache.abdera.parser.stax.FOMCharacterDataNode;
import org.apache.abdera.parser.stax.FOMCollection;
import org.apache.abdera.parser.stax.FOMComment;
import org.apache.abdera.parser.stax.FOMContent;
import org.apache.abdera.parser.stax.FOMControl;
import org.apache.abdera.parser.stax.FOMDateTime;
import org.apache.abdera.parser.stax.FOMDiv;
import org.apache.abdera.parser.stax.FOMDocType;
import org.apache.abdera.parser.stax.FOMDocument;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.parser.stax.FOMEntityReference;
import org.apache.abdera.parser.stax.FOMEntry;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.parser.stax.FOMFeed;
import org.apache.abdera.parser.stax.FOMGenerator;
import org.apache.abdera.parser.stax.FOMIRI;
import org.apache.abdera.parser.stax.FOMLink;
import org.apache.abdera.parser.stax.FOMMultipartCollection;
import org.apache.abdera.parser.stax.FOMNamespaceDeclaration;
import org.apache.abdera.parser.stax.FOMPerson;
import org.apache.abdera.parser.stax.FOMProcessingInstruction;
import org.apache.abdera.parser.stax.FOMService;
import org.apache.abdera.parser.stax.FOMSource;
import org.apache.abdera.parser.stax.FOMText;
import org.apache.abdera.parser.stax.FOMWorkspace;
import org.apache.axiom.core.CoreCDATASection;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreComment;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreDocumentTypeDeclaration;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreProcessingInstruction;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.impl.common.AxiomAttribute;
import org.apache.axiom.om.impl.common.AxiomCDATASection;
import org.apache.axiom.om.impl.common.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.common.AxiomComment;
import org.apache.axiom.om.impl.common.AxiomDocType;
import org.apache.axiom.om.impl.common.AxiomDocument;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomEntityReference;
import org.apache.axiom.om.impl.common.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.common.AxiomProcessingInstruction;

public final class FOMNodeFactory implements NodeFactory {
    public static final FOMNodeFactory INSTANCE = new FOMNodeFactory();
    
    private FOMNodeFactory() {}
    
    public <T extends CoreNode> T createNode(Class<T> type) {
        CoreNode node;
        if (type == CoreCDATASection.class || type == AxiomCDATASection.class || type == FOMCDATASection.class) {
            node = new FOMCDATASection();
        } else if (type == CoreCharacterDataNode.class || type == AxiomCharacterDataNode.class || type == FOMCharacterDataNode.class) {
            node = new FOMCharacterDataNode();
        } else if (type == CoreComment.class || type == AxiomComment.class || type == FOMComment.class) {
            node = new FOMComment();
        } else if (type == CoreDocument.class || type == AxiomDocument.class || type == FOMDocument.class) {
            node = new FOMDocument();
        } else if (type == CoreDocumentTypeDeclaration.class || type == AxiomDocType.class) {
            node = new FOMDocType();
        } else if (type == CoreEntityReference.class || type == AxiomEntityReference.class) {
            node = new FOMEntityReference();
        } else if (type == CoreNamespaceDeclaration.class || type == AxiomNamespaceDeclaration.class) {
            node = new FOMNamespaceDeclaration();
        } else if (type == CoreNSAwareAttribute.class || type == AxiomAttribute.class || type == FOMAttribute.class) {
            node = new FOMAttribute();
        } else if (type == CoreNSAwareElement.class || type == AxiomElement.class || type == FOMElement.class) {
            node = new FOMElement();
        } else if (type == CoreProcessingInstruction.class || type == AxiomProcessingInstruction.class || type == FOMProcessingInstruction.class) {
            node = new FOMProcessingInstruction();
        } else if (type == FOMCategories.class) {
            node = new FOMCategories();
        } else if (type == FOMCategory.class) {
            node = new FOMCategory();
        } else if (type == FOMCollection.class) {
            node = new FOMCollection();
        } else if (type == FOMContent.class) {
            node = new FOMContent();
        } else if (type == FOMControl.class) {
            node = new FOMControl();
        } else if (type == FOMDateTime.class) {
            node = new FOMDateTime();
        } else if (type == FOMDiv.class) {
            node = new FOMDiv();
        } else if (type == FOMEntry.class) {
            node = new FOMEntry();
        } else if (type == FOMExtensibleElement.class) {
            node = new FOMExtensibleElement();
        } else if (type == FOMFeed.class) {
            node = new FOMFeed();
        } else if (type == FOMGenerator.class) {
            node = new FOMGenerator();
        } else if (type == FOMIRI.class) {
            node = new FOMIRI();
        } else if (type == FOMLink.class) {
            node = new FOMLink();
        } else if (type == FOMMultipartCollection.class) {
            node = new FOMMultipartCollection();
        } else if (type == FOMPerson.class) {
            node = new FOMPerson();
        } else if (type == FOMService.class) {
            node = new FOMService();
        } else if (type == FOMSource.class) {
            node = new FOMSource();
        } else if (type == FOMText.class) {
            node = new FOMText();
        } else if (type == FOMWorkspace.class) {
            node = new FOMWorkspace();
        } else {
            throw new IllegalArgumentException(type.getName() + " not supported");
        }
        return type.cast(node);
    }
}
