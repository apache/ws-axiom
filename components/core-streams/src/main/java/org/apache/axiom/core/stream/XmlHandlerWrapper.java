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

public class XmlHandlerWrapper implements XmlHandler {
    private final XmlHandler parent;

    public XmlHandlerWrapper(XmlHandler parent) {
        this.parent = parent;
    }

    public final XmlHandler getParent() {
        return parent;
    }

    @Override
    public void startDocument(
            String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone)
            throws StreamException {
        parent.startDocument(inputEncoding, xmlVersion, xmlEncoding, standalone);
    }

    @Override
    public void startFragment() throws StreamException {
        parent.startFragment();
    }

    @Override
    public void processDocumentTypeDeclaration(
            String rootName, String publicId, String systemId, String internalSubset)
            throws StreamException {
        parent.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        parent.startElement(namespaceURI, localName, prefix);
    }

    @Override
    public void endElement() throws StreamException {
        parent.endElement();
    }

    @Override
    public void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified)
            throws StreamException {
        parent.processAttribute(namespaceURI, localName, prefix, value, type, specified);
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        parent.processAttribute(name, value, type, specified);
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        parent.processNamespaceDeclaration(prefix, namespaceURI);
    }

    @Override
    public void attributesCompleted() throws StreamException {
        parent.attributesCompleted();
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        parent.processCharacterData(data, ignorable);
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        parent.startProcessingInstruction(target);
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        parent.endProcessingInstruction();
    }

    @Override
    public void startComment() throws StreamException {
        parent.startComment();
    }

    @Override
    public void endComment() throws StreamException {
        parent.endComment();
    }

    @Override
    public void startCDATASection() throws StreamException {
        parent.startCDATASection();
    }

    @Override
    public void endCDATASection() throws StreamException {
        parent.endCDATASection();
    }

    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        parent.processEntityReference(name, replacementText);
    }

    @Override
    public void completed() throws StreamException {
        parent.completed();
    }

    @Override
    public boolean drain() throws StreamException {
        return parent.drain();
    }
}
