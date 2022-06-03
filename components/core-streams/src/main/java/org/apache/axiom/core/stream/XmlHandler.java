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
    void startDocument(
            String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone)
            throws StreamException;

    /**
     * Notify the handler of the beginning of a fragment.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void startFragment() throws StreamException;

    void processDocumentTypeDeclaration(
            String rootName, String publicId, String systemId, String internalSubset)
            throws StreamException;

    /**
     * Prepare to write an element start tag. A call to this method will be followed by zero or more
     * calls to {@link #processNamespaceDeclaration(String, String)} and {@link
     * #processAttribute(String, String, String, String, String)} and a single call to {@link
     * #attributesCompleted()}.
     *
     * @param namespaceURI the namespace URI of the element; never <code>null</code>
     * @param localName the local name of the element; never <code>null</code>
     * @param prefix the prefix of the element; never <code>null</code>
     * @throws StreamException
     */
    void startElement(String namespaceURI, String localName, String prefix) throws StreamException;

    void endElement() throws StreamException;

    /**
     * Add the given namespace aware attribute to the element.
     *
     * @param namespaceURI the namespace URI of the attribute; never <code>null</code>
     * @param localName the local name of the attribute; never <code>null</code>
     * @param prefix the namespace prefix of the attribute; never <code>null</code>
     * @param value the value of the attribute; never <code>null</code>
     * @param type the attribute type (e.g. {@code CDATA}); never <code>null</code>
     * @throws StreamException
     */
    void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified)
            throws StreamException;

    /**
     * Add the given namespece unaware attribute to the element.
     *
     * @param name the name of the attribute; never <code>null</code>
     * @param value the value of the attribute; never <code>null</code>
     * @param type the attribute type (e.g. {@code CDATA}); never <code>null</code>
     * @throws StreamException
     */
    void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException;

    /**
     * Add the given namespace declaration to the element.
     *
     * @param prefix the namespace prefix; never <code>null</code>
     * @param namespaceURI the namespace URI; never <code>null</code>
     * @throws StreamException
     */
    void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException;

    void attributesCompleted() throws StreamException;

    void processCharacterData(Object data, boolean ignorable) throws StreamException;

    /**
     * Notify the handler of the beginning of a processing instruction.
     *
     * @param target the target of the processing instruction
     * @throws StreamException if an error occurs when processing the event
     */
    void startProcessingInstruction(String target) throws StreamException;

    /**
     * Notify the handler of the end of a processing instruction.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void endProcessingInstruction() throws StreamException;

    /**
     * Notify the handler of the beginning of a comment.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void startComment() throws StreamException;

    /**
     * Notify the handler of the end of a comment.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void endComment() throws StreamException;

    /**
     * Notify the handler of the beginning of a CDATA section.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void startCDATASection() throws StreamException;

    /**
     * Notify the handler of the end of a CDATA section.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void endCDATASection() throws StreamException;

    void processEntityReference(String name, String replacementText) throws StreamException;

    /**
     * Notify the handler that the document or fragment is complete.
     *
     * @throws StreamException if an error occurs when processing the event
     */
    void completed() throws StreamException;

    /**
     * Drain the pipeline by pushing pending events to the next handler in the chain.
     *
     * @return {@code true} if the pipeline was already drained and no new events were pushed,
     *     {@code false} if at least one event was pushed and this method should be called again to
     *     continue draining the pipeline
     * @throws StreamException
     */
    boolean drain() throws StreamException;
}
