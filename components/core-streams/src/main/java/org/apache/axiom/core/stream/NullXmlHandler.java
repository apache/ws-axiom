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

public final class NullXmlHandler implements XmlHandler {
    public static final NullXmlHandler INSTANCE = new NullXmlHandler();

    private NullXmlHandler() {}

    @Override
    public void startDocument(
            String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone)
            throws StreamException {}

    @Override
    public void startFragment() throws StreamException {}

    @Override
    public void processDocumentTypeDeclaration(
            String rootName, String publicId, String systemId, String internalSubset)
            throws StreamException {}

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {}

    @Override
    public void endElement() throws StreamException {}

    @Override
    public void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified)
            throws StreamException {}

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {}

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {}

    @Override
    public void attributesCompleted() throws StreamException {}

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {}

    @Override
    public void startProcessingInstruction(String target) throws StreamException {}

    @Override
    public void endProcessingInstruction() throws StreamException {}

    @Override
    public void startComment() throws StreamException {}

    @Override
    public void endComment() throws StreamException {}

    @Override
    public void startCDATASection() throws StreamException {}

    @Override
    public void endCDATASection() throws StreamException {}

    @Override
    public void processEntityReference(String name, String replacementText)
            throws StreamException {}

    @Override
    public void completed() throws StreamException {}

    @Override
    public boolean drain() throws StreamException {
        return true;
    }
}
