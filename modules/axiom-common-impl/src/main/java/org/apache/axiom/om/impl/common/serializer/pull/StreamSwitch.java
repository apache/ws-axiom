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
}
