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
package org.apache.axiom.om.impl.common.factory;

import static org.apache.axiom.util.xml.NSUtils.generatePrefix;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.intf.AxiomText;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.om.impl.intf.factory.AxiomElementType;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;

public class OMFactoryImpl implements OMFactory {
    protected final AxiomNodeFactory nodeFactory;

    public OMFactoryImpl(AxiomNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public final OMMetaFactory getMetaFactory() {
        return nodeFactory;
    }

    @Override
    public final OMNamespace createOMNamespace(String uri, String prefix) {
        return new OMNamespaceImpl(uri, prefix);
    }

    @Override
    public final OMDocument createOMDocument() {
        return nodeFactory.createDocument();
    }

    @Override
    public final OMDocType createOMDocType(
            OMContainer parent,
            String rootName,
            String publicId,
            String systemId,
            String internalSubset) {
        AxiomDocType node = nodeFactory.createDocumentTypeDeclaration();
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        if (parent != null) {
            ((AxiomContainer) parent).addChild(node);
        }
        return node;
    }

    private AxiomText createAxiomText(OMContainer parent, Object content, int type) {
        AxiomText node;
        switch (type) {
            case OMNode.TEXT_NODE:
                {
                    node = nodeFactory.createCharacterDataNode();
                    break;
                }
            case OMNode.SPACE_NODE:
                {
                    AxiomCharacterDataNode cdata = nodeFactory.createCharacterDataNode();
                    cdata.coreSetIgnorable(true);
                    node = cdata;
                    break;
                }
            case OMNode.CDATA_SECTION_NODE:
                {
                    node = nodeFactory.createCDATASection();
                    break;
                }
            default:
                throw new IllegalArgumentException("Invalid node type");
        }
        if (parent != null) {
            ((AxiomContainer) parent).addChild(node);
        }
        try {
            node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
        return node;
    }

    @Override
    public final OMText createOMText(String s, int type) {
        return createAxiomText(null, s, type);
    }

    @Override
    public final OMText createOMText(String s) {
        return createAxiomText(null, s, OMNode.TEXT_NODE);
    }

    @Override
    public final OMText createOMText(OMContainer parent, String text, int type) {
        return createAxiomText(parent, text, type);
    }

    @Override
    public final OMText createOMText(OMContainer parent, String text) {
        return createAxiomText(parent, text, OMNode.TEXT_NODE);
    }

    @Override
    public final OMText createOMText(OMContainer parent, char[] charArray, int type) {
        return createAxiomText(parent, new String(charArray), type);
    }

    @Override
    public final OMText createOMText(OMContainer parent, QName text, int type) {
        if (text == null) {
            throw new IllegalArgumentException("QName text arg cannot be null!");
        }
        OMNamespace ns =
                ((AxiomElement) parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        return createAxiomText(
                parent,
                ns == null ? text.getLocalPart() : ns.getPrefix() + ":" + text.getLocalPart(),
                type);
    }

    @Override
    public final OMText createOMText(OMContainer parent, QName text) {
        return createAxiomText(parent, text, OMNode.TEXT_NODE);
    }

    @Override
    public final OMText createOMText(
            OMContainer parent, String s, String mimeType, boolean optimize) {
        TextContent textContent = new TextContent(s);
        textContent.setOptimize(optimize);
        return createAxiomText(parent, textContent, OMNode.TEXT_NODE);
    }

    @Override
    public final OMText createOMText(String s, String mimeType, boolean optimize) {
        return createOMText(null, s, mimeType, optimize);
    }

    @Override
    public final OMText createOMText(OMContainer parent, OMText source) {
        try {
            // TODO: this doesn't necessarily produce a node with the expected OMFactory
            return (AxiomText)
                    ((AxiomText) source)
                            .coreClone(AxiomSemantics.CLONE_POLICY, null, (AxiomContainer) parent);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final OMText createOMText(Blob blob, boolean optimize) {
        return createAxiomText(null, new TextContent(null, blob, optimize), OMNode.TEXT_NODE);
    }

    @Override
    public final OMText createOMText(
            String contentID, BlobProvider blobProvider, boolean optimize) {
        return createAxiomText(
                null, new TextContent(contentID, blobProvider, optimize), OMNode.TEXT_NODE);
    }

    @Override
    public final OMProcessingInstruction createOMProcessingInstruction(
            OMContainer parent, String piTarget, String piData) {
        AxiomProcessingInstruction node = nodeFactory.createProcessingInstruction();
        node.coreSetTarget(piTarget);
        try {
            node.coreSetCharacterData(piData, AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
        if (parent != null) {
            ((AxiomContainer) parent).addChild(node);
        }
        return node;
    }

    @Override
    public final OMEntityReference createOMEntityReference(OMContainer parent, String name) {
        AxiomEntityReference node = nodeFactory.createEntityReference();
        node.coreSetName(name);
        if (parent != null) {
            ((AxiomContainer) parent).addChild(node);
        }
        return node;
    }

    @Override
    public final OMComment createOMComment(OMContainer parent, String content) {
        AxiomComment node = nodeFactory.createComment();
        try {
            node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
        if (parent != null) {
            ((AxiomContainer) parent).addChild(node);
        }
        return node;
    }

    @Override
    public final OMElement createOMElement(String localName, OMNamespace ns) {
        return createOMElement(localName, ns, null);
    }

    protected final <T extends AxiomElement> T createAxiomElement(
            AxiomElementType<T> type, OMContainer parent, String localName, OMNamespace ns) {
        T element = type.create(nodeFactory);
        if (parent != null) {
            ((AxiomContainer) parent).addChild(element);
        }
        element.initName(localName, ns, true);
        return element;
    }

    @Override
    public final OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent) {
        return createAxiomElement(AxiomNodeFactory::createNSAwareElement, parent, localName, ns);
    }

    @Override
    public final OMElement createOMElement(QName qname, OMContainer parent) {
        AxiomElement element = nodeFactory.createNSAwareElement();
        if (parent != null) {
            parent.addChild(element);
        }
        element.internalSetLocalName(qname.getLocalPart());
        String prefix = qname.getPrefix();
        String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            // The goal here is twofold:
            //  * check if the namespace needs to be declared;
            //  * locate an existing OMNamespace object, so that we can avoid creating a new one.
            OMNamespace ns =
                    element.findNamespace(namespaceURI, prefix.length() == 0 ? null : prefix);
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = generatePrefix(namespaceURI);
                }
                ns = element.declareNamespace(namespaceURI, prefix);
            }
            element.internalSetNamespace(ns);
        } else if (prefix.length() > 0) {
            throw new IllegalArgumentException(
                    "Cannot create a prefixed element with an empty namespace name");
        } else {
            if (element.getDefaultNamespace() != null) {
                element.declareDefaultNamespace("");
            }
            element.internalSetNamespace(null);
        }
        return element;
    }

    @Override
    public final OMElement createOMElement(QName qname) {
        return createOMElement(qname, null);
    }

    @Override
    public final OMElement createOMElement(String localName, String namespaceURI, String prefix) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        } else if (namespaceURI.length() == 0) {
            if (prefix != null && prefix.length() > 0) {
                throw new IllegalArgumentException(
                        "Cannot create a prefixed element with an empty namespace name");
            }
            return createOMElement(localName, null);
        } else {
            return createOMElement(localName, createOMNamespace(namespaceURI, prefix));
        }
    }

    @Override
    public final OMSourcedElement createOMElement(OMDataSource source) {
        AxiomSourcedElement element = nodeFactory.createSourcedElement();
        element.init(source);
        return element;
    }

    @Override
    public final OMSourcedElement createOMElement(
            OMDataSource source, String localName, OMNamespace ns) {
        AxiomSourcedElement element = nodeFactory.createSourcedElement();
        element.init(localName, ns, source);
        return element;
    }

    @Override
    public final OMSourcedElement createOMElement(OMDataSource source, QName qname) {
        AxiomSourcedElement element = nodeFactory.createSourcedElement();
        element.init(qname, source);
        return element;
    }

    @Override
    public final OMAttribute createOMAttribute(String localName, OMNamespace ns, String value) {
        if (ns != null && ns.getPrefix() == null) {
            String namespaceURI = ns.getNamespaceURI();
            if (namespaceURI.length() == 0) {
                ns = null;
            } else {
                ns = new OMNamespaceImpl(namespaceURI, generatePrefix(namespaceURI));
            }
        }
        if (ns != null) {
            if (ns.getNamespaceURI().length() == 0) {
                if (ns.getPrefix().length() > 0) {
                    throw new IllegalArgumentException(
                            "Cannot create a prefixed attribute with an empty namespace name");
                } else {
                    ns = null;
                }
            } else if (ns.getPrefix().length() == 0) {
                throw new IllegalArgumentException(
                        "Cannot create an unprefixed attribute with a namespace");
            }
        }
        AxiomAttribute attr = nodeFactory.createNSAwareAttribute();
        attr.internalSetLocalName(localName);
        try {
            attr.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
        attr.internalSetNamespace(ns);
        attr.coreSetType("CDATA");
        return attr;
    }

    protected final <T extends AxiomElement> T importElement(
            OMElement element, AxiomElementType<T> type) {
        T importedElement = type.create(nodeFactory);
        copyName(element, importedElement);
        for (Iterator<OMAttribute> it = element.getAllAttributes(); it.hasNext(); ) {
            importedElement.coreAppendAttribute(importAttribute(it.next()));
        }
        for (Iterator<OMNamespace> it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
            OMNamespace ns = it.next();
            AxiomNamespaceDeclaration nsDecl = nodeFactory.createNamespaceDeclaration();
            nsDecl.coreSetDeclaredNamespace(ns.getPrefix(), ns.getNamespaceURI());
            importedElement.coreAppendAttribute(nsDecl);
        }
        importChildren(element, importedElement);
        return importedElement;
    }

    private AxiomChildNode importChildNode(OMNode child) {
        int type = child.getType();
        switch (type) {
            case OMNode.ELEMENT_NODE:
                return importElement((OMElement) child, AxiomNodeFactory::createNSAwareElement);
            case OMNode.TEXT_NODE:
            case OMNode.SPACE_NODE:
            case OMNode.CDATA_SECTION_NODE:
                {
                    OMText text = (OMText) child;
                    Object content;
                    if (text.isBinary()) {
                        content =
                                new TextContent(
                                        text.getContentID(), text.getBlob(), text.isOptimized());
                    } else {
                        content = text.getText();
                    }
                    return createAxiomText(null, content, type);
                }
            case OMNode.PI_NODE:
                {
                    OMProcessingInstruction pi = (OMProcessingInstruction) child;
                    AxiomProcessingInstruction importedPI =
                            nodeFactory.createProcessingInstruction();
                    importedPI.setTarget(pi.getTarget());
                    importedPI.setValue(pi.getValue());
                    return importedPI;
                }
            case OMNode.COMMENT_NODE:
                {
                    OMComment comment = (OMComment) child;
                    AxiomComment importedComment = nodeFactory.createComment();
                    importedComment.setValue(comment.getValue());
                    return importedComment;
                }
            case OMNode.DTD_NODE:
                {
                    OMDocType docType = (OMDocType) child;
                    AxiomDocType importedDocType = nodeFactory.createDocumentTypeDeclaration();
                    importedDocType.coreSetRootName(docType.getRootName());
                    importedDocType.coreSetPublicId(docType.getPublicId());
                    importedDocType.coreSetSystemId(docType.getSystemId());
                    importedDocType.coreSetInternalSubset(docType.getInternalSubset());
                    return importedDocType;
                }
            case OMNode.ENTITY_REFERENCE_NODE:
                AxiomEntityReference importedEntityRef = nodeFactory.createEntityReference();
                importedEntityRef.coreSetName(((OMEntityReference) child).getName());
                return importedEntityRef;
            default:
                throw new IllegalArgumentException("Unsupported node type");
        }
    }

    private void copyName(OMNamedInformationItem node, AxiomNamedInformationItem importedNode) {
        importedNode.internalSetNamespace(node.getNamespace());
        importedNode.internalSetLocalName(node.getLocalName());
    }

    private void importChildren(OMContainer node, AxiomContainer importedNode) {
        for (OMNode child = node.getFirstOMChild();
                child != null;
                child = child.getNextOMSibling()) {
            try {
                importedNode.coreAppendChild(importChildNode(child));
            } catch (CoreModelException ex) {
                throw AxiomExceptionTranslator.translate(ex);
            }
        }
    }

    private AxiomAttribute importAttribute(OMAttribute attribute) {
        AxiomAttribute importedAttribute = nodeFactory.createNSAwareAttribute();
        copyName(attribute, importedAttribute);
        importedAttribute.setAttributeValue(attribute.getAttributeValue());
        return importedAttribute;
    }

    @Override
    public final OMInformationItem importInformationItem(OMInformationItem node) {
        if (node instanceof OMNode) {
            return importChildNode((OMNode) node);
        } else if (node instanceof OMDocument) {
            OMDocument document = (OMDocument) node;
            AxiomDocument importedDocument = nodeFactory.createDocument();
            // TODO: other attributes
            importChildren(document, importedDocument);
            return importedDocument;
        } else if (node instanceof OMAttribute) {
            return importAttribute((OMAttribute) node);
        } else {
            throw new IllegalArgumentException("Unsupported node type");
        }
    }
}
