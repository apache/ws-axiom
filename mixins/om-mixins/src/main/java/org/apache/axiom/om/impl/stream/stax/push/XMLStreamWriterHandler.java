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
package org.apache.axiom.om.impl.stream.stax.push;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.util.CharacterDataAccumulator;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterHandler implements XmlHandler {
    private final XMLStreamWriter writer;
    private DataHandlerWriter dataHandlerWriter;
    private final CharacterDataAccumulator buffer = new CharacterDataAccumulator();
    private boolean buffering;
    private String piTarget;

    public XMLStreamWriterHandler(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public XMLStreamWriter getWriter() {
        return writer;
    }

    private String stopBuffering() {
        String content = buffer.toString();
        buffer.clear();
        buffering = false;
        return content;
    }

    @Override
    public void startDocument(
            String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone)
            throws StreamException {
        try {
            if (xmlEncoding == null) {
                writer.writeStartDocument(xmlVersion);
            } else {
                writer.writeStartDocument(xmlEncoding, xmlVersion);
            }
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startFragment() throws StreamException {}

    @Override
    public void processDocumentTypeDeclaration(
            String rootName, String publicId, String systemId, String internalSubset)
            throws StreamException {
        StringWriter sw = new StringWriter();
        Serializer serializer = new Serializer(sw);
        serializer.startFragment();
        serializer.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
        serializer.completed();
        try {
            writer.writeDTD(sw.toString());
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        try {
            writer.writeStartElement(prefix, localName, namespaceURI);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        try {
            if (prefix.length() != 0) {
                writer.writeNamespace(prefix, namespaceURI);
            } else {
                writer.writeDefaultNamespace(namespaceURI);
            }
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
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
        try {
            writer.writeAttribute(prefix, namespaceURI, localName, value);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        try {
            writer.writeAttribute(name, value);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void attributesCompleted() throws StreamException {
        // Nothing to do here
    }

    @Override
    public void endElement() throws StreamException {
        try {
            writer.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (buffering) {
            buffer.append(data);
            return;
        }
        try {
            if (data instanceof TextContent) {
                TextContent textContent = (TextContent) data;
                if (textContent.isBinary()) {
                    Object dataHandlerObject = textContent.getDataHandlerObject();
                    if (dataHandlerObject instanceof DataHandlerProvider) {
                        getDataHandlerWriter()
                                .writeDataHandler(
                                        (DataHandlerProvider) dataHandlerObject,
                                        textContent.getContentID(),
                                        textContent.isOptimize());
                    } else {
                        getDataHandlerWriter()
                                .writeDataHandler(
                                        textContent.getDataHandler(),
                                        textContent.getContentID(),
                                        textContent.isOptimize());
                    }
                    return;
                }
            }
            writer.writeCharacters(data.toString());
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startCDATASection() throws StreamException {
        buffering = true;
    }

    @Override
    public void endCDATASection() throws StreamException {
        try {
            writer.writeCData(stopBuffering());
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startComment() throws StreamException {
        buffering = true;
    }

    @Override
    public void endComment() throws StreamException {
        try {
            writer.writeComment(stopBuffering());
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        buffering = true;
        piTarget = target;
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        try {
            writer.writeProcessingInstruction(piTarget + " ", stopBuffering());
            piTarget = null;
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        try {
            writer.writeEntityRef(name);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    private DataHandlerWriter getDataHandlerWriter() {
        // We only retrieve/create the DataHandlerWriter if necessary
        if (dataHandlerWriter == null) {
            dataHandlerWriter = XMLStreamWriterUtils.getDataHandlerWriter(writer);
        }
        return dataHandlerWriter;
    }

    @Override
    public void completed() throws StreamException {
        // TODO: the original StAX serialization code newer called writeEndDocument; this is
        // probably a mistake
    }

    @Override
    public boolean drain() throws StreamException {
        return true;
    }
}
