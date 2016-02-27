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

public interface XmlHandler {
    void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) throws StreamException;
    
    void endDocument() throws StreamException;
    
    void processDocumentTypeDeclaration(String rootName, String publicId,
            String systemId, String internalSubset) throws StreamException;

    /**
     * Prepare to write an element start tag. A call to this method will be followed by zero or more
     * calls to {@link #processNamespaceDeclaration(String, String)} and
     * {@link #processAttribute(String, String, String, String, String)} and a single call to
     * {@link #attributesCompleted()}.
     * 
     * @param namespaceURI
     *            the namespace URI of the element; never <code>null</code>
     * @param localName
     *            the local name of the element; never <code>null</code>
     * @param prefix
     *            the prefix of the element; never <code>null</code>
     * @throws StreamException
     */
    void startElement(String namespaceURI, String localName, String prefix) throws StreamException;
    
    void endElement() throws StreamException;
    
    /**
     * Add the given attribute to the element.
     * 
     * @param namespaceURI
     *            the namespace URI or the attribute; never <code>null</code>
     * @param localName
     *            the local name of the attribute; never <code>null</code>
     * @param prefix
     *            the namespace prefix of the attribute; never <code>null</code>
     * @param value
     *            the value of the attribute; never <code>null</code>
     * @param type
     *            the attribute type (e.g. <tt>CDATA</tt>); never <code>null</code>
     * @throws StreamException
     */
    void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException;
    
    /**
     * Add the given namespace declaration to the element.
     * 
     * @param prefix
     *            the namespace prefix; never <code>null</code>
     * @param namespaceURI
     *            the namespace URI; never <code>null</code>
     * @throws StreamException
     */
    void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException;
    
    void attributesCompleted() throws StreamException;
    
    void processCharacterData(Object data, boolean ignorable) throws StreamException;
    
    void processProcessingInstruction(String piTarget, String piData) throws StreamException;
    
    void processComment(String content) throws StreamException;
    
    /**
     * Notify the handler of the beginning of a CDATA section.
     * 
     * @throws StreamException
     *             if an error occurs when processing the event
     */
    void startCDATASection() throws StreamException;
    
    /**
     * Notify the handler of the end of a CDATA section.
     * 
     * @throws StreamException
     *             if an error occurs when processing the event
     */
    void endCDATASection() throws StreamException;
    
    void processEntityReference(String name, String replacementText) throws StreamException;
}
