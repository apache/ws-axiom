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
package org.apache.abdera.parser.stax;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;

/**
 * Transforms a document into a fragment by unwrapping the root element.
 */
final class ElementUnwrapperFilterHandler extends XmlHandlerWrapper {
    private int depth;

    public ElementUnwrapperFilterHandler(XmlHandler parent) {
        super(parent);
    }

    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            Boolean standalone) throws StreamException {
        super.startFragment();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        if (depth > 0) {
            super.startElement(namespaceURI, localName, prefix);
        }
    }

    @Override
    public void processAttribute(String namespaceURI, String localName, String prefix, String value,
            String type, boolean specified) throws StreamException {
        if (depth > 0) {
            super.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        }
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        if (depth > 0) {
            super.processAttribute(name, value, type, specified);
        }
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        if (depth > 0) {
            super.processNamespaceDeclaration(prefix, namespaceURI);
        }
    }

    @Override
    public void attributesCompleted() throws StreamException {
        if (depth > 0) {
            super.attributesCompleted();
        }
        depth++;
    }

    @Override
    public void endElement() throws StreamException {
        if (--depth > 0) {
            super.endElement();
        }
    }
}
