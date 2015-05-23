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

package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

/**
 * @deprecated To build an Axiom tree from SAX events, either use
 *             {@link OMXMLBuilderFactory#createOMBuilder(SAXSource, boolean)} (if a
 *             {@link SAXSource} is available or can be easily constructed), or create an
 *             {@link OMDocument} (using {@link OMFactory#createOMDocument()}) and use
 *             {@link OMContainer#getSAXResult()} to create a {@link SAXResult} for that document.
 *             After writing the SAX events to the {@link SAXResult}, the root element of the
 *             resulting tree can be retrieved using {@link OMDocument#getOMDocumentElement()}. If
 *             the application code doesn't support {@link SAXResult} and needs to interface with a
 *             {@link ContentHandler} directly, use {@link SAXResult#getHandler()} on the
 *             {@link SAXResult} returned by {@link OMContainer#getSAXResult()}.
 */
public class SAXOMBuilder extends DefaultHandler implements LexicalHandler, DeclHandler, OMXMLParserWrapper {
    private final SAXSource source;
    private final boolean expandEntityReferences;
    
    private OMDocument document;
    
    /**
     * Stores the root name if there is a DTD.
     */
    private String dtdName;
    
    /**
     * Stores the public ID if there is a DTD.
     */
    private String dtdPublicId;
    
    /**
     * Stores the system ID if there is a DTD.
     */
    private String dtdSystemId;
    
    /**
     * Stores the internal subset if there is a DTD.
     */
    private StringBuilder internalSubset;

    /**
     * Stores the replacement values for entities.
     */
    private Map entities;
    
    /**
     * Flag indicating that the parser is processing the external subset.
     */
    private boolean inExternalSubset;
    
    private OMContainerEx target;

    private OMElement nextElement;

    private final OMFactoryEx factory;

    private int textNodeType = OMNode.TEXT_NODE;
    
    private boolean inEntityReference;
    private int entityReferenceDepth;

    private SAXOMBuilder(OMFactory factory, SAXSource source, boolean expandEntityReferences) {
        this.factory = (OMFactoryEx)factory;
        this.source = source;
        this.expandEntityReferences = expandEntityReferences;
    }
    
    public SAXOMBuilder(OMFactory factory) {
        this(factory, null, true);
    }
    
