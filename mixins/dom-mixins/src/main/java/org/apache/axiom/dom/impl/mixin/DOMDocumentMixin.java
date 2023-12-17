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
package org.apache.axiom.dom.impl.mixin;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import java.util.Iterator;

import javax.xml.XMLConstants;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.Mappers;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMCDATASection;
import org.apache.axiom.dom.DOMComment;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMDocument;
import org.apache.axiom.dom.DOMDocumentFragment;
import org.apache.axiom.dom.DOMElement;
import org.apache.axiom.dom.DOMEntityReference;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNSAwareAttribute;
import org.apache.axiom.dom.DOMNSAwareElement;
import org.apache.axiom.dom.DOMNSAwareNamedNode;
import org.apache.axiom.dom.DOMNSUnawareAttribute;
import org.apache.axiom.dom.DOMNSUnawareElement;
import org.apache.axiom.dom.DOMNamespaceDeclaration;
import org.apache.axiom.dom.DOMNode;
import org.apache.axiom.dom.DOMProcessingInstruction;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.dom.DOMText;
import org.apache.axiom.dom.ElementsByTagName;
import org.apache.axiom.dom.ElementsByTagNameNS;
import org.apache.axiom.dom.NSUtil;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

@Mixin
public abstract class DOMDocumentMixin implements DOMDocument {
    private final DOMConfigurationImpl domConfig = new DOMConfigurationImpl();

    private String documentURI;
    private boolean strictErrorChecking = true;

    @Override
    public final Document getOwnerDocument() {
        return null;
    }

    @Override
    public final String getNodeName() {
        return "#document";
    }

    @Override
    public final short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    @Override
    public final String getNodeValue() {
        return null;
    }

    @Override
    public final void setNodeValue(String nodeValue) {}

    @Override
    public final String getPrefix() {
        return null;
    }

    @Override
    public final void setPrefix(String prefix) throws DOMException {
        throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
    }

    @Override
    public final String getNamespaceURI() {
        return null;
    }

    @Override
    public final String getLocalName() {
        return null;
    }

    @Override
    public final boolean hasAttributes() {
        return false;
    }

    @Override
    public final NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public final String getTextContent() {
        return null;
    }

    @Override
    public final void setTextContent(String textContent) {
        // no-op
    }

