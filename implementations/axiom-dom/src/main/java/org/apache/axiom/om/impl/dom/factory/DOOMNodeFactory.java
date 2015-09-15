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
package org.apache.axiom.om.impl.dom.factory;

import org.apache.axiom.core.CoreCDATASection;
import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreComment;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreDocumentTypeDeclaration;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNSUnawareAttribute;
import org.apache.axiom.core.CoreNSUnawareElement;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreProcessingInstruction;
import org.apache.axiom.dom.DOMCDATASection;
import org.apache.axiom.dom.DOMComment;
import org.apache.axiom.dom.DOMDocument;
import org.apache.axiom.dom.DOMDocumentFragment;
import org.apache.axiom.dom.DOMDocumentType;
import org.apache.axiom.dom.DOMEntityReference;
import org.apache.axiom.dom.DOMNSAwareAttribute;
import org.apache.axiom.dom.DOMNSAwareElement;
import org.apache.axiom.dom.DOMNSUnawareAttribute;
import org.apache.axiom.dom.DOMNSUnawareElement;
import org.apache.axiom.dom.DOMNamespaceDeclaration;
import org.apache.axiom.dom.DOMNodeFactory;
import org.apache.axiom.dom.DOMProcessingInstruction;
import org.apache.axiom.dom.DOMText;
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
import org.apache.axiom.om.impl.dom.CDATASectionImpl;
import org.apache.axiom.om.impl.dom.CommentImpl;
import org.apache.axiom.om.impl.dom.DocumentFragmentImpl;
import org.apache.axiom.om.impl.dom.DocumentImpl;
import org.apache.axiom.om.impl.dom.DocumentTypeImpl;
import org.apache.axiom.om.impl.dom.EntityReferenceImpl;
import org.apache.axiom.om.impl.dom.NSAwareAttribute;
import org.apache.axiom.om.impl.dom.NSAwareElement;
import org.apache.axiom.om.impl.dom.NSUnawareAttribute;
import org.apache.axiom.om.impl.dom.NSUnawareElement;
import org.apache.axiom.om.impl.dom.NamespaceDeclaration;
import org.apache.axiom.om.impl.dom.OMSourcedElementImpl;
import org.apache.axiom.om.impl.dom.ProcessingInstructionImpl;
import org.apache.axiom.om.impl.dom.TextImpl;
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
import org.apache.axiom.soap.impl.dom.SOAPMessageImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11BodyImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11EnvelopeImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultDetailImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultReasonImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultRoleImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11HeaderBlockImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11HeaderImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12BodyImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12EnvelopeImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultCodeImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultDetailImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultNodeImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultReasonImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultRoleImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultSubCodeImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultTextImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12FaultValueImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12HeaderBlockImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12HeaderImpl;

public final class DOOMNodeFactory implements DOMNodeFactory {
    public static final DOOMNodeFactory INSTANCE = new DOOMNodeFactory();
    
    private DOOMNodeFactory() {}

    public final <T extends CoreNode> T createNode(Class<T> type) {
        CoreNode node;
        if (type == CoreCDATASection.class || type == AxiomCDATASection.class || type == DOMCDATASection.class) {
            node = new CDATASectionImpl();
        } else if (type == CoreCharacterDataNode.class || type == AxiomCharacterDataNode.class || type == DOMText.class) {
            node = new TextImpl();
        } else if (type == CoreComment.class || type == AxiomComment.class || type == DOMComment.class) {
            node = new CommentImpl();
        } else if (type == CoreDocument.class || type == AxiomDocument.class || type == DOMDocument.class) {
            node = new DocumentImpl();
        } else if (type == CoreDocumentFragment.class || type == DOMDocumentFragment.class) {
            node = new DocumentFragmentImpl();
        } else if (type == CoreDocumentTypeDeclaration.class || type == AxiomDocType.class || type == DOMDocumentType.class) {
            node = new DocumentTypeImpl();
        } else if (type == CoreEntityReference.class || type == AxiomEntityReference.class || type == DOMEntityReference.class) {
            node = new EntityReferenceImpl();
        } else if (type == CoreNamespaceDeclaration.class || type == AxiomNamespaceDeclaration.class || type == DOMNamespaceDeclaration.class) {
            node = new NamespaceDeclaration();
        } else if (type == CoreNSAwareAttribute.class || type == AxiomAttribute.class || type == DOMNSAwareAttribute.class) {
            node = new NSAwareAttribute();
        } else if (type == CoreNSAwareElement.class || type == AxiomElement.class || type == DOMNSAwareElement.class) {
            node = new NSAwareElement();
        } else if (type == CoreNSUnawareAttribute.class || type == DOMNSUnawareAttribute.class) {
            node = new NSUnawareAttribute();
        } else if (type == CoreNSUnawareElement.class || type == DOMNSUnawareElement.class) {
            node = new NSUnawareElement();
        } else if (type == CoreProcessingInstruction.class || type == AxiomProcessingInstruction.class || type == DOMProcessingInstruction.class) {
            node = new ProcessingInstructionImpl();
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
