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

import javax.xml.namespace.QName;

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
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
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
import org.apache.axiom.om.impl.common.factory.AxiomNodeFactory;
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
import org.apache.axiom.soap.impl.common.AxiomSOAP11Fault;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultCode;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultDetail;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultReason;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultRole;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Header;
import org.apache.axiom.soap.impl.common.AxiomSOAP11HeaderBlock;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Body;
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
import org.apache.axiom.soap.impl.common.AxiomSOAPEnvelope;
import org.apache.axiom.soap.impl.common.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;
import org.apache.axiom.soap.impl.llom.SOAPMessageImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11BodyImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12BodyImpl;
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

/** Class OMLinkedListImplFactory
 */
public class OMLinkedListImplFactory implements AxiomNodeFactory {
    private final OMLinkedListMetaFactory metaFactory;
    
    /**
     * For internal use only.
     * 
     * @param metaFactory
     */
    protected OMLinkedListImplFactory(OMLinkedListMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }
    
    /**
     * @deprecated Use {@link OMAbstractFactory#getOMFactory()} to get an instance of this class.
     */
    public OMLinkedListImplFactory() {
        this(new OMLinkedListMetaFactory());
    }

    public OMMetaFactory getMetaFactory() {
        return metaFactory;
    }

    public OMSourcedElement createOMElement(OMDataSource source) {
        return new OMSourcedElementImpl(this, source);
    }

    /**
     * Construct element with arbitrary data source.
     *
     * @param source
     * @param localName
     * @param ns
     */
    public OMSourcedElement createOMElement(OMDataSource source, String localName, OMNamespace ns) {
        return new OMSourcedElementImpl(localName, ns, this, source);
    }

    /**
     * Construct element with arbitrary data source.
     * 
     * @param source the data source
     * @param qname the name of the element produced by the data source
     */
    public OMSourcedElement createOMElement(OMDataSource source, QName qname) {
        return new OMSourcedElementImpl(qname, this, source);
    }

    public <T extends CoreNode> T createNode(Class<T> type) {
        CoreNode node;
        if (type == CoreCDATASection.class || type == AxiomCDATASection.class) {
            node = new CDATASectionImpl(this);
        } else if (type == CoreCharacterDataNode.class || type == AxiomCharacterDataNode.class) {
            node = new CharacterDataImpl(this);
        } else if (type == CoreComment.class || type == AxiomComment.class) {
            node = new OMCommentImpl(this);
        } else if (type == CoreDocument.class || type == AxiomDocument.class) {
            node = new OMDocumentImpl(this);
        } else if (type == CoreDocumentTypeDeclaration.class || type == AxiomDocType.class) {
            node = new OMDocTypeImpl(this);
        } else if (type == CoreEntityReference.class || type == AxiomEntityReference.class) {
            node = new OMEntityReferenceImpl(this);
        } else if (type == CoreNamespaceDeclaration.class || type == AxiomNamespaceDeclaration.class) {
            node = new NamespaceDeclaration(this);
        } else if (type == CoreNSAwareAttribute.class || type == AxiomAttribute.class) {
            node = new OMAttributeImpl(this);
        } else if (type == CoreNSAwareElement.class || type == AxiomElement.class) {
            node = new OMElementImpl(this);
        } else if (type == CoreProcessingInstruction.class || type == AxiomProcessingInstruction.class) {
            node = new OMProcessingInstructionImpl(this);
        } else if (type == AxiomSOAPMessage.class) {
            node = new SOAPMessageImpl(this);
        } else if (type == AxiomSOAPEnvelope.class) {
            node = new SOAPEnvelopeImpl(this);
        } else if (type == AxiomSOAP11Header.class) {
            node = new SOAP11HeaderImpl(this);
        } else if (type == AxiomSOAP12Header.class) {
            node = new SOAP12HeaderImpl(this);
        } else if (type == AxiomSOAP11HeaderBlock.class) {
            node = new SOAP11HeaderBlockImpl(this);
        } else if (type == AxiomSOAP12HeaderBlock.class) {
            node = new SOAP12HeaderBlockImpl(this);
        } else if (type == AxiomSOAP11Body.class) {
            node = new SOAP11BodyImpl(this);
        } else if (type == AxiomSOAP12Body.class) {
            node = new SOAP12BodyImpl(this);
        } else if (type == AxiomSOAP11Fault.class) {
            node = new SOAP11FaultImpl(this);
        } else if (type == AxiomSOAP12Fault.class) {
            node = new SOAP12FaultImpl(this);
        } else if (type == AxiomSOAP11FaultCode.class) {
            node = new SOAP11FaultCodeImpl(this);
        } else if (type == AxiomSOAP12FaultCode.class) {
            node = new SOAP12FaultCodeImpl(this);
        } else if (type == AxiomSOAP12FaultValue.class) {
            node = new SOAP12FaultValueImpl(this);
        } else if (type == AxiomSOAP12FaultSubCode.class) {
            node = new SOAP12FaultSubCodeImpl(this);
        } else if (type == AxiomSOAP11FaultReason.class) {
            node = new SOAP11FaultReasonImpl(this);
        } else if (type == AxiomSOAP12FaultReason.class) {
            node = new SOAP12FaultReasonImpl(this);
        } else if (type == AxiomSOAP12FaultText.class) {
            node = new SOAP12FaultTextImpl(this);
        } else if (type == AxiomSOAP12FaultNode.class) {
            node = new SOAP12FaultNodeImpl(this);
        } else if (type == AxiomSOAP11FaultRole.class) {
            node = new SOAP11FaultRoleImpl(this);
        } else if (type == AxiomSOAP12FaultRole.class) {
            node = new SOAP12FaultRoleImpl(this);
        } else if (type == AxiomSOAP11FaultDetail.class) {
            node = new SOAP11FaultDetailImpl(this);
        } else if (type == AxiomSOAP12FaultDetail.class) {
            node = new SOAP12FaultDetailImpl(this);
        } else {
            throw new IllegalArgumentException();
        }
        return type.cast(node);
    }
}
