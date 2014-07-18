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

import java.util.Collections;
import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;

final class EndDocumentState extends PullSerializerState {
    static final EndDocumentState INSTANCE = new EndDocumentState();
    
    private EndDocumentState() {}

    DTDReader getDTDReader() {
        return NullDTDReader.INSTANCE;
    }

    DataHandlerReader getDataHandlerReader() {
        return NullDataHandlerReader.INSTANCE;
    }

    CharacterDataReader getCharacterDataReader() {
        return NullCharacterDataReader.INSTANCE;
    }

    int getEventType() {
        return XMLStreamReader.END_DOCUMENT;
    }

    boolean hasNext() throws XMLStreamException {
        return false;
    }

    void next() throws XMLStreamException {
        throw new NoSuchElementException("End of the document reached");
    }

    int nextTag() throws XMLStreamException {
        throw new IllegalStateException();
    }

    Object getProperty(String name) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    String getCharacterEncodingScheme() {
        throw new IllegalStateException();
    }

    String getEncoding() {
        throw new IllegalStateException();
    }

    boolean isStandalone() {
        throw new IllegalStateException();
    }

    boolean standaloneSet() {
        throw new IllegalStateException();
    }

    String getPrefix() {
        throw new IllegalStateException();
    }

    String getNamespaceURI() {
        throw new IllegalStateException();
    }

    String getLocalName() {
        throw new IllegalStateException();
    }

    QName getName() {
        throw new IllegalStateException();
    }

    int getNamespaceCount() {
        throw new IllegalStateException();
    }

    String getNamespacePrefix(int index) {
        throw new IllegalStateException();
    }

    String getNamespaceURI(int index) {
        throw new IllegalStateException();
    }

    int getAttributeCount() {
        throw new IllegalStateException();
    }

    String getAttributePrefix(int index) {
        throw new IllegalStateException();
    }

    String getAttributeNamespace(int index) {
        throw new IllegalStateException();
    }

    String getAttributeLocalName(int index) {
        throw new IllegalStateException();
    }

    QName getAttributeName(int index) {
        throw new IllegalStateException();
    }

    boolean isAttributeSpecified(int index) {
        throw new IllegalStateException();
    }

    String getAttributeType(int index) {
        throw new IllegalStateException();
    }

    String getAttributeValue(int index) {
        throw new IllegalStateException();
    }

    String getAttributeValue(String namespaceURI, String localName) {
        throw new IllegalStateException();
    }

    NamespaceContext getNamespaceContext() {
        return new MapBasedNamespaceContext(Collections.EMPTY_MAP);
    }

    String getNamespaceURI(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    String getElementText() throws XMLStreamException {
        throw new IllegalStateException();
    }

    String getText() {
        throw new IllegalStateException();
    }

    char[] getTextCharacters() {
        throw new IllegalStateException();
    }

    int getTextStart() {
        throw new IllegalStateException();
    }

    int getTextLength() {
        throw new IllegalStateException();
    }

    int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        throw new IllegalStateException();
    }

    Boolean isWhiteSpace() {
        return Boolean.FALSE;
    }

    String getPIData() {
        throw new IllegalStateException();
    }

    String getPITarget() {
        throw new IllegalStateException();
    }

    OMDataSource getDataSource() {
        return null;
    }

    void released() throws XMLStreamException {
    }

    void restored() throws XMLStreamException {
    }
}
