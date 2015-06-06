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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.DTDInfo;

import com.google.common.base.Strings;

final class StAXTraverser implements Traverser {
    private final XMLStreamReader reader;

    StAXTraverser(XMLStreamReader reader) {
        this.reader = reader;
    }
    
    @Override
    public Event next() throws TraverserException {
        try {
            if (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.DTD:
                        return Event.DOCUMENT_TYPE;
                    case XMLStreamReader.START_ELEMENT:
                        return Event.START_ELEMENT;
                    case XMLStreamReader.END_ELEMENT:
                        return Event.END_ELEMENT;
                    case XMLStreamReader.CHARACTERS:
                        return Event.TEXT;
                    case XMLStreamReader.SPACE:
                        return Event.WHITESPACE;
                    case XMLStreamReader.ENTITY_REFERENCE:
                        return Event.ENTITY_REFERENCE;
                    case XMLStreamReader.COMMENT:
                        return Event.COMMENT;
                    case XMLStreamReader.CDATA:
                        return Event.CDATA_SECTION;
                    case XMLStreamReader.PROCESSING_INSTRUCTION:
                        return Event.PROCESSING_INSTRUCTION;
                    case XMLStreamReader.END_DOCUMENT:
                        return null;
                    default:
                        throw new IllegalStateException();
                }
            } else {
                return null;
            }
        } catch (XMLStreamException ex) {
            throw new TraverserException(ex);
        }
    }

    @Override
    public String getRootName() {
        return ((DTDInfo)reader).getDTDRootName();
    }

    @Override
    public String getPublicId() {
        return ((DTDInfo)reader).getDTDPublicId();
    }

    @Override
    public String getSystemId() {
        return ((DTDInfo)reader).getDTDSystemId();
    }

    @Override
    public QName getQName() {
        return reader.getName();
    }

    @Override
    public Map<QName,String> getAttributes() {
        int attributeCount = reader.getAttributeCount();
        if (attributeCount == 0) {
            return null;
        } else {
            Map<QName,String> attributes = new HashMap<QName,String>();
            for (int i=0; i<attributeCount; i++) {
                attributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
            return attributes;
        }
    }

    @Override
    public Map<String,String> getNamespaces() {
        int namespaceCount = reader.getNamespaceCount();
        if (namespaceCount == 0) {
            return null;
        } else {
            Map<String,String> namespaces = new HashMap<String,String>();
            for (int i=0; i<namespaceCount; i++) {
                namespaces.put(Strings.nullToEmpty(reader.getNamespacePrefix(i)), reader.getNamespaceURI(i));
            }
            return namespaces;
        }
    }

    @Override
    public String getText() {
        return reader.getText();
    }

    @Override
    public String getEntityName() {
        return reader.getLocalName();
    }

    @Override
    public String getPITarget() {
        return reader.getPITarget();
    }

    @Override
    public String getPIData() {
        return reader.getPIData();
    }
}
