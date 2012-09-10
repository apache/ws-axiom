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

package org.apache.axiom.om.impl.dom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.common.IContainer;
import org.apache.axiom.om.impl.common.OMContainerHelper;
import org.apache.axiom.om.impl.common.OMDocumentImplUtil;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
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

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class DocumentImpl extends RootNode implements Document, OMDocument, IContainer {
    protected OMXMLParserWrapper builder;

    protected int state;

    private String xmlVersion;

    private String xmlEncoding;
    
    private boolean xmlStandalone = false;
    
    private String charEncoding;

    private Vector idAttrs;

    protected Hashtable identifiers;
    
    private final DOMConfigurationImpl domConfig = new DOMConfigurationImpl();

    public DocumentImpl(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(factory);
        this.builder = parserWrapper;
    }

    public DocumentImpl(OMFactory factory) {
        super(factory);
        state = COMPLETE;
    }

    ParentNode internalGetOwnerNode() {
        return this;
    }

    void internalSetOwnerNode(ParentNode ownerNode) {
        // The owner node of a document node cannot be set
        throw new UnsupportedOperationException();
    }

    public Document getOwnerDocument() {
        return null;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        internalSerialize(writer, cache, !((MTOMXMLStreamWriter) writer).isIgnoreXMLDeclaration());
    }

    // /
    // /org.w3c.dom.Node methods
    // /
    public String getNodeName() {
        return "#document";
    }

    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    // /org.w3c.dom.Document methods
    // /

    public Attr createAttribute(String name) throws DOMException {
        if (!DOMUtil.isQualifiedName(name)) {
            throw DOMUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        return new AttrImpl(this, name, this.factory);
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
            throws DOMException {
        String localName = DOMUtil.getLocalName(qualifiedName);
        String prefix = DOMUtil.getPrefix(qualifiedName);
        DOMUtil.validateAttrNamespace(namespaceURI, localName, prefix);

        if (!XMLConstants.XMLNS_ATTRIBUTE.equals(localName)) {
            this.checkQName(prefix, localName);
        }

        OMNamespace namespace;
        if (namespaceURI == null) {
            namespace = null;
        } else {
            namespace = new OMNamespaceImpl(namespaceURI,
                    prefix == null && !XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI) ? "" : prefix);
        }
        return new AttrImpl(this, localName, namespace, this.factory);
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        CDATASectionImpl cdataSection = new CDATASectionImpl(data, factory);
        cdataSection.setOwnerDocument(this);
        return cdataSection;
    }

    public Comment createComment(String data) {
        CommentImpl comment = new CommentImpl(data, this.factory);
        comment.setOwnerDocument(this);
        return comment;
    }

    public DocumentFragment createDocumentFragment() {
        DocumentFragmentImpl fragment = new DocumentFragmentImpl(this.factory);
        fragment.setOwnerDocument(this);
        return fragment;
    }

    public Element createElement(String tagName) throws DOMException {
        ElementImpl element = new ElementImpl(null, tagName, null, null, this.factory, false);
        element.setOwnerDocument(this);
        return element;
    }

    public Element createElementNS(String ns, String qualifiedName)
            throws DOMException {

        if (ns == null) ns = "";

        String localName = DOMUtil.getLocalName(qualifiedName);
        String prefix = DOMUtil.getPrefix(qualifiedName);
        checkQName(prefix, localName);
        
        if(prefix == null) {
            prefix = "";
        }

        OMNamespaceImpl namespace;
        if (ns.length() == 0) {
            namespace = null;
        } else {
            namespace = new OMNamespaceImpl(ns, prefix);
        }
        ElementImpl element = new ElementImpl(null, localName, namespace, null, this.factory, false);
        element.setOwnerDocument(this);
        return element;
    }

    public EntityReference createEntityReference(String name) throws DOMException {
        EntityReferenceImpl node = new EntityReferenceImpl(name, null, factory);
        node.setOwnerDocument(this);
        return node;
    }

    public ProcessingInstruction createProcessingInstruction(String target,
                                                             String data) throws DOMException {
        ProcessingInstructionImpl pi = new ProcessingInstructionImpl(target, data, factory);
        pi.setOwnerDocument(this);
        return pi;
    }

    public Text createTextNode(String value) {
        TextImpl text = new TextImpl(value, this.factory);
        text.setOwnerDocument(this);
        return text;
    }

    public DocumentType getDoctype() {
        Iterator it = getChildren();
        while (it.hasNext()) {
            Object child = it.next();
            if (child instanceof DocumentType) {
                return (DocumentType)child;
            } else if (child instanceof Element) {
                // A doctype declaration can only appear before the root element. Stop here.
                return null;
            }
        }
        return null;
    }

    public Element getElementById(String elementId) {

        //If there are no id attrs
        if (this.idAttrs == null) {
            return null;
        }

        Enumeration attrEnum = this.idAttrs.elements();
        while (attrEnum.hasMoreElements()) {
            Attr tempAttr = (Attr) attrEnum.nextElement();
            if (tempAttr.getValue().equals(elementId)) {
                return tempAttr.getOwnerElement();
            }
        }

        //If we reach this point then, there's no such attr 
        return null;
    }

    public NodeList getElementsByTagName(String tagname) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public DOMImplementation getImplementation() {
        return ((DOMMetaFactory)factory.getMetaFactory()).getDOMImplementation();
    }

    public Node importNode(Node importedNode, boolean deep) throws DOMException {

        short type = importedNode.getNodeType();
        Node newNode = null;
        switch (type) {
            case Node.ELEMENT_NODE: {
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
                        Attr attr = (Attr) sourceAttrs.item(index);
                        if (attr.getNamespaceURI() != null
                                && !attr.getNamespaceURI().equals(
                                XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                            Attr newAttr = (Attr) importNode(attr, true);
                            newElement.setAttributeNodeNS(newAttr);
                        } else { // if (attr.getLocalName() == null) {
                            Attr newAttr = (Attr) importNode(attr, true);
                            newElement.setAttributeNode(newAttr);
                        }

                    }
                }
                newNode = newElement;
                break;
            }

            case Node.ATTRIBUTE_NODE: {
                if ("".equals(importedNode.getNamespaceURI())
                        || importedNode.getNamespaceURI() == null) {
                    newNode = createAttribute(importedNode.getNodeName());
                } else {
                    //Check whether it is a default ns decl
                    if (XMLConstants.XMLNS_ATTRIBUTE.equals(importedNode.getNodeName())) {
                        newNode = createAttribute(importedNode.getNodeName());
                    } else {
                        String ns = importedNode.getNamespaceURI();
                        ns = (ns != null) ? ns.intern() : null;
                        newNode = createAttributeNS(ns ,
                                                    importedNode.getNodeName());
                    }
                }
                ((Attr) newNode).setValue(importedNode.getNodeValue());
                break;
            }

            case Node.TEXT_NODE: {
                newNode = createTextNode(importedNode.getNodeValue());
                break;
            }

            case Node.COMMENT_NODE: {
                newNode = createComment(importedNode.getNodeValue());
                break;
            }
                
            case Node.DOCUMENT_FRAGMENT_NODE: {
                newNode = createDocumentFragment();
                // No name, kids carry value
                break;
            }

            case Node.CDATA_SECTION_NODE:
            case Node.ENTITY_REFERENCE_NODE:
            case Node.ENTITY_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE:
            case Node.DOCUMENT_TYPE_NODE:
            case Node.NOTATION_NODE:
                throw new UnsupportedOperationException("TODO : Implement handling of org.w3c.dom.Node type == " + type );

            case Node.DOCUMENT_NODE: // Can't import document nodes
            default:
                throw DOMUtil.newDOMException(DOMException.NOT_SUPPORTED_ERR);
        }

        // If deep, replicate and attach the kids.
        if (deep && !(importedNode instanceof Attr)) {
            for (Node srckid = importedNode.getFirstChild(); srckid != null;
                 srckid = srckid.getNextSibling()) {
                newNode.appendChild(importNode(srckid, true));
            }
        }

        return newNode;

    }

    // /
    // /OMDocument Methods
    // /
    public String getCharsetEncoding() {
        return this.charEncoding;
    }

    public String getXMLVersion() {
        return this.xmlVersion;
    }

    public String isStandalone() {
        return (this.xmlStandalone) ? "yes" : "no";
    }

    public void setCharsetEncoding(String charsetEncoding) {
        this.charEncoding = charsetEncoding;
    }

    public void setOMDocumentElement(OMElement documentElement) {
        if (documentElement == null) {
            throw new IllegalArgumentException("documentElement must not be null");
        }
        OMElement existingDocumentElement = getOMDocumentElement();
        if (existingDocumentElement == null) {
            addChild(documentElement);
        } else {
            OMNode nextSibling = existingDocumentElement.getNextOMSibling();
            existingDocumentElement.detach();
            if (nextSibling == null) {
                addChild(documentElement);
            } else {
                nextSibling.insertSiblingBefore(documentElement);
            }
        }
    }

    public void setStandalone(String isStandalone) {
        this.xmlStandalone = "yes".equalsIgnoreCase(isStandalone);
    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        internalSerialize(writer, false);
        writer.flush();
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        internalSerialize(writer, true);
        writer.flush();
    }

    public void setXMLVersion(String version) {
        this.xmlVersion = version;
    }

    public String getXMLEncoding() {
        return xmlEncoding;
    }

    public void setXMLEncoding(String encoding) {
        this.xmlEncoding = encoding;
    }
    
    public OMElement getOMDocumentElement() {
        return getOMDocumentElement(true);
    }

    OMElement getOMDocumentElement(boolean build) {
        OMNode child = build ? getFirstOMChild() : getFirstOMChildIfAvailable();
        while (child != null) {
            if (child instanceof OMElement) {
                return (OMElement)child;
            }
            child = build ? child.getNextOMSibling() : ((OMNodeEx)child).getNextOMSiblingIfAvailable();
        }
        return null;
    }

    /**
     * Returns the document element.
     *
     * @see org.w3c.dom.Document#getDocumentElement()
     */
    public Element getDocumentElement() {

        return (Element) this.getOMDocumentElement();
    }

    /**
     * Borrowed from the Xerces impl. Checks if the given qualified name is legal with respect to
     * the version of XML to which this document must conform.
     *
     * @param prefix prefix of qualified name
     * @param local  local part of qualified name
     */
    protected final void checkQName(String prefix, String local) {
        // check that both prefix and local part match NCName
        if ((prefix != null && !XMLChar.isValidNCName(prefix))
                || !XMLChar.isValidNCName(local)) {
            throw DOMUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
    }

    protected void addIdAttr(Attr attr) {
        if (this.idAttrs == null) {
            this.idAttrs = new Vector();
        }
        this.idAttrs.add(attr);
    }

    protected void removeIdAttr(Attr attr) {
        if (this.idAttrs != null) {
            this.idAttrs.remove(attr);
        }

    }

    /*
    * DOM-Level 3 methods
    */

    public String getTextContent() throws DOMException {
        return null;
    }

    public void setTextContent(String textContent) throws DOMException {
        // no-op
    }

    public Node adoptNode(Node node) throws DOMException {
        if (node instanceof NodeImpl) {
            NodeImpl childNode = (NodeImpl)node;
            if (childNode.hasParent()) {
                childNode.detach();
            }
            childNode.setOwnerDocument(this);
            return childNode;
        } else {
            return null;
        }
    }

    public String getDocumentURI() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public DOMConfiguration getDomConfig() {
        return domConfig;
    }

    public String getInputEncoding() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean getStrictErrorChecking() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public String getXmlEncoding() {
        return this.charEncoding;
    }

    public boolean getXmlStandalone() {
        return this.xmlStandalone;
    }

    public String getXmlVersion() {
        return getXMLVersion();
    }

    public void normalizeDocument() {
        if (domConfig.isEnabled(DOMConfigurationImpl.SPLIT_CDATA_SECTIONS)
                || domConfig.isEnabled(DOMConfigurationImpl.WELLFORMED)) {
            throw new UnsupportedOperationException("TODO");
        } else {
            normalize(domConfig);
        }
    }

    public Node renameNode(Node node, String namespaceURI, String qualifiedName)
            throws DOMException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setDocumentURI(String documentURI) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setStrictErrorChecking(boolean strictErrorChecking) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setXmlStandalone(boolean standalone) throws DOMException {
        this.xmlStandalone = standalone;
    }

    public void setXmlVersion(String version) throws DOMException {
        setXMLVersion(version);
    }

    protected void internalSerialize(XMLStreamWriter writer, boolean cache,
            boolean includeXMLDeclaration) throws XMLStreamException {
        OMDocumentImplUtil.internalSerialize(this, writer, cache, includeXMLDeclaration);
    }

    ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        DocumentImpl clone;
        if (options.isPreserveModel()) {
            clone = createClone(options);
        } else {
            clone = new DocumentImpl(getOMFactory());
        }
        clone.xmlVersion = xmlVersion;
        clone.xmlEncoding = xmlEncoding;
        clone.xmlStandalone = xmlStandalone;
        clone.charEncoding = charEncoding;
        return clone;
    }

    protected DocumentImpl createClone(OMCloneOptions options) {
        return new DocumentImpl(factory);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return builder;
    }

    public final int getState() {
        return state;
    }

    public final boolean isComplete() {
        return state == COMPLETE;
    }

    public final void setComplete(boolean complete) {
        state = complete ? COMPLETE : INCOMPLETE;
    }

    public final void discarded() {
        state = DISCARDED;
    }

    public final void build() {
        OMContainerHelper.build(this);
    }

    public final Node getNextSibling() {
        return null;
    }
    
    public final void removeChildren() {
        OMContainerHelper.removeChildren(this);
    }

    public final String lookupNamespaceURI(String specifiedPrefix) {
        Element documentElement = getDocumentElement();
        return documentElement == null ? null
                : documentElement.lookupNamespaceURI(specifiedPrefix);
    }
}
