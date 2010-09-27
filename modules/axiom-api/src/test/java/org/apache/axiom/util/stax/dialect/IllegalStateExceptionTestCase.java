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
package org.apache.axiom.util.stax.dialect;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.util.stax.XMLEventUtils;

public abstract class IllegalStateExceptionTestCase extends DialectTestCase {
    private final int event;
    private final boolean expectException;
    
    public IllegalStateExceptionTestCase(int event, boolean expectException) {
        this.event = event;
        this.expectException = expectException;
        setName(getClass().getName() + " [" + XMLEventUtils.getEventTypeString(event) + "]");
    }

    protected final void runTest() throws Throwable {
        XMLInputFactory factory = getDialect().enableCDataReporting(newNormalizedXMLInputFactory());
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        InputStream in = IllegalStateExceptionTestCase.class.getResourceAsStream("alleventtypes.xml");
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            while (true) {
                if (reader.getEventType() == event) {
                    break;
                } else if (reader.hasNext()) {
                    reader.next();
                } else {
                    fail("Internal error: didn't encounter event " + event);
                }
            }
            boolean exception;
            try {
                invoke(reader);
                exception = false;
            } catch (IllegalStateException ex) {
                exception = true;
            }
            if (exception && !expectException) {
                fail("Didn't expect IllegalStateException");
            } else if (!exception && expectException) {
                fail("Expected IllegalStateException");
            }
        } finally {
            in.close();
        }
    }
    
    protected abstract void invoke(XMLStreamReader reader) throws XMLStreamException;
}
