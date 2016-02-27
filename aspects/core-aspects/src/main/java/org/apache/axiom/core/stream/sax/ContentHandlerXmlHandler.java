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

import java.io.IOException;
import java.util.Stack;

import org.apache.axiom.core.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class ContentHandlerXmlHandler implements XmlHandler {
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    private String[] prefixStack = new String[16];
    private int bindings;
    private int[] scopeStack = new int[8];
    private boolean startDocumentWritten;
    private boolean autoStartDocument;
    private int depth;
    private Stack<String> elementNameStack = new Stack<String>();
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private final AttributesImpl attributes = new AttributesImpl();
    
    public ContentHandlerXmlHandler(ContentHandler contentHandler, LexicalHandler lexicalHandler) {
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }

    private static String getQName(String prefix, String localName) {
        if (prefix.length() == 0) {
            return localName;
        } else {
            return prefix + ":" + localName;
        }
    }
    
    private void writeStartDocument() throws StreamException {
        try {
            contentHandler.startDocument();
            startDocumentWritten = true;
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }
    
    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            boolean standalone) throws StreamException {
        writeStartDocument();
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        if (lexicalHandler != null) {
            try {
                lexicalHandler.startDTD(rootName, publicId, systemId);
                lexicalHandler.endDTD();
            } catch (SAXException ex) {
                throw new StreamException(ex);
            }
        }
    }

    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        if (!startDocumentWritten) {
            writeStartDocument();
            autoStartDocument = true;
        }
        elementURI = namespaceURI;
        elementLocalName = localName;
        elementQName = getQName(prefix, localName);
        if (depth == scopeStack.length) {
            int[] newScopeStack = new int[scopeStack.length*2];
            System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
            scopeStack = newScopeStack;
        }
        scopeStack[depth++] = bindings;
    }

    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        if (bindings == prefixStack.length) {
            String[] newPrefixStack = new String[prefixStack.length*2];
            System.arraycopy(prefixStack, 0, newPrefixStack, 0, prefixStack.length);
            prefixStack = newPrefixStack;
        }
        prefixStack[bindings++] = prefix;
        try {
            contentHandler.startPrefixMapping(prefix, namespaceURI);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
        // TODO: depending on the http://xml.org/sax/features/xmlns-uris feature, we also need to add an attribute
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        attributes.addAttribute(namespaceURI, localName, getQName(prefix, localName), type, value);
    }

    public void attributesCompleted() throws StreamException {
        try {
            contentHandler.startElement(elementURI, elementLocalName, elementQName, attributes);
            elementNameStack.push(elementURI);
            elementNameStack.push(elementLocalName);
            elementNameStack.push(elementQName);
            elementURI = null;
            elementLocalName = null;
            elementQName = null;
            attributes.clear();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void endElement() throws StreamException {
        try {
            String elementQName = elementNameStack.pop();
            String elementLocalName = elementNameStack.pop();
            String elementURI = elementNameStack.pop();
            contentHandler.endElement(elementURI, elementLocalName, elementQName);
            for (int i=bindings-1; i>=scopeStack[depth-1]; i--) {
                contentHandler.endPrefixMapping(prefixStack[i]);
            }
            bindings = scopeStack[--depth];
            if (depth == 0 && autoStartDocument) {
                contentHandler.endDocument();
            }
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        try {
            if (ignorable) {
                char[] ch = data.toString().toCharArray();
                contentHandler.ignorableWhitespace(ch, 0, ch.length);
            } else if (data instanceof CharacterData) {
                try {
                    ((CharacterData)data).writeTo(new ContentHandlerWriter(contentHandler));
                } catch (IOException ex) {
                    Throwable cause = ex.getCause();
                    SAXException saxException;
                    if (cause instanceof SAXException) {
                        saxException = (SAXException)cause;
                    } else {
                        saxException = new SAXException(ex);
                    }
                    throw new StreamException(saxException);
                }
            } else {
                char[] ch = data.toString().toCharArray();
                contentHandler.characters(ch, 0, ch.length);
            }
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }
    
    @Override
    public void startCDATASection() throws StreamException {
        try {
            if (lexicalHandler != null) {
                lexicalHandler.startCDATA();
            }
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void endCDATASection() throws StreamException {
        try {
            if (lexicalHandler != null) {
                lexicalHandler.endCDATA();
            }
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void processComment(String data) throws StreamException {
        if (lexicalHandler != null) {
            char[] ch = data.toCharArray();
            try {
                lexicalHandler.comment(ch, 0, ch.length);
            } catch (SAXException ex) {
                throw new StreamException(ex);
            }
        }
    }

    public void processProcessingInstruction(String target, String data) throws StreamException {
        try {
            contentHandler.processingInstruction(target, data);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void processEntityReference(String name, String replacementText) throws StreamException {
        try {
            contentHandler.skippedEntity(name);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void endDocument() throws StreamException {
        try {
            contentHandler.endDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }
}
