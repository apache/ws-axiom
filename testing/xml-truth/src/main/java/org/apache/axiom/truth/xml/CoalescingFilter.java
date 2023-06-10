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
package org.apache.axiom.truth.xml;

import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.apache.axiom.truth.xml.spi.TraverserException;

final class CoalescingFilter extends Filter {
    private Event savedEvent;
    private String savedText;

    CoalescingFilter(Traverser parent) {
        super(parent);
    }

    @Override
    public Event next() throws TraverserException {
        savedText = null;
        if (savedEvent != null) {
            Event event = savedEvent;
            savedEvent = null;
            return event;
        } else {
            Event event = super.next();
            if (event == Event.TEXT || event == Event.WHITESPACE) {
                String text = super.getText();
                StringBuilder buffer = null;
                Event nextEvent;
                while ((nextEvent = super.next()) == event) {
                    if (buffer == null) {
                        buffer = new StringBuilder(text);
                    }
                    buffer.append(super.getText());
                }
                savedEvent = nextEvent;
                savedText = buffer == null ? text : buffer.toString();
            }
            return event;
        }
    }

    @Override
    public String getText() {
        return savedText != null ? savedText : super.getText();
    }
}
