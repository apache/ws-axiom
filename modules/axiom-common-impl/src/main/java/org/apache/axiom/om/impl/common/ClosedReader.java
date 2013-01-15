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
package org.apache.axiom.om.impl.common;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

final class ClosedReader implements XMLStreamReader {
    static final ClosedReader INSTANCE = new ClosedReader();
    
    private ClosedReader() {}

    public void close() throws XMLStreamException {
        // no-op
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return null;
    }

    public int getAttributeCount() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getAttributeLocalName(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public QName getAttributeName(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getAttributeNamespace(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getAttributePrefix(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getAttributeType(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getAttributeValue(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getCharacterEncodingScheme() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getElementText() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public String getEncoding() {
        throw new IllegalStateException("Reader already closed");
    }

    public int getEventType() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getLocalName() {
        throw new IllegalStateException("Reader already closed");
    }

    public Location getLocation() {
        throw new IllegalStateException("Reader already closed");
    }

    public QName getName() {
        throw new IllegalStateException("Reader already closed");
    }

    public NamespaceContext getNamespaceContext() {
        throw new IllegalStateException("Reader already closed");
    }

    public int getNamespaceCount() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getNamespacePrefix(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getNamespaceURI() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getNamespaceURI(String prefix) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getNamespaceURI(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public String getPIData() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getPITarget() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getPrefix() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getText() {
        throw new IllegalStateException("Reader already closed");
    }

    public char[] getTextCharacters() {
        throw new IllegalStateException("Reader already closed");
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public int getTextLength() {
        throw new IllegalStateException("Reader already closed");
    }

    public int getTextStart() {
        throw new IllegalStateException("Reader already closed");
    }

    public String getVersion() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean hasName() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean hasNext() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean hasText() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean isAttributeSpecified(int index) {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean isCharacters() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean isEndElement() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean isStandalone() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean isStartElement() {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean isWhiteSpace() {
        throw new IllegalStateException("Reader already closed");
    }

    public int next() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public int nextTag() throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        throw new IllegalStateException("Reader already closed");
    }

    public boolean standaloneSet() {
        throw new IllegalStateException("Reader already closed");
    }
}
