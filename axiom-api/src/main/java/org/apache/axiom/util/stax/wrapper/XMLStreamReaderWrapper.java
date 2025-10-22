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

package org.apache.axiom.util.stax.wrapper;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Base class for {@link XMLStreamReader} wrappers. The class provides default implementations for
 * all methods. Each of them calls the corresponding method in the parent reader. This class is
 * similar to {@link javax.xml.stream.util.StreamReaderDelegate}, with the difference that it is
 * immutable.
 */
public class XMLStreamReaderWrapper implements XMLStreamReader {
    private final XMLStreamReader parent;

    /**
     * Constructor.
     *
     * @param parent the parent reader
     */
    public XMLStreamReaderWrapper(XMLStreamReader parent) {
        this.parent = parent;
    }

    /**
     * Get the parent stream reader. This method is declared as protected because it should only be
     * used by subclasses. However, stream reader wrappers that can safely be unwrapped may
     * implement the {@link org.apache.axiom.ext.stax.DelegatingXMLStreamReader} interface to make
     * this a public method. Note that a corresponding <code>setParent</code> method is
     * intentionally omitted because {@link XMLStreamReaderWrapper} is immutable.
     *
     * @return the parent stream reader that is wrapped by this object
     */
    protected XMLStreamReader getParent() {
        return parent;
    }

    @Override
    public void close() throws XMLStreamException {
        parent.close();
    }

    @Override
    public int getAttributeCount() {
        return parent.getAttributeCount();
    }

    @Override
    public String getAttributeLocalName(int index) {
        return parent.getAttributeLocalName(index);
    }

    @Override
    public QName getAttributeName(int index) {
        return parent.getAttributeName(index);
    }

    @Override
    public String getAttributeNamespace(int index) {
        return parent.getAttributeNamespace(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return parent.getAttributePrefix(index);
    }

    @Override
    public String getAttributeType(int index) {
        return parent.getAttributeType(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return parent.getAttributeValue(index);
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        return parent.getAttributeValue(namespaceURI, localName);
    }

    @Override
    public String getCharacterEncodingScheme() {
        return parent.getCharacterEncodingScheme();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return parent.getElementText();
    }

    @Override
    public String getEncoding() {
        return parent.getEncoding();
    }

    @Override
    public int getEventType() {
        return parent.getEventType();
    }

    @Override
    public String getLocalName() {
        return parent.getLocalName();
    }

    @Override
    public Location getLocation() {
        return parent.getLocation();
    }

    @Override
    public QName getName() {
        return parent.getName();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return parent.getNamespaceContext();
    }

    @Override
    public int getNamespaceCount() {
        return parent.getNamespaceCount();
    }

    @Override
    public String getNamespacePrefix(int index) {
        return parent.getNamespacePrefix(index);
    }

    @Override
    public String getNamespaceURI() {
        return parent.getNamespaceURI();
    }

    @Override
    public String getNamespaceURI(int index) {
        return parent.getNamespaceURI(index);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return parent.getNamespaceURI(prefix);
    }

    @Override
    public String getPIData() {
        return parent.getPIData();
    }

    @Override
    public String getPITarget() {
        return parent.getPITarget();
    }

    @Override
    public String getPrefix() {
        return parent.getPrefix();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return parent.getProperty(name);
    }

    @Override
    public String getText() {
        return parent.getText();
    }

    @Override
    public char[] getTextCharacters() {
        return parent.getTextCharacters();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        return parent.getTextCharacters(sourceStart, target, targetStart, length);
    }

    @Override
    public int getTextLength() {
        return parent.getTextLength();
    }

    @Override
    public int getTextStart() {
        return parent.getTextStart();
    }

    @Override
    public String getVersion() {
        return parent.getVersion();
    }

    @Override
    public boolean hasName() {
        return parent.hasName();
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return parent.hasNext();
    }

    @Override
    public boolean hasText() {
        return parent.hasText();
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        return parent.isAttributeSpecified(index);
    }

    @Override
    public boolean isCharacters() {
        return parent.isCharacters();
    }

    @Override
    public boolean isEndElement() {
        return parent.isEndElement();
    }

    @Override
    public boolean isStandalone() {
        return parent.isStandalone();
    }

    @Override
    public boolean isStartElement() {
        return parent.isStartElement();
    }

    @Override
    public boolean isWhiteSpace() {
        return parent.isWhiteSpace();
    }

    @Override
    public int next() throws XMLStreamException {
        return parent.next();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        return parent.nextTag();
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        parent.require(type, namespaceURI, localName);
    }

    @Override
    public boolean standaloneSet() {
        return parent.standaloneSet();
    }
}
