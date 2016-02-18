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
package org.apache.axiom.core.stream.sax;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import java.util.HashMap;
import java.util.Map;

public final class XmlHandlerContentHandler implements ContentHandler, LexicalHandler, DeclHandler, DTDHandler {
    private final XmlHandler handler;
    private final boolean expandEntityReferences;
    
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
    private Map<String,String> entities;
    
    /**
     * Flag indicating that the parser is processing the external subset.
     */
    private boolean inExternalSubset;

    /**
     * Stores namespace declarations reported to {@link #startPrefixMapping(String, String)}. These
     * declarations will be passed to the {@link XmlHandler} by
     * {@link #startElement(String, String, String, Attributes)}. Each declaration is stored as
     * (prefix, uri) pair using two array elements.
     */
    private String[] namespaces = new String[16];

    /**
     * The number of namespace declarations stored in {@link #namespaces}.
     */
    private int namespaceCount;

    private boolean inCDATASection;
    
    private boolean inEntityReference;
    private int entityReferenceDepth;

    public XmlHandlerContentHandler(XmlHandler handler, boolean expandEntityReferences) {
        this.handler = handler;
        this.expandEntityReferences = expandEntityReferences;
    }
    
    private static SAXException toSAXException(StreamException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof SAXException) {
            return (SAXException)cause;
        } else {
            return new SAXException(ex);
        }
    }
    
    public final void setDocumentLocator(Locator locator) {
    }

    public final void startDocument() throws SAXException {
        try {
            handler.startDocument(null, "1.0", null, true);
        } catch (StreamException ex) {
            throw toSAXException(ex);
        }
    }

    public final void endDocument() throws SAXException {
        try {
            handler.endDocument();
        } catch (StreamException ex) {
            throw toSAXException(ex);
        }
    }

    public final void startDTD(String name, String publicId, String systemId) throws SAXException {
        dtdName = name;
        dtdPublicId = publicId;
        dtdSystemId = systemId;
        internalSubset = new StringBuilder();
    }

    public final void elementDecl(String name, String model) throws SAXException {
        if (!inExternalSubset) {
            internalSubset.append("<!ELEMENT ");
            internalSubset.append(name);
            internalSubset.append(' ');
            internalSubset.append(model);
            internalSubset.append(">\n");
        }
    }

    public final void attributeDecl(String eName, String aName, String type, String mode, String value)
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

    public final void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
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

    public final void internalEntityDecl(String name, String value) throws SAXException {
        if (entities == null) {
            entities = new HashMap<String,String>();
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

    public final void notationDecl(String name, String publicId, String systemId) throws SAXException {
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

    public final void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
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

    public final void endDTD() throws SAXException {
        try {
            handler.processDocumentTypeDeclaration(dtdName, dtdPublicId, dtdSystemId,
                    internalSubset.length() == 0 ? null : internalSubset.toString());
        } catch (StreamException ex) {
            throw toSAXException(ex);
        }
        internalSubset = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    public final void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (!inEntityReference) {
            int index = namespaceCount*2;
            if (index == namespaces.length) {
                String[] newNamespaces = new String[namespaces.length*2];
                System.arraycopy(namespaces, 0, newNamespaces, 0, namespaces.length);
                namespaces = newNamespaces;
            }
            namespaces[index] = prefix;
            namespaces[index+1] = uri;
            namespaceCount++;
        }
    }

    public final void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public final void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        if (inEntityReference) {
            return;
        }
        try {
            if (localName == null || localName.trim().equals(""))
                localName = qName.substring(qName.indexOf(':') + 1);
            int idx = qName.indexOf(':');
            String prefix = idx == -1 ? "" : qName.substring(0, idx);
            handler.startElement(namespaceURI, localName, prefix);
            for (int i = 0; i < namespaceCount; i++) {
                handler.processNamespaceDeclaration(namespaces[2*i], namespaces[2*i+1]);
            }
            namespaceCount = 0;
    
            int j = atts.getLength();
            for (int i = 0; i < j; i++) {
                String attrQName = atts.getQName(i);
                // Note that some SAX parsers report namespace declarations as attributes in addition
                // to calling start/endPrefixMapping.
                // NOTE: This filter was introduced to make SAXOMBuilder work with some versions of
                //       XMLBeans (2.3.0). It is not clear whether this is a bug in XMLBeans or not.
                //       See http://forum.springframework.org/showthread.php?t=43958 for a discussion.
                //       If this test causes problems with other parsers, don't hesitate to remove it.
                if (!attrQName.startsWith("xmlns")) {
                    idx = attrQName.indexOf(':');
                    String attrPrefix = idx == -1 ? "" : attrQName.substring(0, idx);
                    handler.processAttribute(atts.getURI(i), atts.getLocalName(i), attrPrefix, atts.getValue(i), atts.getType(i), true);
                }
            }
            handler.attributesCompleted();
        } catch (StreamException ex) {
            throw toSAXException(ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public final void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (!inEntityReference) {
            try {
                handler.endElement();
            } catch (StreamException ex) {
                throw toSAXException(ex);
            }
        }
    }

    public final void startCDATA() throws SAXException {
        if (!inEntityReference) {
            inCDATASection = true;
        }
    }

    public final void endCDATA() throws SAXException {
        if (!inEntityReference) {
            inCDATASection = false;
        }
    }

    public final void characters(char[] ch, int start, int length)
            throws SAXException {
        if (!inEntityReference) {
            try {
                if (inCDATASection) {
                    // TODO: incorrect because it may split CDATA sections
                    handler.processCDATASection(new String(ch, start, length));
                } else {
                    handler.processCharacterData(new String(ch, start, length), false);
                }
            } catch (StreamException ex) {
                throw toSAXException(ex);
            }
        }
    }
    
    public final void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (!inEntityReference) {
            try {
                handler.processCharacterData(new String(ch, start, length), true);
            } catch (StreamException ex) {
                throw toSAXException(ex);
            }
        }
    }

    public final void processingInstruction(String piTarget, String data)
            throws SAXException {
        if (!inEntityReference) {
            try {
                handler.processProcessingInstruction(piTarget, data);
            } catch (StreamException ex) {
                throw toSAXException(ex);
            }
        }
    }

    public final void comment(char[] ch, int start, int length) throws SAXException {
        if (!inEntityReference) {
            try {
                handler.processComment(new String(ch, start, length));
            } catch (StreamException ex) {
                throw toSAXException(ex);
            }
        }
    }

    public final void skippedEntity(String name) throws SAXException {
        try {
            handler.processEntityReference(name, null);
        } catch (StreamException ex) {
            throw toSAXException(ex);
        }
    }

    public final void startEntity(String name) throws SAXException {
        if (inEntityReference) {
            entityReferenceDepth++;
        } else if (name.equals("[dtd]")) {
            inExternalSubset = true;
        } else if (!expandEntityReferences) {
            try {
                handler.processEntityReference(name, entities == null ? null : entities.get(name));
            } catch (StreamException ex) {
                throw toSAXException(ex);
            }
            inEntityReference = true;
            entityReferenceDepth = 1;
        }
    }

    public final void endEntity(String name) throws SAXException {
        if (inEntityReference) {
            entityReferenceDepth--;
            if (entityReferenceDepth == 0) {
                inEntityReference = false;
            }
        } else if (name.equals("[dtd]")) {
            inExternalSubset = false;
        }
    }
}
