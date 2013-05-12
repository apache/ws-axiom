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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.axiom.om.OMDataSource;

final class StreamSwitch extends StreamReaderDelegate {
    /**
     * Indicates if an OMSourcedElement with an OMDataSource should
     * be considered as an interior node or a leaf node.
     */
    private boolean isDataSourceALeaf;

    OMDataSource getDataSource() {
        XMLStreamReader parent = getParent();
        // Only SwitchingWrapper can produce OMDataSource "events"
        return parent instanceof SwitchingWrapper ? ((SwitchingWrapper)getParent()).getDataSource() : null;
    }
    
    void enableDataSourceEvents(boolean value) {
        isDataSourceALeaf = true;
    }

    boolean isDataSourceALeaf() {
        return isDataSourceALeaf;
    }

    public int nextTag() throws XMLStreamException {
        // The nextTag method is tricky because the delegate may need to switch
        // to another delegate to locate the next tag. We allow the delegate to
        // return -1 in this case.
        int eventType = super.nextTag();
        if (eventType == -1) {
            eventType = next();
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
        }
        return eventType;
    }

    public String getElementText() throws XMLStreamException {
        // getElementText is tricky for the same reasons as nextTag.
        String text = super.getElementText();
        if (text != null) {
            return text;
        } else {
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
}
