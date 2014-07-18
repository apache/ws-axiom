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

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMDataSource;

final class ClosedState extends PullSerializerState {
    static final ClosedState INSTANCE = new ClosedState();
    
    private ClosedState() {}

    DTDReader getDTDReader() {
        return NullDTDReader.INSTANCE;
    }

    DataHandlerReader getDataHandlerReader() {
        return NullDataHandlerReader.INSTANCE;
    }

    CharacterDataReader getCharacterDataReader() {
        return NullCharacterDataReader.INSTANCE;
    }

    Object getProperty(String name) throws IllegalArgumentException {
        return null;
    }

    int getAttributeCount() {
        throw new IllegalStateException("Reader already closed");
    }

    String getAttributeLocalName(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    QName getAttributeName(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getAttributeNamespace(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getAttributePrefix(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getAttributeType(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getAttributeValue(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getAttributeValue(String namespaceURI, String localName) {
        throw new IllegalStateException("Reader already closed");
    }

    String getCharacterEncodingScheme() {
        throw new IllegalStateException("Reader already closed");
    }

    String getElementText() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    String getEncoding() {
        throw new IllegalStateException("Reader already closed");
    }

    int getEventType() {
        throw new IllegalStateException("Reader already closed");
    }

    String getLocalName() {
        throw new IllegalStateException("Reader already closed");
    }

    QName getName() {
        throw new IllegalStateException("Reader already closed");
    }

    NamespaceContext getNamespaceContext() {
        throw new IllegalStateException("Reader already closed");
    }

    int getNamespaceCount() {
        throw new IllegalStateException("Reader already closed");
    }

    String getNamespacePrefix(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getNamespaceURI() {
        throw new IllegalStateException("Reader already closed");
    }

    String getNamespaceURI(String prefix) {
        throw new IllegalStateException("Reader already closed");
    }

    String getNamespaceURI(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    String getPIData() {
        throw new IllegalStateException("Reader already closed");
    }

    String getPITarget() {
        throw new IllegalStateException("Reader already closed");
    }

    String getPrefix() {
        throw new IllegalStateException("Reader already closed");
    }

    String getText() {
        throw new IllegalStateException("Reader already closed");
    }

    char[] getTextCharacters() {
        throw new IllegalStateException("Reader already closed");
    }

    int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    int getTextLength() {
        throw new IllegalStateException("Reader already closed");
    }

    int getTextStart() {
        throw new IllegalStateException("Reader already closed");
    }

    String getVersion() {
        throw new IllegalStateException("Reader already closed");
    }

    boolean hasNext() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    boolean isAttributeSpecified(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    boolean isStandalone() {
        throw new IllegalStateException("Reader already closed");
    }

    Boolean isWhiteSpace() {
        throw new IllegalStateException("Reader already closed");
    }

    void next() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    int nextTag() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean standaloneSet() {
        throw new IllegalStateException("Reader already closed");
    }

    OMDataSource getDataSource() {
        throw new IllegalStateException("Reader already closed");
    }

    void released() throws XMLStreamException {
    }

    void restored() {
    }
}
