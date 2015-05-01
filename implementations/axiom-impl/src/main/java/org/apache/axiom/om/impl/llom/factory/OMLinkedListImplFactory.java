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
import org.apache.axiom.core.CoreCharacterData;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.factory.AxiomNodeFactory;
import org.apache.axiom.om.impl.llom.CDATASectionImpl;
import org.apache.axiom.om.impl.llom.CharacterDataImpl;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.OMCommentImpl;
import org.apache.axiom.om.impl.llom.OMDocTypeImpl;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMEntityReferenceImpl;
import org.apache.axiom.om.impl.llom.OMProcessingInstructionImpl;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

import javax.xml.namespace.QName;

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

    /**
     * Method createOMElement.
     *
     * @param localName
     * @param ns
     * @return Returns OMElement.
     */
    public OMElement createOMElement(String localName, OMNamespace ns) {
        return new OMElementImpl(null, localName, ns, null, this, true);
    }

    public OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent) {
        return new OMElementImpl(parent, localName, ns, null, this, true);
    }

    /**
     * Method createOMElement.
     *
     * @param localName
     * @param parent
     * @param builder
     * @return Returns OMElement.
     */
    public OMElement createOMElement(String localName, OMContainer parent,
                                     OMXMLParserWrapper builder) {
        return new OMElementImpl(parent, localName, null, builder, this, false);
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
     * Create an OMElement with the given QName under the given parent.
     *
     * If the QName contains a prefix, we will ensure that an OMNamespace is created
     * mapping the given namespace to the given prefix.  If no prefix is passed, we'll
     * use whatever's already mapped in the parent, or create a generated one.
     *
     * @param qname the QName of the element to create
     * @param parent the OMContainer in which to place the new element
     * @return Returns the new OMElement
     * @throws OMException if there's a namespace mapping problem
     */
    public OMElement createOMElement(QName qname, OMContainer parent)
            throws OMException {
        return new OMElementImpl(qname, parent, this);
    }

    /**
     * Create an OMElement with the given QName
     * <p/>
     * If the QName contains a prefix, we will ensure that an OMNamespace is created mapping the
     * given namespace to the given prefix.  If no prefix is passed, we'll use whatever's already
     * mapped in the parent, or create a generated one.
     *
     * @param qname
     * @return the new OMElement.
     */
    public OMElement createOMElement(QName qname) throws OMException {
        return new OMElementImpl(qname, null, this);
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

    /**
     * Method createOMNamespace.
     *
     * @param uri
     * @param prefix
     * @return Returns OMNamespace.
     */
    public OMNamespace createOMNamespace(String uri, String prefix) {
        return new OMNamespaceImpl(uri, prefix);
    }

    /**
     * Creates attribute.
     *
     * @param localName
     * @param ns
     * @param value
     * @return Returns OMAttribute.
     */
    public OMAttribute createOMAttribute(String localName,
                                         OMNamespace ns,
                                         String value) {
        if (ns != null && ns.getPrefix() == null) {
            String namespaceURI = ns.getNamespaceURI();
            if (namespaceURI.length() == 0) {
                ns = null;
            } else {
                ns = new OMNamespaceImpl(namespaceURI, OMSerializerUtil.getNextNSPrefix());
            }
        }
        return new OMAttributeImpl(localName, ns, value, this);
    }

    public OMDocType createOMDocType(OMContainer parent, String rootName, String publicId,
            String systemId, String internalSubset) {
        return createOMDocType(parent, rootName, publicId, systemId, internalSubset, false);
    }

    public OMDocType createOMDocType(OMContainer parent, String rootName, String publicId,
            String systemId, String internalSubset, boolean fromBuilder) {
        return new OMDocTypeImpl(parent, rootName, publicId, systemId, internalSubset, this, fromBuilder);
    }

    /**
     * Creates a PI.
     *
     * @param parent
     * @param piTarget
     * @param piData
     * @return Returns OMProcessingInstruction.
     */
    public OMProcessingInstruction createOMProcessingInstruction(OMContainer parent,
                                                                 String piTarget, String piData) {
        return createOMProcessingInstruction(parent, piTarget, piData, false);
    }

    public OMProcessingInstruction createOMProcessingInstruction(OMContainer parent,
            String piTarget, String piData, boolean fromBuilder) {
        return new OMProcessingInstructionImpl(parent, piTarget, piData, this, fromBuilder);
    }

    /**
     * Creates a comment.
     *
     * @param parent
     * @param content
     * @return Returns OMComment.
     */
    public OMComment createOMComment(OMContainer parent, String content) {
        return createOMComment(parent, content, false);
    }

    public OMComment createOMComment(OMContainer parent, String content, boolean fromBuilder) {
        return new OMCommentImpl(parent, content, this, fromBuilder);
    }

    /* (non-Javadoc)
    * @see org.apache.axiom.om.OMFactory#createOMDocument()
    */
    public OMDocument createOMDocument() {
        return new OMDocumentImpl(this);
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMFactory#createOMDocument(org.apache.axiom.om.OMXMLParserWrapper)
      */
    public OMDocument createOMDocument(OMXMLParserWrapper builder) {
        return new OMDocumentImpl(builder, this);
    }

    public OMEntityReference createOMEntityReference(OMContainer parent, String name) {
        return createOMEntityReference(parent, name, null, false);
    }

    public OMEntityReference createOMEntityReference(OMContainer parent, String name, String replacementText, boolean fromBuilder) {
        return new OMEntityReferenceImpl(parent, name, replacementText, this, fromBuilder);
    }

    /**
     * This method is intended only to be used by Axiom intenals when merging Objects from different
     * Axiom implementations to the LLOM implementation.
     *
     * @param child
     */
    public OMNode importNode(OMNode child) {
        int type = child.getType();
        switch (type) {
            case (OMNode.ELEMENT_NODE): {
                OMElement childElement = (OMElement) child;
                OMElement newElement = (new StAXOMBuilder(this, childElement
                        .getXMLStreamReader())).getDocumentElement();
                newElement.buildWithAttachments();
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
                    newText = createOMText(null, importedText
                            .getTextCharacters(), importedText.getType());
                } else {
                    newText = createOMText(null, importedText
                            .getText()/*, importedText.getOMNodeType()*/);
                }
                return newText;
            }

            case (OMNode.PI_NODE): {
                OMProcessingInstruction importedPI = (OMProcessingInstruction) child;
                return createOMProcessingInstruction(null,
                                                                  importedPI.getTarget(),
                                                                  importedPI.getValue());
            }
            case (OMNode.COMMENT_NODE): {
                OMComment importedComment = (OMComment) child;
                return createOMComment(null, importedComment.getValue());
            }
            case (OMNode.DTD_NODE) : {
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

    public CoreDocument createDocument() {
        return new OMDocumentImpl(this);
    }

    public CoreCharacterData createCharacterData() {
        return new CharacterDataImpl(this);
    }
    
    public CoreCDATASection createCDATASection() {
        return new CDATASectionImpl(this);
    }
}
