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
package org.apache.axiom.core.stream;

public final class DocumentElementExtractingFilterHandler extends XmlHandlerWrapper {
    private int depth;

    public DocumentElementExtractingFilterHandler(XmlHandler parent) {
        super(parent);
    }

    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) throws StreamException {
    }

    @Override
    public void endDocument() throws StreamException {
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        depth++;
    }

    @Override
    public void endElement() throws StreamException {
        super.endElement();
        depth--;
    }

    @Override
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (depth > 0) {
            super.processCharacterData(data, ignorable);
        }
    }

    @Override
    public void processProcessingInstruction(String piTarget, String piData) throws StreamException {
        if (depth > 0) {
            super.processProcessingInstruction(piTarget, piData);
        }
    }

    @Override
    public void processComment(String content) throws StreamException {
        if (depth > 0) {
            super.processComment(content);
        }
    }
}
