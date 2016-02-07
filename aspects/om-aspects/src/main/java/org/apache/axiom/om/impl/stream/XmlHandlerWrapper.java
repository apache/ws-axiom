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
package org.apache.axiom.om.impl.stream;

public class XmlHandlerWrapper implements XmlHandler {
    private final XmlHandler parent;

    public XmlHandlerWrapper(XmlHandler parent) {
        this.parent = parent;
    }

    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            boolean standalone) throws StreamException {
        parent.startDocument(inputEncoding, xmlVersion, xmlEncoding, standalone);
    }

    public void endDocument() throws StreamException {
        parent.endDocument();
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException {
        parent.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
    }

    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        parent.startElement(namespaceURI, localName, prefix);
    }

    public void endElement() throws StreamException {
        parent.endElement();
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value,
            String type, boolean specified) throws StreamException {
        parent.processAttribute(namespaceURI, localName, prefix, value, type, specified);
    }

    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        parent.processNamespaceDeclaration(prefix, namespaceURI);
    }

    public void attributesCompleted() throws StreamException {
        parent.attributesCompleted();
    }

    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        parent.processCharacterData(data, ignorable);
    }

    public void processProcessingInstruction(String piTarget, String piData)
            throws StreamException {
        parent.processProcessingInstruction(piTarget, piData);
    }

    public void processComment(String content) throws StreamException {
        parent.processComment(content);
    }

    public void processCDATASection(String content) throws StreamException {
        parent.processCDATASection(content);
    }

    public void processEntityReference(String name, String replacementText) throws StreamException {
        parent.processEntityReference(name, replacementText);
    }
}
