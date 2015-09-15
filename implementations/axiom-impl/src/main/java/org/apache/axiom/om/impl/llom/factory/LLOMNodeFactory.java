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
package org.apache.axiom.om.impl.llom.factory;

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
import org.apache.axiom.om.impl.common.AxiomSourcedElement;
import org.apache.axiom.om.impl.llom.CDATASectionImpl;
import org.apache.axiom.om.impl.llom.CharacterDataImpl;
import org.apache.axiom.om.impl.llom.NamespaceDeclaration;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.OMCommentImpl;
import org.apache.axiom.om.impl.llom.OMDocTypeImpl;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMEntityReferenceImpl;
import org.apache.axiom.om.impl.llom.OMProcessingInstructionImpl;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Body;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Envelope;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Fault;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultCode;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultDetail;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultReason;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultRole;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Header;
import org.apache.axiom.soap.impl.common.AxiomSOAP11HeaderBlock;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Body;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Envelope;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Fault;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultCode;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultDetail;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultReason;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultRole;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultSubCode;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultText;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultValue;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Header;
import org.apache.axiom.soap.impl.common.AxiomSOAP12HeaderBlock;
import org.apache.axiom.soap.impl.common.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.llom.SOAPMessageImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11BodyImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11EnvelopeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12BodyImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12EnvelopeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultNodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultTextImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultValueImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12HeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12HeaderImpl;

public final class LLOMNodeFactory implements NodeFactory {
    public static LLOMNodeFactory INSTANCE = new LLOMNodeFactory();
    
    private LLOMNodeFactory() {}

    public <T extends CoreNode> T createNode(Class<T> type) {
        CoreNode node;
        if (type == CoreCDATASection.class || type == AxiomCDATASection.class) {
            node = new CDATASectionImpl();
        } else if (type == CoreCharacterDataNode.class || type == AxiomCharacterDataNode.class) {
            node = new CharacterDataImpl();
        } else if (type == CoreComment.class || type == AxiomComment.class) {
            node = new OMCommentImpl();
        } else if (type == CoreDocument.class || type == AxiomDocument.class) {
            node = new OMDocumentImpl();
        } else if (type == CoreDocumentTypeDeclaration.class || type == AxiomDocType.class) {
            node = new OMDocTypeImpl();
        } else if (type == CoreEntityReference.class || type == AxiomEntityReference.class) {
            node = new OMEntityReferenceImpl();
        } else if (type == CoreNamespaceDeclaration.class || type == AxiomNamespaceDeclaration.class) {
            node = new NamespaceDeclaration();
        } else if (type == CoreNSAwareAttribute.class || type == AxiomAttribute.class) {
            node = new OMAttributeImpl();
        } else if (type == CoreNSAwareElement.class || type == AxiomElement.class) {
            node = new OMElementImpl();
        } else if (type == CoreProcessingInstruction.class || type == AxiomProcessingInstruction.class) {
            node = new OMProcessingInstructionImpl();
        } else if (type == AxiomSourcedElement.class) {
            node = new OMSourcedElementImpl();
        } else if (type == AxiomSOAPMessage.class) {
            node = new SOAPMessageImpl();
        } else if (type == AxiomSOAP11Envelope.class) {
            node = new SOAP11EnvelopeImpl();
        } else if (type == AxiomSOAP12Envelope.class) {
            node = new SOAP12EnvelopeImpl();
        } else if (type == AxiomSOAP11Header.class) {
            node = new SOAP11HeaderImpl();
        } else if (type == AxiomSOAP12Header.class) {
            node = new SOAP12HeaderImpl();
        } else if (type == AxiomSOAP11HeaderBlock.class) {
            node = new SOAP11HeaderBlockImpl();
        } else if (type == AxiomSOAP12HeaderBlock.class) {
            node = new SOAP12HeaderBlockImpl();
        } else if (type == AxiomSOAP11Body.class) {
            node = new SOAP11BodyImpl();
        } else if (type == AxiomSOAP12Body.class) {
            node = new SOAP12BodyImpl();
        } else if (type == AxiomSOAP11Fault.class) {
            node = new SOAP11FaultImpl();
        } else if (type == AxiomSOAP12Fault.class) {
            node = new SOAP12FaultImpl();
        } else if (type == AxiomSOAP11FaultCode.class) {
            node = new SOAP11FaultCodeImpl();
        } else if (type == AxiomSOAP12FaultCode.class) {
            node = new SOAP12FaultCodeImpl();
        } else if (type == AxiomSOAP12FaultValue.class) {
            node = new SOAP12FaultValueImpl();
        } else if (type == AxiomSOAP12FaultSubCode.class) {
            node = new SOAP12FaultSubCodeImpl();
        } else if (type == AxiomSOAP11FaultReason.class) {
            node = new SOAP11FaultReasonImpl();
        } else if (type == AxiomSOAP12FaultReason.class) {
            node = new SOAP12FaultReasonImpl();
        } else if (type == AxiomSOAP12FaultText.class) {
            node = new SOAP12FaultTextImpl();
        } else if (type == AxiomSOAP12FaultNode.class) {
            node = new SOAP12FaultNodeImpl();
        } else if (type == AxiomSOAP11FaultRole.class) {
            node = new SOAP11FaultRoleImpl();
        } else if (type == AxiomSOAP12FaultRole.class) {
            node = new SOAP12FaultRoleImpl();
        } else if (type == AxiomSOAP11FaultDetail.class) {
            node = new SOAP11FaultDetailImpl();
        } else if (type == AxiomSOAP12FaultDetail.class) {
            node = new SOAP12FaultDetailImpl();
        } else {
            throw new IllegalArgumentException();
        }
        return type.cast(node);
    }
}
