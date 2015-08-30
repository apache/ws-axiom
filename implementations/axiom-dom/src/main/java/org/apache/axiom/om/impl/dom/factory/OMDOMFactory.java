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

import javax.xml.namespace.QName;

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
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMHierarchyException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
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
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.factory.AxiomNodeFactory;
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
import org.apache.axiom.om.impl.dom.ProcessingInstructionImpl;
import org.apache.axiom.om.impl.dom.TextImpl;
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
import org.apache.axiom.soap.impl.dom.SOAPEnvelopeImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11BodyImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultDetailImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultReasonImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11FaultRoleImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11HeaderBlockImpl;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11HeaderImpl;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12BodyImpl;
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

/**
 * OM factory implementation for DOOM. It creates nodes that implement
 * DOM as defined by the interfaces in {@link org.w3c.dom}.
 */
public class OMDOMFactory implements AxiomNodeFactory, DOMNodeFactory {
    private final OMDOMMetaFactory metaFactory;

    public OMDOMFactory(OMDOMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }

    public OMDOMFactory() {
        this(new OMDOMMetaFactory());
    }

    public OMMetaFactory getMetaFactory() {
        return metaFactory;
    }

    public OMSourcedElement createOMElement(OMDataSource source) {
        throw new UnsupportedOperationException("Not supported for DOM");
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMFactory#createOMElement(org.apache.axiom.om.OMDataSource, java.lang.String, org.apache.axiom.om.OMNamespace)
     */
    public OMSourcedElement createOMElement(OMDataSource source, String localName, OMNamespace ns) {
        throw new UnsupportedOperationException("Not supported for DOM");
    }

    /**
     * Unsupported.
     */
    public OMSourcedElement createOMElement(OMDataSource source, QName qname) {
        throw new UnsupportedOperationException("Not supported for DOM");
    }

    public OMElement createOMElement(String localName, String namespaceURI, String prefix) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        } else if (namespaceURI.length() == 0) {
            if (prefix != null && prefix.length() > 0) {
                throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
            }
            return createOMElement(localName, null);
        } else {
            return createOMElement(localName, createOMNamespace(namespaceURI, prefix));
        }
    }

    /**
     * Creates a new OMNamespace.
     *
     * @see org.apache.axiom.om.OMFactory#createOMNamespace(String, String)
     */
    public OMNamespace createOMNamespace(String uri, String prefix) {
        return new OMNamespaceImpl(uri, prefix);
    }

    public final void validateOMTextParent(OMContainer parent) {
        if (parent instanceof DocumentImpl) {
            throw new OMHierarchyException(
                    "DOM doesn't support text nodes as children of a document");
        }
    }
    
    /**
     * This method is intended only to be used by Axiom intenals when merging Objects from different
     * Axiom implementations to the DOOM implementation.
     *
     * @param child
     */
    public OMNode importNode(OMNode child) {
        int type = child.getType();
        switch (type) {
            case (OMNode.ELEMENT_NODE): {
                OMElement childElement = (OMElement) child;
                OMElement newElement = (new StAXOMBuilder(this,
                                                          childElement.getXMLStreamReader()))
                        .getDocumentElement();
                newElement.build();
                return newElement;
            }
            case (OMNode.TEXT_NODE): {
                OMText importedText = (OMText) child;
                OMText newText;
                if (importedText.isBinary()) {
                    boolean isOptimize = importedText.isOptimized();
                    newText = createOMText(importedText
                            .getDataHandler(), isOptimize);
                } else if (importedText.isCharacters()) {
                    newText = createOMText(null, importedText.getTextCharacters(), OMNode.TEXT_NODE);
                } else {
                    newText = createOMText(importedText.getText());
                }
                return newText;
            }

            case (OMNode.PI_NODE): {
                OMProcessingInstruction importedPI = (OMProcessingInstruction) child;
                OMProcessingInstruction newPI =
                        createOMProcessingInstruction(null, importedPI.getTarget(),
                                                       importedPI.getValue());
                return newPI;
            }
            case (OMNode.COMMENT_NODE): {
                OMComment importedComment = (OMComment) child;
                return createOMComment(null, importedComment.getValue());
            }
            case (OMNode.DTD_NODE): {
                OMDocType importedDocType = (OMDocType) child;
                return createOMDocType(null, importedDocType.getRootName(),
                        importedDocType.getPublicId(), importedDocType.getSystemId(),
                        importedDocType.getInternalSubset());
            }
            default: {
                throw new UnsupportedOperationException(
                        "Not Implemented Yet for the given node type");
            }
        }
    }

    public final <T extends CoreNode> T createNode(Class<T> type) {
        CoreNode node;
        if (type == CoreCDATASection.class || type == AxiomCDATASection.class || type == DOMCDATASection.class) {
            node = new CDATASectionImpl(this);
        } else if (type == CoreCharacterDataNode.class || type == AxiomCharacterDataNode.class || type == DOMText.class) {
            node = new TextImpl(this);
        } else if (type == CoreComment.class || type == AxiomComment.class || type == DOMComment.class) {
            node = new CommentImpl(this);
        } else if (type == CoreDocument.class || type == AxiomDocument.class || type == DOMDocument.class) {
            node = new DocumentImpl(this);
        } else if (type == CoreDocumentFragment.class || type == DOMDocumentFragment.class) {
            node = new DocumentFragmentImpl(this);
        } else if (type == CoreDocumentTypeDeclaration.class || type == AxiomDocType.class || type == DOMDocumentType.class) {
            node = new DocumentTypeImpl(this);
        } else if (type == CoreEntityReference.class || type == AxiomEntityReference.class || type == DOMEntityReference.class) {
            node = new EntityReferenceImpl(this);
        } else if (type == CoreNamespaceDeclaration.class || type == AxiomNamespaceDeclaration.class || type == DOMNamespaceDeclaration.class) {
            node = new NamespaceDeclaration(this);
        } else if (type == CoreNSAwareAttribute.class || type == AxiomAttribute.class || type == DOMNSAwareAttribute.class) {
            node = new NSAwareAttribute(this);
        } else if (type == CoreNSAwareElement.class || type == AxiomElement.class || type == DOMNSAwareElement.class) {
            node = new NSAwareElement(this);
        } else if (type == CoreNSUnawareAttribute.class || type == DOMNSUnawareAttribute.class) {
            node = new NSUnawareAttribute(this);
        } else if (type == CoreNSUnawareElement.class || type == DOMNSUnawareElement.class) {
            node = new NSUnawareElement(this);
        } else if (type == CoreProcessingInstruction.class || type == AxiomProcessingInstruction.class || type == DOMProcessingInstruction.class) {
            node = new ProcessingInstructionImpl(this);
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
