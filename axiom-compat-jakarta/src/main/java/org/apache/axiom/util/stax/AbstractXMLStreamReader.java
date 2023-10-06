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

package org.apache.axiom.util.stax;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Partial implementation of the {@link XMLStreamReader} interface.
 * This class implements methods that can be easily expressed in terms of other
 * (abstract) methods or for which it makes sense to provide a default
 * implementation.
 * 
 * @deprecated This class is no longer maintained.
 */
public abstract class AbstractXMLStreamReader implements XMLStreamReader {
    @Override
    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }

    /**
     * @return Returns boolean.
     * @see javax.xml.stream.XMLStreamReader#hasText()
     */
    @Override
    public boolean hasText() {
        int event = getEventType();
        return ((event == CHARACTERS) || (event == DTD)
                || (event == CDATA)
                || (event == ENTITY_REFERENCE)
                || (event == COMMENT) || (event == SPACE));
    }

    /**
     * Returns the next tag.
     *
     * @return Returns int.
     *
     * @throws XMLStreamException
     */
    @Override
    public int nextTag() throws XMLStreamException {
        int eventType = next();
        while ((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace()) // skip whitespace
                || (eventType == XMLStreamConstants.CDATA && isWhiteSpace()) // skip whitespace
                || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT) {
            eventType = next();
        }
        if (eventType != XMLStreamConstants.START_ELEMENT &&
                eventType != XMLStreamConstants.END_ELEMENT) {
            throw new XMLStreamException("expected start or end tag", getLocation());
        }
        return eventType;
    }

    @Override
    public boolean isStartElement() {
        return getEventType() == START_ELEMENT;
    }

    @Override
    public boolean isEndElement() {
        return getEventType() == END_ELEMENT;
    }

    @Override
    public boolean isCharacters() {
        return getEventType() == CHARACTERS;
    }

    @Override
    public boolean isWhiteSpace() {
        switch (getEventType()) {
            case SPACE:
                return true;
            case CHARACTERS:
                // XMLStreamReader Javadoc says that isWhiteSpace "returns true if the cursor
                // points to a character data event that consists of all whitespace". This
                // means that this method may return true for a CHARACTER event and we need
                // to scan the text of the node.
                String text = getText();
                for (int i=0; i<text.length(); i++) {
                    char c = text.charAt(i);
                    if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean hasName() {
        int event = getEventType();
        return event == START_ELEMENT || event == END_ELEMENT;
    }

    @Override
    public void require(int type, String uri, String localName) throws XMLStreamException {
        int actualType = getEventType();

        if (type != actualType) {
            throw new XMLStreamException("Required type " + XMLEventUtils.getEventTypeString(type)
                    + ", actual type " + XMLEventUtils.getEventTypeString(actualType));
        }

        if (localName != null) {
            if (actualType != START_ELEMENT && actualType != END_ELEMENT
                && actualType != ENTITY_REFERENCE) {
                throw new XMLStreamException("Required a non-null local name, but current token " +
                		"not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " +
                		XMLEventUtils.getEventTypeString(actualType) + ")");
            }
            String actualLocalName = getLocalName();
            if (actualLocalName != localName && !actualLocalName.equals(localName)) {
                throw new XMLStreamException("Required local name '" + localName +
                        "'; current local name '" + actualLocalName + "'.");
            }
        }
        
        if (uri != null) {
            if (actualType != START_ELEMENT && actualType != END_ELEMENT) {
                throw new XMLStreamException("Required non-null namespace URI, but current token " +
                		"not a START_ELEMENT or END_ELEMENT (was " +
                		XMLEventUtils.getEventTypeString(actualType) + ")");
            }
            String actualUri = getNamespaceURI();
            if (uri.length() == 0) {
                if (actualUri != null && actualUri.length() > 0) {
                    throw new XMLStreamException("Required empty namespace, instead have '" + actualUri + "'.");
                }
            } else {
                if (!uri.equals(actualUri)) {
                    throw new XMLStreamException("Required namespace '" + uri + "'; have '" + actualUri +"'.");
                }
            }
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        ///////////////////////////////////////////////////////
        //// Code block directly from the API documentation ///
        if (getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException(
                    "parser must be on START_ELEMENT to read next text", getLocation());
        }
        int eventType = next();
        StringBuffer content = new StringBuffer();
        while (eventType != XMLStreamConstants.END_ELEMENT) {
            if (eventType == XMLStreamConstants.CHARACTERS
                    || eventType == XMLStreamConstants.CDATA
                    || eventType == XMLStreamConstants.SPACE
                    || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                content.append(getText());
            } else if (eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                    || eventType == XMLStreamConstants.COMMENT) {
                // skipping
            } else if (eventType == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException(
                        "unexpected end of document when reading element text content");
            } else if (eventType == XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException(
                        "element text content may not contain START_ELEMENT");
            } else {
                throw new XMLStreamException(
                        "Unexpected event type " + eventType, getLocation());
            }
            eventType = next();
        }
        return content.toString();
        ///////////////////////////////////////////////////////////////
    }
}
