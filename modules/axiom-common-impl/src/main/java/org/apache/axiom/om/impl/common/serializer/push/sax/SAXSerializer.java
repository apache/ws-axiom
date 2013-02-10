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
package org.apache.axiom.om.impl.common.serializer.push.sax;

import java.io.IOException;
import java.util.Stack;

import javax.activation.DataHandler;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SAXSerializer extends Serializer {
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    private final ScopedNamespaceContext nsContext = new ScopedNamespaceContext();
    private boolean startDocumentWritten;
    private boolean autoStartDocument;
    private int depth;
    private Stack elementNameStack = new Stack();
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private final AttributesImpl attributes = new AttributesImpl();
    
    public SAXSerializer(OMSerializable contextNode, ContentHandler contentHandler, LexicalHandler lexicalHandler) {
        super(contextNode);
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }

    protected boolean isAssociated(String prefix, String namespace) throws OutputException {
        return nsContext.getNamespaceURI(prefix).equals(namespace);
    }

    private static String getQName(String prefix, String localName) {
        if (prefix.length() == 0) {
            return localName;
        } else {
            return prefix + ":" + localName;
        }
    }
    
    private void writeStartDocument() throws OutputException {
        try {
            contentHandler.startDocument();
            startDocumentWritten = true;
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
    
    public void writeStartDocument(String version) throws OutputException {
        writeStartDocument();
    }

    public void writeStartDocument(String encoding, String version) throws OutputException {
        writeStartDocument();
    }

    public void writeDTD(String rootName, String publicId, String systemId, String internalSubset) throws OutputException {
        if (lexicalHandler != null) {
            try {
                lexicalHandler.startDTD(rootName, publicId, systemId);
                lexicalHandler.endDTD();
            } catch (SAXException ex) {
                throw new SAXOutputException(ex);
            }
        }
    }

    protected void beginStartElement(String prefix, String namespaceURI, String localName) throws OutputException {
        if (!startDocumentWritten) {
            writeStartDocument();
            autoStartDocument = true;
        }
        elementURI = namespaceURI;
        elementLocalName = localName;
        elementQName = getQName(prefix, localName);
        nsContext.startScope();
        depth++;
    }

    protected void addNamespace(String prefix, String namespaceURI) throws OutputException {
        nsContext.setPrefix(prefix, namespaceURI);
        try {
            contentHandler.startPrefixMapping(prefix, namespaceURI);
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
        // TODO: depending on the http://xml.org/sax/features/xmlns-uris feature, we also need to add an attribute
    }

    protected void addAttribute(String prefix, String namespaceURI, String localName, String type, String value) throws OutputException {
        attributes.addAttribute(namespaceURI, localName, getQName(prefix, localName), type, value);
    }

    protected void finishStartElement() throws OutputException {
        try {
            contentHandler.startElement(elementURI, elementLocalName, elementQName, attributes);
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
        elementNameStack.push(elementURI);
        elementNameStack.push(elementLocalName);
        elementNameStack.push(elementQName);
        elementURI = null;
        elementLocalName = null;
        elementQName = null;
        attributes.clear();
    }

    public void writeEndElement() throws OutputException {
        try {
            String elementQName = (String)elementNameStack.pop();
            String elementLocalName = (String)elementNameStack.pop();
            String elementURI = (String)elementNameStack.pop();
            contentHandler.endElement(elementURI, elementLocalName, elementQName);
            for (int i=nsContext.getBindingsCount()-1; i>=nsContext.getFirstBindingInCurrentScope(); i--) {
                contentHandler.endPrefixMapping(nsContext.getPrefix(i));
            }
            nsContext.endScope();
            if (--depth == 0 && autoStartDocument) {
                contentHandler.endDocument();
            }
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }

    public void writeText(int type, String data) throws OutputException {
        char[] ch = data.toCharArray();
        try {
            switch (type) {
                case OMNode.TEXT_NODE:
                    contentHandler.characters(ch, 0, ch.length);
                    break;
                case OMNode.CDATA_SECTION_NODE:
                    if (lexicalHandler != null) {
                        lexicalHandler.startCDATA();
                    }
                    contentHandler.characters(ch, 0, ch.length);
                    if (lexicalHandler != null) {
                        lexicalHandler.endCDATA();
                    }
                    break;
                case OMNode.SPACE_NODE:
                    contentHandler.ignorableWhitespace(ch, 0, ch.length);
            }
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }

    public void writeComment(String data) throws OutputException {
        if (lexicalHandler != null) {
            char[] ch = data.toCharArray();
            try {
                lexicalHandler.comment(ch, 0, ch.length);
            } catch (SAXException ex) {
                throw new SAXOutputException(ex);
            }
        }
    }

    public void writeProcessingInstruction(String target, String data) throws OutputException {
        try {
            contentHandler.processingInstruction(target, data);
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }

    public void writeEntityRef(String name) throws OutputException {
        try {
            contentHandler.skippedEntity(name);
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize) throws OutputException {
        Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(new ContentHandlerWriter(contentHandler));
        try {
            dataHandler.writeTo(out);
            out.complete();
        } catch (IOException ex) {
            Throwable cause = ex.getCause();
            SAXException saxException;
            if (cause instanceof SAXException) {
                saxException = (SAXException)ex.getCause();
            } else {
                saxException = new SAXException(ex);
            }
            throw new SAXOutputException(saxException);
        }
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID,
            boolean optimize) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void serializePushOMDataSource(OMDataSource dataSource) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeEndDocument() throws OutputException {
        try {
            contentHandler.endDocument();
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }
}
