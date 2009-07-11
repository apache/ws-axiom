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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Partial implementation of the {@link XMLStreamReader} interface.
 * This class implements methods that can be easily expressed in terms of other
 * (abstract) methods or for which it makes sense to provide a default
 * implementation.
 */
public abstract class AbstractXMLStreamReader implements XMLStreamReader {
    /**
     * @return Returns boolean.
     * @see javax.xml.stream.XMLStreamReader#hasText()
     */
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
     * @throws org.apache.axiom.om.impl.exception.OMStreamingException
     *
     * @throws XMLStreamException
     */
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

    public boolean isStartElement() {
        return getEventType() == START_ELEMENT;
    }

    public boolean isEndElement() {
        return getEventType() == END_ELEMENT;
    }

    public boolean isCharacters() {
        return getEventType() == CHARACTERS;
    }

    public boolean hasName() {
        int event = getEventType();
        // TODO: need to check the StAX specs if this is correct
        return event == START_ELEMENT || event == END_ELEMENT;
    }

    /**
     * @param i
     * @param s
     * @param s1
     * @throws XMLStreamException
     * @see javax.xml.stream.XMLStreamReader#require(int, String, String)
     */
    // TODO: this comes from OMStAXWrapper and needs to be implemented properly
    public void require(int i, String s, String s1) throws XMLStreamException {
        throw new XMLStreamException();
    }
}
