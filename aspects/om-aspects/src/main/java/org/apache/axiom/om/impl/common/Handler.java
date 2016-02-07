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
package org.apache.axiom.om.impl.common;

public interface Handler {
    void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone);
    
    void endDocument();
    
    void createDocumentTypeDeclaration(String rootName, String publicId,
            String systemId, String internalSubset);

    void startElement(String namespaceURI, String localName, String prefix);
    
    void endElement();
    
    void createAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified);
    
    void createNamespaceDeclaration(String prefix, String namespaceURI);
    
    void attributesCompleted();
    
    void processCharacterData(Object data, boolean ignorable);
    
    void createProcessingInstruction(String piTarget, String piData);
    
    void createComment(String content);
    
    void createCDATASection(String content);
    
    void createEntityReference(String name, String replacementText);
}
