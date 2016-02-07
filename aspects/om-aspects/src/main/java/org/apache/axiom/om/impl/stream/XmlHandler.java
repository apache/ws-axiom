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

import org.apache.axiom.om.OMDataSource;

public interface XmlHandler {
    void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) throws StreamException;
    
    void endDocument() throws StreamException;
    
    void processDocumentTypeDeclaration(String rootName, String publicId,
            String systemId, String internalSubset) throws StreamException;

    void startElement(String namespaceURI, String localName, String prefix) throws StreamException;
    
    void endElement() throws StreamException;
    
    void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException;
    
    void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException;
    
    void attributesCompleted() throws StreamException;
    
    void processCharacterData(Object data, boolean ignorable) throws StreamException;
    
    void processProcessingInstruction(String piTarget, String piData) throws StreamException;
    
    void processComment(String content) throws StreamException;
    
    void processCDATASection(String content) throws StreamException;
    
    void processEntityReference(String name, String replacementText) throws StreamException;
    
    void processOMDataSource(String namespaceURI, String localName, OMDataSource dataSource) throws StreamException;
}
