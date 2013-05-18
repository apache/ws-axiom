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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

/**
 * Base class for {@link PullSerializerState} implementations that wrap an {@link XMLStreamReader}.
 */
//TODO: what about the close() method?
// TODO: make methods final
abstract class AbstractWrapper extends PullSerializerState {
    protected final XMLStreamReader reader;
    private final PullSerializer serializer;
    private final PullSerializerState nextState;
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

    AbstractWrapper(PullSerializer streamSwitch, PullSerializerState nextState,
            XMLStreamReader reader, int startDepth) {
        this.reader = reader;
        this.serializer = streamSwitch;
        this.nextState = nextState;
        depth = startDepth;
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

    int getEventType() {
        return reader.getEventType();
    }

    boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    int next() throws XMLStreamException {
        if (depth == 0) {
            // We get here if the underlying XMLStreamReader is on the last END_ELEMENT event
            // TODO: also do this if the reader is prematurely closed
            release();
            serializer.switchState(nextState);
            return nextState.next();
        } else {
            int event = reader.next();
            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    depth++;
                    break;
                case XMLStreamReader.END_ELEMENT:
                    depth--;
                    break;
            }
            return event;
        }
    }
    
    int nextTag() throws XMLStreamException {
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

    void close() throws XMLStreamException {
        reader.close();
    }

    Object getProperty(String name) throws IllegalArgumentException {
        return reader.getProperty(name);
    }

    String getVersion() {
        return reader.getVersion();
    }

    String getCharacterEncodingScheme() {
        return reader.getCharacterEncodingScheme();
    }

    String getEncoding() {
        return reader.getEncoding();
    }

    boolean isStandalone() {
        return reader.isStandalone();
    }

    boolean standaloneSet() {
        return reader.standaloneSet();
    }

    String getPrefix() {
        return reader.getPrefix();
    }

    String getNamespaceURI() {
        return reader.getNamespaceURI();
    }

    String getLocalName() {
        return reader.getLocalName();
    }

    QName getName() {
        return reader.getName();
    }

    int getNamespaceCount() {
        return reader.getNamespaceCount();
    }

    String getNamespacePrefix(int index) {
        return reader.getNamespacePrefix(index);
    }

    String getNamespaceURI(int index) {
        return reader.getNamespaceURI(index);
    }

    int getAttributeCount() {
        return reader.getAttributeCount();
    }

    String getAttributePrefix(int index) {
        return reader.getAttributePrefix(index);
    }

    String getAttributeNamespace(int index) {
        return reader.getAttributeNamespace(index);
    }

    String getAttributeLocalName(int index) {
        return reader.getAttributeLocalName(index);
    }

    QName getAttributeName(int index) {
        return reader.getAttributeName(index);
    }

    boolean isAttributeSpecified(int index) {
        return reader.isAttributeSpecified(index);
    }

    String getAttributeType(int index) {
        return reader.getAttributeType(index);
    }

    String getAttributeValue(int index) {
        return reader.getAttributeValue(index);
    }

    String getAttributeValue(String namespaceURI, String localName) {
        return reader.getAttributeValue(namespaceURI, localName);
    }

    NamespaceContext getNamespaceContext() {
        return reader.getNamespaceContext();
    }

    String getNamespaceURI(String prefix) {
        return reader.getNamespaceURI(prefix);
    }

    String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    String getText() {
        return reader.getText();
    }

    char[] getTextCharacters() {
        return reader.getTextCharacters();
    }

    int getTextStart() {
        return reader.getTextStart();
    }

    int getTextLength() {
        return reader.getTextLength();
    }

    int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        return reader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    Boolean isWhiteSpace() {
        return Boolean.valueOf(reader.isWhiteSpace());
    }

    String getPIData() {
        return reader.getPIData();
    }

    String getPITarget() {
        return reader.getPITarget();
    }

    OMDataSource getDataSource() {
        return null;
    }

    abstract void release() throws XMLStreamException;
}
