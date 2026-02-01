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
package org.apache.axiom.core.stream.sax.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.CharacterDataSink;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.util.CharacterDataAccumulator;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class ContentHandlerXmlHandler implements XmlHandler, CharacterDataSink {
    private enum CharacterDataMode {
        PASS_THROUGH,
        BUFFER,
        SKIP,
        ACCUMULATE
    };

    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    private String[] prefixStack = new String[16];
    private int bindings;
    private int[] scopeStack = new int[8];
    private int depth;
    private Stack<String> elementNameStack = new Stack<String>();
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private final AttributesImpl attributes = new AttributesImpl();
    private CharacterDataMode characterDataMode = CharacterDataMode.PASS_THROUGH;
    private char[] buffer = new char[4096];
    private int bufferPos;
    private CharacterDataAccumulator accumulator;
    private String piTarget;

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

    @Override
    public void startDocument(
            String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone)
            throws StreamException {
        try {
            contentHandler.startDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startFragment() throws StreamException {
        try {
            contentHandler.startDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processDocumentTypeDeclaration(
            String rootName, String publicId, String systemId, String internalSubset)
            throws StreamException {
        if (lexicalHandler != null) {
            try {
                lexicalHandler.startDTD(rootName, publicId, systemId);
                lexicalHandler.endDTD();
            } catch (SAXException ex) {
                throw new StreamException(ex);
            }
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        elementURI = namespaceURI;
        elementLocalName = localName;
        elementQName = getQName(prefix, localName);
        if (depth == scopeStack.length) {
            int[] newScopeStack = new int[scopeStack.length * 2];
            System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
            scopeStack = newScopeStack;
        }
        scopeStack[depth++] = bindings;
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        if (bindings == prefixStack.length) {
            String[] newPrefixStack = new String[prefixStack.length * 2];
            System.arraycopy(prefixStack, 0, newPrefixStack, 0, prefixStack.length);
            prefixStack = newPrefixStack;
        }
        prefixStack[bindings++] = prefix;
        try {
            contentHandler.startPrefixMapping(prefix, namespaceURI);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
        // TODO: depending on the http://xml.org/sax/features/xmlns-uris feature, we also need to
        // add an attribute
    }

    @Override
    public void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified)
            throws StreamException {
        attributes.addAttribute(namespaceURI, localName, getQName(prefix, localName), type, value);
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
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

    @Override
    public void endElement() throws StreamException {
        try {
            String elementQName = elementNameStack.pop();
            String elementLocalName = elementNameStack.pop();
            String elementURI = elementNameStack.pop();
            contentHandler.endElement(elementURI, elementLocalName, elementQName);
            for (int i = bindings - 1; i >= scopeStack[depth - 1]; i--) {
                contentHandler.endPrefixMapping(prefixStack[i]);
            }
            bindings = scopeStack[--depth];
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    private void writeToBuffer(String data) {
        int dataLen = data.length();
        if (buffer.length - bufferPos < dataLen) {
            int newLength = buffer.length;
            do {
                newLength *= 2;
            } while (newLength - bufferPos < dataLen);
            char[] newBuffer = new char[newLength];
            System.arraycopy(buffer, 0, newBuffer, 0, bufferPos);
            buffer = newBuffer;
        }
        data.getChars(0, dataLen, buffer, bufferPos);
        bufferPos += dataLen;
    }

    @Override
    public Writer getWriter() {
        return new ContentHandlerWriter(contentHandler);
    }

    @Override
    public AbstractBase64EncodingOutputStream getBase64EncodingOutputStream() {
        return new Base64EncodingWriterOutputStream(getWriter());
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        try {
            switch (characterDataMode) {
                case PASS_THROUGH -> {
                    if (ignorable) {
                        writeToBuffer(data.toString());
                        contentHandler.ignorableWhitespace(buffer, 0, bufferPos);
                        bufferPos = 0;
                    } else if (data instanceof CharacterData characterData) {
                        try {
                            characterData.writeTo(this);
                        } catch (IOException ex) {
                            Throwable cause = ex.getCause();
                            SAXException saxException;
                            if (cause instanceof SAXException se) {
                                saxException = se;
                            } else {
                                saxException = new SAXException(ex);
                            }
                            throw new StreamException(saxException);
                        }
                    } else {
                        writeToBuffer(data.toString());
                        contentHandler.characters(buffer, 0, bufferPos);
                        bufferPos = 0;
                    }
                }
                case BUFFER -> writeToBuffer(data.toString());
                case ACCUMULATE -> accumulator.append(data);
                case SKIP -> {}
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

    @Override
    public void startComment() throws StreamException {
        characterDataMode =
                lexicalHandler == null ? CharacterDataMode.SKIP : CharacterDataMode.BUFFER;
    }

    @Override
    public void endComment() throws StreamException {
        if (lexicalHandler != null) {
            try {
                lexicalHandler.comment(buffer, 0, bufferPos);
                bufferPos = 0;
            } catch (SAXException ex) {
                throw new StreamException(ex);
            }
        }
        characterDataMode = CharacterDataMode.PASS_THROUGH;
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        if (accumulator == null) {
            accumulator = new CharacterDataAccumulator();
        }
        piTarget = target;
        characterDataMode = CharacterDataMode.ACCUMULATE;
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        try {
            contentHandler.processingInstruction(piTarget, accumulator.toString());
            accumulator.clear();
            piTarget = null;
            characterDataMode = CharacterDataMode.PASS_THROUGH;
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        try {
            contentHandler.skippedEntity(name);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void completed() throws StreamException {
        try {
            contentHandler.endDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public boolean drain() throws StreamException {
        return true;
    }
}
