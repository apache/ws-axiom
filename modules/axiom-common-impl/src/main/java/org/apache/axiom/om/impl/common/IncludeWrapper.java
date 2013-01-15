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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

// TODO: we may have an issue with the depth calculation when nextTag is called
// TODO: what about the close() method?
final class IncludeWrapper extends XMLStreamReaderWrapper {
    private final StreamSwitch streamSwitch;
    private final XMLStreamReader nextTarget;
    private int depth = 1;
    
    public IncludeWrapper(StreamSwitch streamSwitch,
            XMLStreamReader nextTarget, XMLStreamReader parent) {
        super(parent);
        this.streamSwitch = streamSwitch;
        this.nextTarget = nextTarget;
    }

    public int next() throws XMLStreamException {
        if (depth == 0) {
            // We get here if the underlying XMLStreamReader is on the last END_ELEMENT event
            // TODO: this needs testing! the unit test should validate that the reader is closed
            getParent().close();
            streamSwitch.setParent(nextTarget);
            return nextTarget.next();
        } else {
            int event = super.next();
            switch (event) {
                case START_ELEMENT:
                    depth++;
                    break;
                case END_ELEMENT:
                    depth--;
                    break;
            }
            return event;
        }
    }
}
