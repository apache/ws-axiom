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

/**
 * 
 */
package org.apache.axiom.om;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Interface to arbitrary source of XML element data. This provides the hook for using a general
 * data source (such as data binding frameworks) as the backing source of data for an element.
 */
public interface OMDataSource {
    /**
     * Serializes element data directly to stream.
     * <p>
     * It is assumed that this method consumes the content (i.e. destroys the backing object) unless
     * the data source also implements {@link OMDataSourceExt} and
     * {@link OMDataSourceExt#isDestructiveWrite()} returns <code>false</code>.
     * 
     * @param output
     *            destination stream for element XML text
     * @param format
     *            Output format information. The implementation must use this information to choose
     *            the correct character set encoding when writing to the output stream. This
     *            parameter must not be null.
     * @throws XMLStreamException
     */
    void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException;

    /**
     * Serializes element data directly to writer.
     * <p>
     * It is assumed that this method consumes the content (i.e. destroys the backing object) unless
     * the data source also implements {@link OMDataSourceExt} and
     * {@link OMDataSourceExt#isDestructiveWrite()} returns <code>false</code>.
     * 
     * @param writer
     *            destination writer for element XML text
     * @param format
     *            output format information (<code>null</code> if none; may be ignored if not
     *            supported by data binding even if supplied)
     * @throws XMLStreamException
     */
    void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException;

    /**
     * Serializes element data directly to StAX writer.
     * <p>
     * The implementation of this method must satisfy the following requirements:
     * <ul>
     * <li>The implementation MUST NOT not write any start document or end document event, i.e. it
     * MUST NOT use {@link XMLStreamWriter#writeStartDocument()},
     * {@link XMLStreamWriter#writeStartDocument(String)},
     * {@link XMLStreamWriter#writeStartDocument(String, String)} or
     * {@link XMLStreamWriter#writeEndElement()}.
     * <li>The implementation MUST output a single element (hereafter called the root element). It
     * MUST NOT output any other content before or after that element.
     * <li>The implementation MUST specify the namespace URI when writing an element, i.e. it MUST
     * NOT use the namespace unaware methods {@link XMLStreamWriter#writeStartElement(String)} or
     * {@link XMLStreamWriter#writeEmptyElement(String)}. On the other hand, it MAY use the
     * namespace unaware {@link XMLStreamWriter#writeAttribute(String, String)} method, provided
     * that the specified name is an <tt>NCName</tt>.
     * <li>The implementation MUST ensure that the produced XML is well formed with respect to
     * namespaces, i.e. it MUST generate the required namespace declarations using
     * {@link XMLStreamWriter#writeNamespace(String, String)} and
     * {@link XMLStreamWriter#writeDefaultNamespace(String)}. It MUST NOT assume that the
     * {@link XMLStreamWriter} performs any kind of namespace repairing (although that may be the
     * case).
     * <li>In addition the implementation MAY use {@link XMLStreamWriter#setPrefix(String, String)}
     * and {@link XMLStreamWriter#setDefaultNamespace(String)} to track the namespace declarations
     * it generates, so that the namespace context maintained by the {@link XMLStreamWriter}
     * accurately reflects the namespace context in the output document. However, it MUST NOT call
     * these methods before the start of the root element or after the end of the root element.
     * <li>Since the element may be serialized as part of a larger document, the implementation MUST
     * take into account the pre-existing namespace context (which can be queried using
     * {@link XMLStreamWriter#getPrefix(String)} and {@link XMLStreamWriter#getNamespaceContext()}).
     * This means that the implementation MUST NOT assume that the empty prefix is bound to the
     * empty namespace URI. Therefore if the implementation outputs elements that have no namespace,
     * it MUST generate namespace declarations of the form <tt>xmlns=""</tt> in the appropriate
     * locations. In addition it MAY use the namespace context information to minimize the number of
     * generated namespace declarations (by reusing already bound prefixes).
     * </ul>
     * <p>
     * On the other hand, the caller of this method (typically an {@link OMSourcedElement} instance)
     * must ensure that the following requirements are satisfied:
     * <ul>
     * <li>The namespace context information provided by {@link XMLStreamWriter#getPrefix(String)}
     * and {@link XMLStreamWriter#getNamespaceContext()} MUST accurately reflect the actual
     * namespace context at the location in the output document where the root element is
     * serialized. Note that this requirement may be relaxed if the caller implements some form of
     * namespace repairing.
     * </ul>
     * <p>
     * It is assumed that this method consumes the content (i.e. destroys the backing object) unless
     * the data source also implements {@link OMDataSourceExt} and
     * {@link OMDataSourceExt#isDestructiveWrite()} returns <code>false</code>.
     * 
     * @param xmlWriter
     *            destination writer
     * @throws XMLStreamException
     */
    // TODO: specify how optimizable base64 binary data (MTOM) is handled
    void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException;

    /**
     * Get parser for element data. In the general case this may require the data source to
     * serialize data as XML text and then parse that text.
     * <p>
     * It is assumed that this method consumed the content (i.e. destroys the backing object) unless
     * the data source also implements {@link OMDataSourceExt} and
     * {@link OMDataSourceExt#isDestructiveRead()} returns <code>false</code>.
     * 
     * @return element parser
     * @throws XMLStreamException
     */
    XMLStreamReader getReader() throws XMLStreamException;
}
