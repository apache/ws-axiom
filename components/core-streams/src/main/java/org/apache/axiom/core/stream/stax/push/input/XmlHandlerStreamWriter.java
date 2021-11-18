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
package org.apache.axiom.core.stream.stax.push.input;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class XmlHandlerStreamWriter implements InternalXMLStreamWriter, NamespaceContext {
    private static final Log log = LogFactory.getLog(XmlHandlerStreamWriter.class);

    private final XmlHandler handler;
    private final Serializer serializer;
    private final XMLStreamWriterExtensionFactory extensionFactory;
    private Map<String,Object> extensions;
    private boolean inStartElement;
    private boolean inEmptyElement;

    /**
     * Array containing the prefixes for the namespace bindings.
     */
    private String[] prefixArray = new String[16];
    
    /**
     * Array containing the URIs for the namespace bindings.
     */
    private String[] uriArray = new String[16];
    
    /**
     * The number of currently defined namespace bindings.
     */
    private int bindings;
    
    /**
     * Tracks the scopes defined for this namespace context. Each entry in the array identifies
     * the first namespace binding defined in the corresponding scope and points to an entry
     * in {@link #prefixArray}/{@link #uriArray}.
     */
    private int[] scopeIndexes = new int[16];
    
    /**
     * The number of currently defined scopes. This is the same as the depth of the current scope,
     * where the depth of the root scope is 0.
     */
    private int scopes;

    public XmlHandlerStreamWriter(XmlHandler handler, Serializer serializer,
            XMLStreamWriterExtensionFactory extensionFactory) {
        this.handler = handler;
        this.serializer = serializer;
        this.extensionFactory = extensionFactory;
    }
    
    public XmlHandler getHandler() {
        return handler;
    }

    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    private static XMLStreamException toXMLStreamException(StreamException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof XMLStreamException) {
            return (XMLStreamException)cause;
        } else {
            return new XMLStreamException(ex);
        }
    }
    
    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (extensions != null) {
            Object extension = extensions.get(name);
            if (extension != null) {
                return extension;
            }
        }
        if (extensionFactory != null) {
            Object extension = extensionFactory.createExtension(name, this);
            if (extension != null) {
                if (extensions == null) {
                    extensions = new HashMap<String,Object>();
                }
                extensions.put(name, extension);
                return extension;
            }
        }
        throw new IllegalArgumentException("Unsupported property " + name);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this;
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        // We currently don't support this method
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPrefix(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("namespaceURI can't be null");
        } else if (uri.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (uri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        } else {
            outer: for (int i=bindings-1; i>=0; i--) {
                if (uri.equals(uriArray[i])) {
                    String prefix = prefixArray[i];
                    // Now check that the prefix is not masked
                    for (int j=i+1; j<bindings; j++) {
                        if (prefix.equals(prefixArray[j])) {
                            continue outer;
                        }
                    }
                    return prefix;
                }
            }
            return null;
        }
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        setPrefix("", uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (inEmptyElement) {
            log.warn("The behavior of XMLStreamWriter#setPrefix and " +
                    "XMLStreamWriter#setDefaultNamespace is undefined when invoked in the " +
                    "context of an empty element");
        }
        internalSetPrefix(normalize(prefix), normalize(uri));
    }
    
    private void internalSetPrefix(String prefix, String uri) {
        if (bindings == prefixArray.length) {
            int len = prefixArray.length;
            int newLen = len*2;
            String[] newPrefixArray = new String[newLen];
            System.arraycopy(prefixArray, 0, newPrefixArray, 0, len);
            String[] newUriArray = new String[newLen];
            System.arraycopy(uriArray, 0, newUriArray, 0, len);
            prefixArray = newPrefixArray;
            uriArray = newUriArray;
        }
        prefixArray[bindings] = prefix;
        uriArray[bindings] = uri;
        bindings++;
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        try {
            handler.startDocument(null, "1.0", null, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        try {
            handler.startDocument(null, version, encoding, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        try {
            handler.startDocument(null, version, null, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            handler.completed();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        if (serializer != null) {
            try {
                serializer.writeRaw(dtd, UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE);
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void doWriteStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startElement(normalize(namespaceURI), localName, normalize(prefix));
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inStartElement = true;
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        doWriteStartElement(prefix, localName, namespaceURI);
        if (scopes == scopeIndexes.length) {
            int[] newScopeIndexes = new int[scopeIndexes.length*2];
            System.arraycopy(scopeIndexes, 0, newScopeIndexes, 0, scopeIndexes.length);
            scopeIndexes = newScopeIndexes;
        }
        scopeIndexes[scopes++] = bindings;
        inEmptyElement = false;
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    private void doWriteEndElement() throws XMLStreamException {
        finishStartElement();
        try {
            handler.endElement();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        doWriteEndElement();
        bindings = scopeIndexes[--scopes];
        inEmptyElement = false;
    }

    private void finishStartElement() throws XMLStreamException {
        if (inStartElement) {
            try {
                handler.attributesCompleted();
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
            inStartElement = false;
        }
    }
    
    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        doWriteStartElement(prefix, localName, namespaceURI);
        finishStartElement();
        doWriteEndElement();
        inEmptyElement = true;
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws XMLStreamException {
        try {
            handler.processAttribute(normalize(namespaceURI), localName, normalize(prefix), value, "CDATA", true);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        writeAttribute(null, null, localName, value);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        prefix = normalize(prefix);
        namespaceURI = normalize(namespaceURI);
        try {
            handler.processNamespaceDeclaration(prefix, namespaceURI);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        internalSetPrefix(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        writeNamespace("", namespaceURI);
    }

    private String internalGetPrefix(String namespaceURI) throws XMLStreamException {
        String prefix = getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        } else {
            return prefix;
        }
    }
    
    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        writeStartElement(internalGetPrefix(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName)
            throws XMLStreamException {
        writeEmptyElement(internalGetPrefix(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        writeAttribute(internalGetPrefix(namespaceURI), namespaceURI, localName, value);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        writeCharacterData(new String(text, start, len));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        writeCharacterData(text);
    }

    @Override
    public void writeCharacterData(Object data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.processCharacterData(data, false);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startCDATASection();
            handler.processCharacterData(data, false);
            handler.endCDATASection();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startComment();
            handler.processCharacterData(data, false);
            handler.endComment();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        finishStartElement();
        try {
            handler.processEntityReference(name, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeProcessingInstruction(String target, String data)
            throws XMLStreamException {
        finishStartElement();
        try {
            handler.startProcessingInstruction(target);
            handler.processCharacterData(data, false);
            handler.endProcessingInstruction();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        writeProcessingInstruction(target, "");
    }

    @Override
    public void flush() throws XMLStreamException {
        if (serializer != null) {
            try {
                serializer.flushBuffer();
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
        }
    }

    @Override
    public void close() throws XMLStreamException {
        flush();
    }

    @Override
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix can't be null");
        } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else {
            for (int i=bindings-1; i>=0; i--) {
                if (prefix.equals(prefixArray[i])) {
                    return uriArray[i];
                }
            }
            // The Javadoc of NamespaceContext#getNamespaceURI specifies that XMLConstants.NULL_NS_URI
            // is returned both for unbound prefixes and the null namespace
            return XMLConstants.NULL_NS_URI;
        }
    }

    @Override
    public final Iterator<String> getPrefixes(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI can't be null");
        } else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
            return Collections.singleton(XMLConstants.XML_NS_PREFIX).iterator();
        } else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return Collections.singleton(XMLConstants.XMLNS_ATTRIBUTE).iterator();
        } else {
            return new Iterator<String>() {
                private int binding = bindings;
                private String next;

                @Override
                public boolean hasNext() {
                    if (next == null) {
                        outer: while (--binding >= 0) {
                            if (namespaceURI.equals(uriArray[binding])) {
                                String prefix = prefixArray[binding];
                                // Now check that the prefix is not masked
                                for (int j=binding+1; j<bindings; j++) {
                                    if (prefix.equals(prefixArray[j])) {
                                        continue outer;
                                    }
                                }
                                next = prefix;
                                break;
                            }
                        }
                    }
                    return next != null;
                }

                @Override
                public String next() {
                    if (hasNext()) {
                        String result = next;
                        next = null;
                        return result;
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
