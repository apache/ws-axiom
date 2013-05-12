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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

//TODO: what about the close() method?
abstract class AbstractWrapper extends XMLStreamReaderWrapper {
    private final StreamSwitch streamSwitch;
    private final XMLStreamReader nextTarget;
    private int depth;

    AbstractWrapper(StreamSwitch streamSwitch, XMLStreamReader nextTarget,
            XMLStreamReader parent, int startDepth) {
        super(parent);
        this.streamSwitch = streamSwitch;
        this.nextTarget = nextTarget;
        depth = startDepth;
    }

    public int next() throws XMLStreamException {
        if (depth == 0) {
            // We get here if the underlying XMLStreamReader is on the last END_ELEMENT event
            // TODO: also do this if the reader is prematurely closed
            release();
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
    
    public int nextTag() throws XMLStreamException {
        // TODO: need to handle depth == 0 case here!
        int result = super.nextTag();
        switch (result) {
            case START_ELEMENT:
                depth++;
                break;
            case END_ELEMENT:
                depth--;
        }
        return result;
    }

    abstract void release() throws XMLStreamException;
}
