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
import java.io.OutputStream;
import java.io.Writer;

import org.apache.axiom.core.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;

public class SerializerXmlHandler implements XmlHandler {
    private final ToStream serializer;
    
    public SerializerXmlHandler(Writer writer) {
        this.serializer = new ToStream(writer);
    }

    public SerializerXmlHandler(OutputStream out, String encoding) {
        this.serializer = new ToStream(out, encoding);
    }

    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            Boolean standalone) throws StreamException {
        serializer.startDocument(inputEncoding, xmlVersion, xmlEncoding, standalone);
    }
    
    @Override
    public void startFragment() throws StreamException {
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        serializer.startDTD(rootName, publicId, systemId);
        if (internalSubset != null) {
            serializer.writeInternalSubset(internalSubset);
        }
        serializer.endDTD();
    }

    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        serializer.closeStartTag();
        serializer.startElement(namespaceURI, localName, prefix);
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
    }

    public void endElement() throws StreamException {
        serializer.endElement();
    }

    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        serializer.closeStartTag();
        if (data instanceof CharacterData) {
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
            serializer.characters(data.toString());
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
        serializer.startComment();
    }

    @Override
    public void endComment() throws StreamException {
        serializer.endComment();
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        serializer.closeStartTag();
        serializer.startProcessingInstruction(target);
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        serializer.endProcessingInstruction();
    }

    public void processEntityReference(String name, String replacementText) throws StreamException {
        serializer.closeStartTag();
        serializer.processEntityReference(name);
    }

    public void completed() throws StreamException {
        serializer.completed();
    }

    public void flushBuffer() throws StreamException {
        serializer.flushBuffer();
    }

    public void writeRaw(String s, UnmappableCharacterHandler unmappableCharacterHandler) throws StreamException {
        serializer.writeRaw(s, unmappableCharacterHandler);
    }
}
