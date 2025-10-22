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

import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.codehaus.stax2.XMLStreamReader2;

class Woodstox4StreamReaderWrapper extends StAX2StreamReaderWrapper implements CharacterDataReader {
    public Woodstox4StreamReaderWrapper(XMLStreamReader reader) {
        super(reader);
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
    public boolean isCharacters() {
        // TODO: in the dialect detection we should take into account the Woodstox version,
        //       so that we can avoid creating the wrapper for Woodstox versions where this
        //       issue has been fixed
        // This addresses WSTX-201:
        return getEventType() == CHARACTERS;
    }

    @Override
    public String getNamespaceURI() {
        // Woodstox 4.0 may return "" instead of null
        String uri = super.getNamespaceURI();
        return uri == null || uri.length() == 0 ? null : uri;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        // Woodstox 4.0 may return "" instead of null
        String uri = super.getNamespaceURI(prefix);
        return uri == null || uri.length() == 0 ? null : uri;
    }

    @Override
    public String getNamespacePrefix(int index) {
        // Woodstox 4.0 may return "" instead of null
        String prefix = super.getNamespacePrefix(index);
        return prefix == null || prefix.length() == 0 ? null : prefix;
    }

    @Override
    public String getAttributeNamespace(int index) {
        // Woodstox 4.0 may return "" instead of null
        String uri = super.getAttributeNamespace(index);
        return uri == null || uri.length() == 0 ? null : uri;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (CharacterDataReader.PROPERTY.equals(name)) {
            return this;
        } else {
            return super.getProperty(name);
        }
    }

    @Override
    public void writeTextTo(Writer writer) throws XMLStreamException, IOException {
        // Allow efficient access to character data, even if coalescing is enabled
        ((XMLStreamReader2) getParent()).getText(writer, false);
    }
}
