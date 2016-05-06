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
package org.apache.axiom.core.stream.serializer;

import java.io.IOException;
import java.util.Stack;

import org.apache.axiom.core.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.util.CharacterDataAccumulator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SerializerXmlHandler implements XmlHandler {
    private enum CharacterDataMode { PASS_THROUGH, BUFFER, SKIP, ACCUMULATE };
    
    private final ToXMLStream serializer;
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
    
    public SerializerXmlHandler(ToXMLStream serializer) {
        this.serializer = serializer;
    }

    private static String getQName(String prefix, String localName) {
        if (prefix.length() == 0) {
            return localName;
        } else {
            return prefix + ":" + localName;
        }
    }
    
    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            boolean standalone) throws StreamException {
        try {
            serializer.startDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }
    
    @Override
    public void startFragment() throws StreamException {
        try {
            serializer.startDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        try {
            serializer.startDTD(rootName, publicId, systemId);
            serializer.endDTD();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
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
            serializer.startPrefixMapping(prefix, namespaceURI);
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
            serializer.startElement(elementURI, elementLocalName, elementQName, attributes);
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
            serializer.endElement(elementURI, elementLocalName, elementQName);
            for (int i=bindings-1; i>=scopeStack[depth-1]; i--) {
                serializer.endPrefixMapping(prefixStack[i]);
            }
            bindings = scopeStack[--depth];
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    private void writeToBuffer(String data) {
        int dataLen = data.length();
        if (buffer.length-bufferPos < dataLen) {
            int newLength = buffer.length;
            do {
                newLength *= 2;
            } while (newLength-bufferPos < dataLen);
            char[] newBuffer = new char[newLength];
            System.arraycopy(buffer, 0, newBuffer, 0, bufferPos);
            buffer = newBuffer;
        }
        data.getChars(0, dataLen, buffer, bufferPos);
        bufferPos += dataLen;
    }

    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        try {
            switch (characterDataMode) {
                case PASS_THROUGH:
                    if (ignorable) {
                        writeToBuffer(data.toString());
                        serializer.ignorableWhitespace(buffer, 0, bufferPos);
                        bufferPos = 0;
                    } else if (data instanceof CharacterData) {
                        try {
                            ((CharacterData)data).writeTo(new SerializerWriter(serializer));
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
                        writeToBuffer(data.toString());
                        serializer.characters(buffer, 0, bufferPos);
                        bufferPos = 0;
                    }
                    break;
                case BUFFER:
                    writeToBuffer(data.toString());
                    break;
                case ACCUMULATE:
                    accumulator.append(data);
                    break;
                case SKIP:
            }
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }
    
    @Override
    public void startCDATASection() throws StreamException {
        try {
            serializer.startCDATA();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void endCDATASection() throws StreamException {
        try {
            serializer.endCDATA();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startComment() throws StreamException {
        characterDataMode = CharacterDataMode.BUFFER;
    }

    @Override
    public void endComment() throws StreamException {
        try {
            serializer.comment(buffer, 0, bufferPos);
            bufferPos = 0;
        } catch (SAXException ex) {
            throw new StreamException(ex);
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
            serializer.processingInstruction(piTarget, accumulator.toString());
            accumulator.clear();
            piTarget = null;
            characterDataMode = CharacterDataMode.PASS_THROUGH;
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void processEntityReference(String name, String replacementText) throws StreamException {
        try {
            serializer.skippedEntity(name);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }

    public void completed() throws StreamException {
        try {
            serializer.endDocument();
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
    }
}