    @Override
    public final Element getDocumentElement() {
        try {
            return (Element) coreGetDocumentElement();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final CoreElement getNamespaceContext() {
        try {
            return coreGetDocumentElement();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final DOMImplementation getImplementation() {
        return getDOMNodeFactory();
    }

    @Override
    public final DOMConfiguration getDomConfig() {
        return domConfig;
    }

    @Override
    public final String getInputEncoding() {
        return coreGetInputEncoding();
    }

    @Override
    public final String getXmlVersion() {
        String version = coreGetXmlVersion();
        return version == null ? "1.0" : version;
    }

    @Override
    public final void setXmlVersion(String version) {
        coreSetXmlVersion(version);
    }

    @Override
    public final String getXmlEncoding() {
        return coreGetXmlEncoding();
    }

    @Override
    public final boolean getXmlStandalone() {
        Boolean standalone = coreGetStandalone();
        return standalone != null && standalone;
    }

    @Override
    public final void setXmlStandalone(boolean standalone) {
        coreSetStandalone(standalone);
    }

    @Override
    public final void normalizeDocument() {
        if (domConfig.isEnabled(DOMConfigurationImpl.SPLIT_CDATA_SECTIONS)
                || domConfig.isEnabled(DOMConfigurationImpl.WELLFORMED)) {
            throw new UnsupportedOperationException("TODO");
        } else {
            normalizeRecursively(domConfig);
        }
    }

    @Override
    public final Text createTextNode(String data) {
        DOMText text = getDOMNodeFactory().createCharacterDataNode();
        text.coreSetOwnerDocument(this);
        text.coreSetCharacterData(data);
        return text;
    }

    @Override
    public final CDATASection createCDATASection(String data) {
        try {
            DOMCDATASection cdataSection = getDOMNodeFactory().createCDATASection();
            cdataSection.coreSetOwnerDocument(this);
            cdataSection.coreSetCharacterData(data, DOMSemantics.INSTANCE);
            return cdataSection;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Element createElement(String tagName) {
        NSUtil.validateName(tagName);
        DOMNSUnawareElement element = getDOMNodeFactory().createNSUnawareElement();
        element.coreSetOwnerDocument(this);
        element.coreSetName(tagName);
        return element;
    }

    @Override
    public final Attr createAttribute(String name) {
        NSUtil.validateName(name);
        DOMNSUnawareAttribute attr = getDOMNodeFactory().createNSUnawareAttribute();
        attr.coreSetOwnerDocument(this);
        attr.coreSetName(name);
        attr.coreSetType("CDATA");
        return attr;
    }

    @Override
    public final Element createElementNS(String namespaceURI, String qualifiedName) {
        int i = NSUtil.validateQualifiedName(qualifiedName);
        String prefix;
        String localName;
        if (i == -1) {
            prefix = "";
            localName = qualifiedName;
        } else {
            prefix = qualifiedName.substring(0, i);
            localName = qualifiedName.substring(i + 1);
        }
        namespaceURI = NSUtil.normalizeNamespaceURI(namespaceURI);
        NSUtil.validateNamespace(namespaceURI, prefix);
        DOMNSAwareElement element = getDOMNodeFactory().createNSAwareElement();
        element.coreSetOwnerDocument(this);
        element.coreSetName(namespaceURI, localName, prefix);
        return element;
    }

    @Override
    public final Attr createAttributeNS(String namespaceURI, String qualifiedName) {
        int i = NSUtil.validateQualifiedName(qualifiedName);
        String prefix;
        String localName;
        if (i == -1) {
            prefix = "";
            localName = qualifiedName;
        } else {
            prefix = qualifiedName.substring(0, i);
            localName = qualifiedName.substring(i + 1);
        }
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            DOMNamespaceDeclaration decl = getDOMNodeFactory().createNamespaceDeclaration();
            decl.coreSetOwnerDocument(this);
            decl.coreSetDeclaredNamespace(NSUtil.getDeclaredPrefix(localName, prefix), "");
            return decl;
        } else {
            namespaceURI = NSUtil.normalizeNamespaceURI(namespaceURI);
            NSUtil.validateAttributeName(namespaceURI, localName, prefix);
            DOMNSAwareAttribute attr = getDOMNodeFactory().createNSAwareAttribute();
            attr.coreSetOwnerDocument(this);
            attr.coreSetName(namespaceURI, localName, prefix);
            // TODO: set type?
            return attr;
        }
    }

    @Override
    public final ProcessingInstruction createProcessingInstruction(String target, String data) {
        try {
            DOMProcessingInstruction pi = getDOMNodeFactory().createProcessingInstruction();
            pi.coreSetOwnerDocument(this);
            pi.coreSetTarget(target);
            pi.coreSetCharacterData(data, DOMSemantics.INSTANCE);
            return pi;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final EntityReference createEntityReference(String name) throws DOMException {
        DOMEntityReference node = getDOMNodeFactory().createEntityReference();
        node.coreSetOwnerDocument(this);
        node.coreSetName(name);
        return node;
    }

    @Override
    public final Comment createComment(String data) {
        try {
            DOMComment node = getDOMNodeFactory().createComment();
            node.coreSetOwnerDocument(this);
            node.coreSetCharacterData(data, DOMSemantics.INSTANCE);
            return node;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final DocumentFragment createDocumentFragment() {
        DOMDocumentFragment fragment = getDOMNodeFactory().createDocumentFragment();
        fragment.coreSetOwnerDocument(this);
        return fragment;
    }

    @Override
    public final NodeList getElementsByTagName(String tagname) {
        return new ElementsByTagName(this, tagname);
    }

    @Override
    public final NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return new ElementsByTagNameNS(this, namespaceURI, localName);
    }

    // TODO: need unit test to check that this method works as expected on an OMSourcedElement
    @Override
    public final Node renameNode(Node node, String namespaceURI, String qualifiedName) {
        if (!(node instanceof DOMNode && ((DOMNode) node).coreHasSameOwnerDocument(this))) {
            throw DOMExceptionUtil.newDOMException(DOMException.WRONG_DOCUMENT_ERR);
        }
        // TODO: what about an attempt to rename a namespace unaware node?
        if (!(node instanceof DOMNSAwareNamedNode)) {
            throw DOMExceptionUtil.newDOMException(DOMException.NOT_SUPPORTED_ERR);
        }
        int i = NSUtil.validateQualifiedName(qualifiedName);
        String prefix;
        String localName;
        if (i == -1) {
            prefix = "";
            localName = qualifiedName;
        } else {
            prefix = qualifiedName.substring(0, i);
            localName = qualifiedName.substring(i + 1);
        }
        namespaceURI = NSUtil.normalizeNamespaceURI(namespaceURI);
        switch (((DOMNode) node).coreGetNodeType()) {
            case NS_AWARE_ELEMENT:
                NSUtil.validateNamespace(namespaceURI, prefix);
                ((DOMNSAwareElement) node).coreSetName(namespaceURI, localName, prefix);
                return node;
            case NS_AWARE_ATTRIBUTE:
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
                    DOMNamespaceDeclaration decl = getDOMNodeFactory().createNamespaceDeclaration();
                    decl.coreSetOwnerDocument(this);
                    // TODO: we should have a generic method to move the content over to the new
                    // node
                    decl.coreSetDeclaredNamespace(
                            NSUtil.getDeclaredPrefix(localName, prefix),
                            ((DOMNSAwareAttribute) node).getValue());
                    // TODO: what about replacing the node in the tree??
                    return decl;
                } else {
                    NSUtil.validateAttributeName(namespaceURI, localName, prefix);
                    ((DOMNSAwareAttribute) node).coreSetName(namespaceURI, localName, prefix);
                    return node;
                }
            case NAMESPACE_DECLARATION:
                // TODO
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public final DocumentType getDoctype() {
        try {
            CoreChildNode child = coreGetFirstChild();
            while (child != null) {
                if (child instanceof DocumentType) {
                    return (DocumentType) child;
                } else if (child instanceof Element) {
                    // A doctype declaration can only appear before the root element. Stop here.
                    return null;
                }
                child = child.coreGetNextSibling();
            }
            return null;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Node adoptNode(Node node) throws DOMException {
        if (node instanceof DOMNode) {
            DOMNode childNode = (DOMNode) node;
            if (childNode instanceof CoreChildNode) {
                ((CoreChildNode) childNode).coreDetach(this);
            } else {
                childNode.coreSetOwnerDocument(this);
            }
            if (node instanceof DOMAttribute) {
                ((DOMAttribute) node).coreSetSpecified(true);
            }
            return childNode;
        } else {
            return null;
        }
    }

    final void checkNewChild0(DOMNode newChild) {
        if (newChild instanceof DOMText) {
            throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
        }
    }

    @Override
    public final Element getElementById(String elementId) {
        try {
            for (Iterator<DOMElement> it =
                            coreGetNodes(
                                    Axis.DESCENDANTS,
                                    DOMElement.class,
                                    Mappers.<DOMElement>identity(),
                                    DOMSemantics.INSTANCE);
                    it.hasNext(); ) {
                DOMElement element = it.next();
                for (CoreAttribute attr = element.coreGetFirstAttribute();
                        attr != null;
                        attr = attr.coreGetNextAttribute()) {
                    if (((DOMAttribute) attr).isId()
                            && elementId.equals(attr.coreGetCharacterData().toString())) {
                        return element;
                    }
                }
            }
            return null;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final String getDocumentURI() {
        return documentURI;
    }

    @Override
    public final void setDocumentURI(String documentURI) {
        this.documentURI = documentURI;
    }

    @Override
    public final boolean getStrictErrorChecking() {
        return strictErrorChecking;
    }

    @Override
    public final void setStrictErrorChecking(boolean strictErrorChecking) {
        this.strictErrorChecking = strictErrorChecking;
    }

    @Override
    public final void normalize(DOMConfigurationImpl config) {}

    @Override
    public final Node importNode(Node importedNode, boolean deep) throws DOMException {

        short type = importedNode.getNodeType();
        Node newNode = null;
        switch (type) {
            case Node.ELEMENT_NODE:
                {
                    Element newElement;
                    if (importedNode.getLocalName() == null) {
                        newElement = this.createElement(importedNode.getNodeName());
                    } else {

                        String ns = importedNode.getNamespaceURI();
                        ns = (ns != null) ? ns.intern() : null;
                        newElement = createElementNS(ns, importedNode.getNodeName());
                    }

                    // Copy element's attributes, if any.
                    NamedNodeMap sourceAttrs = importedNode.getAttributes();
                    if (sourceAttrs != null) {
                        int length = sourceAttrs.getLength();
                        for (int index = 0; index < length; index++) {
                            ((DOMElement) newElement)
                                    .coreAppendAttribute(
                                            (DOMAttribute)
                                                    importNode(sourceAttrs.item(index), true));
                        }
                    }
                    newNode = newElement;
                    break;
                }

            case Node.ATTRIBUTE_NODE:
                {
                    if (importedNode.getLocalName() == null) {
                        newNode = createAttribute(importedNode.getNodeName());
                    } else {
                        String ns = importedNode.getNamespaceURI();
                        ns = (ns != null) ? ns.intern() : null;
                        newNode = createAttributeNS(ns, importedNode.getNodeName());
                    }
                    ((Attr) newNode).setValue(importedNode.getNodeValue());
                    break;
                }

            case Node.TEXT_NODE:
                {
                    newNode = createTextNode(importedNode.getNodeValue());
                    break;
                }

            case Node.COMMENT_NODE:
                {
                    newNode = createComment(importedNode.getNodeValue());
                    break;
                }

            case Node.DOCUMENT_FRAGMENT_NODE:
                {
                    newNode = createDocumentFragment();
                    // No name, kids carry value
                    break;
                }

            case Node.CDATA_SECTION_NODE:
                newNode = createCDATASection(importedNode.getNodeValue());
                break;

            case Node.PROCESSING_INSTRUCTION_NODE:
                {
                    ProcessingInstruction pi = (ProcessingInstruction) importedNode;
                    newNode = createProcessingInstruction(pi.getTarget(), pi.getData());
                    break;
                }
            case Node.ENTITY_REFERENCE_NODE:
            case Node.ENTITY_NODE:
            case Node.NOTATION_NODE:
                throw new UnsupportedOperationException(
                        "TODO : Implement handling of org.w3c.dom.Node type == " + type);

            case Node.DOCUMENT_NODE: // Can't import document nodes
            case Node.DOCUMENT_TYPE_NODE:
            default:
                throw newDOMException(DOMException.NOT_SUPPORTED_ERR);
        }

        // If deep, replicate and attach the kids.
        if (deep && !(importedNode instanceof Attr)) {
            for (Node srckid = importedNode.getFirstChild();
                    srckid != null;
                    srckid = srckid.getNextSibling()) {
                newNode.appendChild(importNode(srckid, true));
            }
        }

        return newNode;
    }
}
