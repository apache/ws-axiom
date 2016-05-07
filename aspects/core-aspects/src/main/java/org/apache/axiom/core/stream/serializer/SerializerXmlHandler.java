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
import java.io.Writer;
import java.util.Stack;

import org.apache.axiom.core.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.util.CharacterDataAccumulator;

public class SerializerXmlHandler implements XmlHandler {
    private enum CharacterDataMode { PASS_THROUGH, BUFFER, SKIP, ACCUMULATE };
    
    private final ToXMLStream serializer;
    private Stack<String> elementNameStack = new Stack<String>();
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private CharacterDataMode characterDataMode = CharacterDataMode.PASS_THROUGH;
    private char[] buffer = new char[4096];
    private int bufferPos;
    private CharacterDataAccumulator accumulator;
    private String piTarget;
    
    public SerializerXmlHandler(Writer writer) {
        this.serializer = new ToXMLStream();
        serializer.setWriter(writer);
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
        serializer.startDocument();
    }
    
    @Override
    public void startFragment() throws StreamException {
        serializer.startDocument();
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        serializer.startDTD(rootName, publicId, systemId);
        serializer.endDTD();
    }

    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        serializer.closeStartTag();
        elementURI = namespaceURI;
        elementLocalName = localName;
        elementQName = getQName(prefix, localName);
        serializer.startElement(elementURI, elementLocalName, elementQName);
    }

    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        if (prefix.isEmpty()) {
            serializer.writeAttribute("", "xmlns", namespaceURI);
        } else {
            serializer.writeAttribute("xmlns", prefix, namespaceURI);
        }
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        serializer.writeAttribute(prefix, localName, value);
    }

    public void attributesCompleted() throws StreamException {
        elementNameStack.push(elementURI);
        elementNameStack.push(elementLocalName);
        elementNameStack.push(elementQName);
        elementURI = null;
        elementLocalName = null;
        elementQName = null;
    }

    public void endElement() throws StreamException {
        String elementQName = elementNameStack.pop();
        String elementLocalName = elementNameStack.pop();
        String elementURI = elementNameStack.pop();
        serializer.endElement(elementURI, elementLocalName, elementQName);
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
        serializer.closeStartTag();
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
                        if (cause instanceof StreamException) {
                            throw (StreamException)cause;
                        } else {
                            throw new StreamException(ex);
                        }
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
    }
    
    @Override
    public void startCDATASection() throws StreamException {
        serializer.closeStartTag();
        serializer.startCDATA();
    }

    @Override
    public void endCDATASection() throws StreamException {
        serializer.endCDATA();
    }

    @Override
    public void startComment() throws StreamException {
        serializer.closeStartTag();
        characterDataMode = CharacterDataMode.BUFFER;
    }

    @Override
    public void endComment() throws StreamException {
        serializer.comment(buffer, 0, bufferPos);
        bufferPos = 0;
        characterDataMode = CharacterDataMode.PASS_THROUGH;
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        serializer.closeStartTag();
        if (accumulator == null) {
            accumulator = new CharacterDataAccumulator();
        }
        piTarget = target;
        characterDataMode = CharacterDataMode.ACCUMULATE;
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        serializer.processingInstruction(piTarget, accumulator.toString());
        accumulator.clear();
        piTarget = null;
        characterDataMode = CharacterDataMode.PASS_THROUGH;
    }

    public void processEntityReference(String name, String replacementText) throws StreamException {
        serializer.closeStartTag();
        serializer.entityReference(name);
    }

    public void completed() throws StreamException {
        serializer.endDocument();
    }
}