    public SAXOMBuilder() {
        this(OMAbstractFactory.getOMFactory());
    }
    
    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        document = factory.createOMDocument(this);
        target = (OMContainerEx)document;
    }

    public void endDocument() throws SAXException {
        if (target != document) {
            throw new IllegalStateException();
        }
        target.setComplete(true);
        target = null;
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        dtdName = name;
        dtdPublicId = publicId;
        dtdSystemId = systemId;
        internalSubset = new StringBuilder();
    }

    public void elementDecl(String name, String model) throws SAXException {
        if (!inExternalSubset) {
            internalSubset.append("<!ELEMENT ");
            internalSubset.append(name);
            internalSubset.append(' ');
            internalSubset.append(model);
            internalSubset.append(">\n");
        }
    }

    public void attributeDecl(String eName, String aName, String type, String mode, String value)
            throws SAXException {
        if (!inExternalSubset) {
            internalSubset.append("<!ATTLIST ");
            internalSubset.append(eName);
            internalSubset.append(' ');
            internalSubset.append(aName);
            internalSubset.append(' ');
            internalSubset.append(type);
            if (value != null) {
                internalSubset.append(' ');
                internalSubset.append(value);
            }
            internalSubset.append(">\n");
        }
    }

    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        if (!inExternalSubset) {
            internalSubset.append("<!ENTITY ");            
            internalSubset.append(name);
            if (publicId != null) {
                internalSubset.append(" PUBLIC \"");
                internalSubset.append(publicId);
            } else {
                internalSubset.append(" SYSTEM \"");
                internalSubset.append(systemId);
            }
            internalSubset.append("\">\n");
        }
    }

    public void internalEntityDecl(String name, String value) throws SAXException {
        if (entities == null) {
            entities = new HashMap();
        }
        entities.put(name, value);
        if (!inExternalSubset) {
            internalSubset.append("<!ENTITY ");
            internalSubset.append(name);
            internalSubset.append(" \"");
            internalSubset.append(value);
            internalSubset.append("\">\n");
        }
    }

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        if (!inExternalSubset) {
            internalSubset.append("<!NOTATION ");            
            internalSubset.append(name);
            if (publicId != null) {
                internalSubset.append(" PUBLIC \"");
                internalSubset.append(publicId);
            } else {
                internalSubset.append(" SYSTEM \"");
                internalSubset.append(systemId);
            }
            internalSubset.append("\">\n");
        }
    }

    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
            throws SAXException {
        if (!inExternalSubset) {
            internalSubset.append("<!ENTITY ");
            internalSubset.append(name);
            if (publicId != null) {
                internalSubset.append(" PUBLIC \"");
                internalSubset.append(publicId);
            } else {
                internalSubset.append(" SYSTEM \"");
                internalSubset.append(systemId);
            }
            internalSubset.append("\" NDATA ");
            internalSubset.append(notationName);
            internalSubset.append(">\n");
        }
    }

    public void endDTD() throws SAXException {
        factory.createOMDocType(target, dtdName, dtdPublicId, dtdSystemId,
                internalSubset.length() == 0 ? null : internalSubset.toString(), true);
        internalSubset = null;
    }

    protected OMElement createNextElement(String localName) throws OMException {
        return factory.createOMElement(localName, target, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (!inEntityReference) {
            if (nextElement == null) {
                nextElement = createNextElement("DUMMY");
            }
            ((OMElementEx)nextElement).addNamespaceDeclaration(uri, prefix);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        if (!inEntityReference) {
            if (localName == null || localName.trim().equals(""))
                localName = qName.substring(qName.indexOf(':') + 1);
            if (nextElement == null)
                nextElement = createNextElement(localName);
            else
                nextElement.setLocalName(localName);
    
            int idx = qName.indexOf(':');
            String prefix = idx == -1 ? "" : qName.substring(0, idx);
            BuilderUtil.setNamespace(nextElement, namespaceURI, prefix, false);
            
            int j = atts.getLength();
            for (int i = 0; i < j; i++) {
                // Note that some SAX parsers report namespace declarations as attributes in addition
                // to calling start/endPrefixMapping.
                // NOTE: This filter was introduced to make SAXOMBuilder work with some versions of
                //       XMLBeans (2.3.0). It is not clear whether this is a bug in XMLBeans or not.
                //       See http://forum.springframework.org/showthread.php?t=43958 for a discussion.
                //       If this test causes problems with other parsers, don't hesitate to remove it.
                if (!atts.getQName(i).startsWith("xmlns")) {
                    String attrNamespaceURI = atts.getURI(i);
                    OMNamespace ns;
                    if (attrNamespaceURI.length() > 0) {
                        ns = nextElement.findNamespace(atts.getURI(i), null);
                        if (ns == null) {
                            // The "xml" prefix is not necessarily declared explicitly; in this case,
                            // create a new OMNamespace instance.
                            if (attrNamespaceURI.equals(XMLConstants.XML_NS_URI)) {
                                ns = factory.createOMNamespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
                            } else {
                                throw new SAXException("Unbound namespace " + attrNamespaceURI);
                            }
                        }
                    } else {
                        ns = null;
                    }
                    OMAttribute attr = nextElement.addAttribute(atts.getLocalName(i), atts.getValue(i), ns);
                    attr.setAttributeType(atts.getType(i));
                }
            }
            
            target = (OMContainerEx)nextElement;
            nextElement = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (!inEntityReference) {
            target.setComplete(true);
            target = (OMContainerEx)((OMNode)target).getParent();
        }
    }

    public void startCDATA() throws SAXException {
        if (!inEntityReference) {
            textNodeType = OMNode.CDATA_SECTION_NODE;
        }
    }

    public void endCDATA() throws SAXException {
        if (!inEntityReference) {
            textNodeType = OMNode.TEXT_NODE;
        }
    }

    public void characterData(char[] ch, int start, int length, int nodeType)
            throws SAXException {
        if (!inEntityReference) {
            factory.createOMText(target, new String(ch, start, length), nodeType, true);
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (!inEntityReference) {
            characterData(ch, start, length, textNodeType);
        }
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (!inEntityReference) {
            characterData(ch, start, length, OMNode.SPACE_NODE);
        }
    }

    public void processingInstruction(String piTarget, String data)
            throws SAXException {
        if (!inEntityReference) {
            factory.createOMProcessingInstruction(target, piTarget, data, true);
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (!inEntityReference) {
            factory.createOMComment(target, new String(ch, start, length), true);
        }
    }

    public void skippedEntity(String name) throws SAXException {
        factory.createOMEntityReference(target, name, null, true);
    }

    public void startEntity(String name) throws SAXException {
        if (inEntityReference) {
            entityReferenceDepth++;
        } else if (name.equals("[dtd]")) {
            inExternalSubset = true;
        } else if (!expandEntityReferences) {
            factory.createOMEntityReference(target, name, entities == null ? null : (String)entities.get(name), true);
            inEntityReference = true;
            entityReferenceDepth = 1;
        }
    }

    public void endEntity(String name) throws SAXException {
        if (inEntityReference) {
            entityReferenceDepth--;
            if (entityReferenceDepth == 0) {
                inEntityReference = false;
            }
        } else if (name.equals("[dtd]")) {
            inExternalSubset = false;
        }
    }

    public OMDocument getDocument() {
        if (document == null && source != null) {
            XMLReader reader = source.getXMLReader();
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            try {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.setProperty("http://xml.org/sax/properties/declaration-handler", this);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.parse(source.getInputSource());
            } catch (IOException ex) {
                throw new OMException(ex);
            } catch (SAXException ex) {
                throw new OMException(ex);
            }
        }
        if (document != null && document.isComplete()) {
            return document;
        } else {
            throw new OMException("Tree not complete");
        }
    }
    
    /**
     * Get the root element of the Axiom tree built by this content handler.
     * 
     * @deprecated
     * @return the root element of the tree
     * @throws OMException if the tree is not complete
     */
    public OMElement getRootElement() {
        OMElement root = getDocumentElement();
        if (root != null && root.isComplete()) {
            return root;
        } else {
            throw new OMException("Tree not complete");
        }
    }

    public int next() throws OMException {
        throw new UnsupportedOperationException();
    }

    public void discard(OMElement el) throws OMException {
        throw new UnsupportedOperationException();
    }

    public void setCache(boolean b) throws OMException {
        throw new UnsupportedOperationException();
    }

    public boolean isCache() {
        throw new UnsupportedOperationException();
    }

    public Object getParser() {
        throw new UnsupportedOperationException();
    }

    public boolean isCompleted() {
        return document != null && document.isComplete();
    }

    public OMElement getDocumentElement() {
        return getDocument().getOMDocumentElement();
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        OMElement documentElement = getDocument().getOMDocumentElement();
        if (discardDocument) {
            documentElement.detach();
        }
        return documentElement;
    }

    public short getBuilderType() {
        throw new UnsupportedOperationException();
    }

    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        // This is a no-op
    }

    public void detach() {
        throw new UnsupportedOperationException();
    }
}
