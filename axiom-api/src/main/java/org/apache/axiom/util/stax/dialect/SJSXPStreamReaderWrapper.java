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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class SJSXPStreamReaderWrapper extends XMLStreamReaderWrapper {
    public SJSXPStreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    @Override
    public Object getProperty(String name) {
        if (DTDReader.PROPERTY.equals(name)) {
            return new AbstractDTDReader(getParent()) {
                @Override
                protected String getDocumentTypeDeclaration(XMLStreamReader reader) {
                    return reader.getText();
                }
            };
        } else {
            return super.getProperty(name);
        }
    }

    @Override
    public String getCharacterEncodingScheme() {
        if (getEventType() == START_DOCUMENT) {
            return super.getCharacterEncodingScheme();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getEncoding() {
        if (getEventType() == START_DOCUMENT) {
            return super.getEncoding();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getVersion() {
        if (getEventType() == START_DOCUMENT) {
            return super.getVersion();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isStandalone() {
        if (getEventType() == START_DOCUMENT) {
            return super.isStandalone();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean standaloneSet() {
        if (getEventType() == START_DOCUMENT) {
            return super.standaloneSet();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getLocalName() {
        // Fix for https://sjsxp.dev.java.net/issues/show_bug.cgi?id=21
        int event = super.getEventType();
        if (event == START_ELEMENT || event == END_ELEMENT || event == ENTITY_REFERENCE) {
            return super.getLocalName();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getPrefix() {
        // Fix for:
        // - https://sjsxp.dev.java.net/issues/show_bug.cgi?id=24
        // - https://sjsxp.dev.java.net/issues/show_bug.cgi?id=54
        int event = super.getEventType();
        if (event == START_ELEMENT || event == END_ELEMENT) {
            String result = super.getPrefix();
            return result == null || result.length() == 0 ? null : result;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getNamespaceURI() {
        // Fix for https://sjsxp.dev.java.net/issues/show_bug.cgi?id=27
        int event = getEventType();
        if (event == START_ELEMENT || event == END_ELEMENT) {
            return super.getNamespaceURI();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public QName getName() {
        // Fix for https://sjsxp.dev.java.net/issues/show_bug.cgi?id=44
        try {
            return super.getName();
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean hasName() {
        // Fix for https://sjsxp.dev.java.net/issues/show_bug.cgi?id=22
        int event = super.getEventType();
        return event == START_ELEMENT || event == END_ELEMENT;
    }

    @Override
    public boolean hasText() {
        return super.hasText() || super.getEventType() == SPACE;
    }

    @Override
    public boolean isWhiteSpace() {
        return super.isWhiteSpace() || super.getEventType() == SPACE;
    }

    @Override
    public int next() throws XMLStreamException {
        // Fix for https://sjsxp.dev.java.net/issues/show_bug.cgi?id=17
        // Note that the StAX specs has contradicting information about the type
        // of exception to throw (IllegalStateException or NoSuchElementException)
        // if the end of the document has been reached. We use IllegalStateException
        // because Woodstox does.
        if (hasNext()) {
            return super.next();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return new SJSXPNamespaceContextWrapper(super.getNamespaceContext());
    }
}
