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

package org.apache.axiom.om;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.ext.stax.BlobWriter;

/**
 * Information item that can be serialized (written to an XML stream writer) and deserialized
 * (retrieved from an XML parser) as a unit. This is the common super-interface for {@link
 * OMDocument} and {@link OMNode}. Note that {@link OMAttribute} and {@link OMNamespace} are
 * information items that don't match the definition of this interface because they can only be read
 * from the parser as part of a larger unit, namely an element.
 *
 * <p>In accordance with the definition given above, this interface declares two sets of methods:
 *
 * <ul>
 *   <li>Methods allowing to control whether the information item has been completely built, i.e.
 *       whether all events corresponding to the information item have been retrieved from the
 *       parser.
 *   <li>Methods to write the StAX events corresponding to the information item to an {@link
 *       XMLStreamWriter}.
 * </ul>
 */
public interface OMSerializable extends OMInformationItem {
    /**
     * Indicates whether parser has parsed this information item completely or not. If some info are
     * not available in the item, one has to check this attribute to make sure that, this item has
     * been parsed completely or not.
     *
     * @return Returns boolean.
     */
    boolean isComplete();

    /** Builds itself. */
    void build();

    /**
     * If a builder and parser is associated with the node, it is closed.
     *
     * @param build if true, the object is built first before closing the builder/parser
     */
    void close(boolean build);

    /**
     * Serializes the information item with caching. This method has the same effect as {@link
     * #serialize(XMLStreamWriter, boolean)} with <code>cache</code> set to <code>true</code>.
     *
     * @param xmlWriter
     * @throws XMLStreamException
     */
    void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException;

    /**
     * Serializes the information item without caching. This method has the same effect as {@link
     * #serialize(XMLStreamWriter, boolean)} with <code>cache</code> set to <code>false</code>.
     *
     * @param xmlWriter
     * @throws XMLStreamException
     */
    void serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException;

    /**
     * Serializes the information item to the given {@link XMLStreamWriter}.
     *
     * <p>The implementation of this method must satisfy the following requirements:
     *
     * <ul>
     *   <li>If the writer exposes the {@link BlobWriter} extension, then base64 binary data MUST be
     *       written using one of the methods defined by that extension. This will occur if the
     *       information item is an {@link OMText} node for which {@link OMText#isBinary()} returns
     *       <code>true</code> or if it is an {@link OMContainer} that has such an {@link OMText}
     *       node as descendant. If the writer doesn't expose the {@link BlobWriter} extension, then
     *       the implementation MUST use {@link XMLStreamWriter#writeCharacters(String)} or {@link
     *       XMLStreamWriter#writeCharacters(char[], int, int)} to write the base64 encoded data to
     *       the stream.
     *   <li>The implementation MUST ensure that the produced XML is well formed with respect to
     *       namespaces, i.e. it MUST generate the required namespace declarations using {@link
     *       XMLStreamWriter#writeNamespace(String, String)} and {@link
     *       XMLStreamWriter#writeDefaultNamespace(String)}. This requirement is always applicable,
     *       even if the method is used to serialize a subtree or if the object model is not well
     *       formed with respect to namespaces. This means that the implementation is expected to
     *       implement namespace repairing and that the implementation MUST NOT assume that the
     *       {@link XMLStreamWriter} supplied by the caller performs any kind of namespace
     *       repairing.
     *   <li>The implementation MUST take into account the pre-existing namespace context, so that
     *       the caller can use this method to serialize the information item as part of a larger
     *       document. The implementation uses {@link XMLStreamWriter#getPrefix(String)} and/or
     *       {@link XMLStreamWriter#getNamespaceContext()} to query the pre-existing namespace
     *       context.
     * </ul>
     *
     * <p>On the other hand, the caller of this method must ensure that the following requirements
     * are satisfied:
     *
     * <ul>
     *   <li>The namespace context information provided by {@link XMLStreamWriter#getPrefix(String)}
     *       and {@link XMLStreamWriter#getNamespaceContext()} MUST accurately reflect the actual
     *       namespace context at the location in the output document where the information item is
     *       serialized. In practice this requirement means that if the caller writes content to the
     *       {@link XMLStreamWriter} before calling this method, then it must use {@link
     *       XMLStreamWriter#setPrefix(String, String)} and {@link
     *       XMLStreamWriter#setDefaultNamespace(String)} to update the namespace context. Note that
     *       this requirement may be relaxed if the caller implements some form of namespace
     *       repairing.
     * </ul>
     *
     * @param writer
     * @param cache indicates if caching should be enabled
     * @throws XMLStreamException if an exception was thrown by {@code writer}
     */
    void serialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException;
}
