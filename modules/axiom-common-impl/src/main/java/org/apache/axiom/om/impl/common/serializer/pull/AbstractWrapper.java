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
package org.apache.axiom.om.impl.common.serializer.pull;

import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

/**
 * Base class for {@link PullSerializerState} implementations that wrap an {@link XMLStreamReader}.
 */
abstract class AbstractWrapper extends PullSerializerState implements CharacterDataReader {
    protected final XMLStreamReader reader;
    private final PullSerializer serializer;
    private int depth;
    
    /**
     * The {@link DTDReader} extension of the reader, or <code>null</code> if the extension has not
     * yet been retrieved.
     */
    private DTDReader dtdReader;
    
    /**
     * The {@link DataHandlerReader} extension of the reader, or <code>null</code> if the extension
     * has not yet been retrieved.
     */
    private DataHandlerReader dataHandlerReader;

    /**
     * The {@link CharacterDataReader} extension of the reader, or <code>null</code> if the extension
     * has not yet been retrieved.
     */
    private CharacterDataReader characterDataReader;

    AbstractWrapper(PullSerializer serializer, XMLStreamReader reader, int startDepth) {
        this.reader = reader;
        this.serializer = serializer;
        depth = startDepth;
        // Adjust the depth if we start with an END_ELEMENT event
        if (reader.getEventType() == XMLStreamReader.END_ELEMENT) {
            depth--;
        }
    }

    final DTDReader getDTDReader() {
        if (dtdReader == null) {
            try {
                dtdReader = (DTDReader)reader.getProperty(DTDReader.PROPERTY);
            } catch (IllegalArgumentException ex) {
                // Continue
            }
            if (dtdReader == null) {
                dtdReader = NullDTDReader.INSTANCE;
            }
        }
        return dtdReader;
    }

    final DataHandlerReader getDataHandlerReader() {
        if (dataHandlerReader == null) {
            dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
            if (dataHandlerReader == null) {
                dataHandlerReader = NullDataHandlerReader.INSTANCE;
            }
        }
        return dataHandlerReader;
    }

    final CharacterDataReader getCharacterDataReader() {
        if (characterDataReader == null) {
            try {
                characterDataReader = (CharacterDataReader)reader.getProperty(CharacterDataReader.PROPERTY);
            } catch (IllegalArgumentException ex) {
                // Continue
            }
            if (characterDataReader == null) {
                characterDataReader = this;
            }
        }
        return characterDataReader;
    }

    public final void writeTextTo(Writer writer) throws XMLStreamException, IOException {
        writer.write(reader.getText());
    }

    final int getEventType() {
        return reader.getEventType();
    }

    final boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    final void next() throws XMLStreamException {
        if (!doNext()) {
            // We get here if the underlying XMLStreamReader is on the last END_ELEMENT event
            serializer.popState();
        }
    }
    
    final boolean doNext() throws XMLStreamException {
        if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
            depth++;
        }
        if (depth == 0) {
            return false;
        } else {
            if (reader.next() == XMLStreamReader.END_ELEMENT) {
                depth--;
            }
            return true;
        }
    }
    
    final int nextTag() throws XMLStreamException {
        // TODO: need to handle depth == 0 case here!
        int result = reader.nextTag();
        switch (result) {
            case XMLStreamReader.START_ELEMENT:
                depth++;
                break;
            case XMLStreamReader.END_ELEMENT:
                depth--;
        }
        return result;
    }

    final Object getProperty(String name) throws IllegalArgumentException {
        return reader.getProperty(name);
    }

    final String getVersion() {
        return reader.getVersion();
    }

    final String getCharacterEncodingScheme() {
        return reader.getCharacterEncodingScheme();
    }

    final String getEncoding() {
        return reader.getEncoding();
    }

    final boolean isStandalone() {
        return reader.isStandalone();
    }

    final boolean standaloneSet() {
        return reader.standaloneSet();
    }

    final String getPrefix() {
        return reader.getPrefix();
    }

    final String getNamespaceURI() {
        return reader.getNamespaceURI();
    }

    final String getLocalName() {
        return reader.getLocalName();
    }

    final QName getName() {
        return reader.getName();
    }

    final int getNamespaceCount() {
        return reader.getNamespaceCount();
    }

    final String getNamespacePrefix(int index) {
        return reader.getNamespacePrefix(index);
    }

    final String getNamespaceURI(int index) {
        return reader.getNamespaceURI(index);
    }

    final int getAttributeCount() {
        return reader.getAttributeCount();
    }

    final String getAttributePrefix(int index) {
        return reader.getAttributePrefix(index);
    }

    final String getAttributeNamespace(int index) {
        return reader.getAttributeNamespace(index);
    }

    final String getAttributeLocalName(int index) {
        return reader.getAttributeLocalName(index);
    }

    final QName getAttributeName(int index) {
        return reader.getAttributeName(index);
    }

    final boolean isAttributeSpecified(int index) {
        return reader.isAttributeSpecified(index);
    }

    final String getAttributeType(int index) {
        return reader.getAttributeType(index);
    }

    final String getAttributeValue(int index) {
        return reader.getAttributeValue(index);
    }

    final String getAttributeValue(String namespaceURI, String localName) {
        return reader.getAttributeValue(namespaceURI, localName);
    }

    final NamespaceContext getNamespaceContext() {
        return reader.getNamespaceContext();
    }

    final String getNamespaceURI(String prefix) {
        return reader.getNamespaceURI(prefix);
    }

    final String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    final String getText() {
        return reader.getText();
    }

    final char[] getTextCharacters() {
        return reader.getTextCharacters();
    }

    final int getTextStart() {
        return reader.getTextStart();
    }

    final int getTextLength() {
        return reader.getTextLength();
    }

    final int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        return reader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    final Boolean isWhiteSpace() {
        return Boolean.valueOf(reader.isWhiteSpace());
    }

    final String getPIData() {
        return reader.getPIData();
    }

    final String getPITarget() {
        return reader.getPITarget();
    }

    final OMDataSource getDataSource() {
        return null;
    }

    final void restored() {
    }
    
    public String toString() {
        return super.toString() + "[reader=" + reader + "]";
    }
}
