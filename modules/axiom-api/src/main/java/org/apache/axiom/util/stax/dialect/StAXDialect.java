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

package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * Encapsulates the specific characteristics of a particular StAX implementation.
 * In particular, an implementation of this interface is able to wrap (if necessary) the
 * readers and writers produced by the StAX implementation to make them conform to the
 * StAX specifications. This is called <em>normalization</em>.
 * <p>
 * In addition to bugs in particular StAX implementations and clear violations of the StAX
 * specifications, the following ambiguities and gray areas in the specifications are also addressed
 * by the dialect implementations:
 * <ul>
 *   <li>The specifications don't tell whether it is allowed to use a <code>null</code> value
 *       for the charset encoding parameter in the following methods:
 *       <ul>
 *         <li>{@link XMLOutputFactory#createXMLEventWriter(java.io.OutputStream, String)}</li>
 *         <li>{@link javax.xml.stream.XMLStreamWriter#writeStartDocument(String, String)}</li>
 *       </ul>
 *       Most implementations accept <code>null</code> values, but some throw a
 *       {@link NullPointerException}. To avoid portability issues, the dialect implementation
 *       normalizes the behavior of these methods so that they accept <code>null</code> values
 *       (in which case the methods will delegate to the corresponding variants without
 *       charset encoding parameter).</li>
 *   <li>The StAX specifications require that {@link javax.xml.stream.XMLStreamReader#getEncoding()}
 *       returns the "input encoding if known or <code>null</code> if unknown". This requirement
 *       is not precise enough to guarantee consistent behavior across different implementations.
 *       In order to provide the consumer of the stream reader with complete and unambiguous information about
 *       the encoding of the underlying stream, the dialect implementations normalize the
 *       behavior of the {@link javax.xml.stream.XMLStreamReader#getEncoding()} method such that
 *       it returns a non null value if and only if the reader was created from a byte stream, in
 *       which case the return value is the effective charset encoding used by the parser to
 *       decode the byte stream. According to the XML specifications, this value is determined
 *       by one of the following means:
 *       <ul>
 *         <li>The encoding was provided when the stream reader was created, i.e. as a parameter
 *             to the {@link javax.xml.stream.XMLInputFactory#createXMLStreamReader(java.io.InputStream, String)}
 *             method. This is referred to as "external encoding information" by the XML
 *             specifications.</li>
 *         <li>The encoding was specified by the XML encoding declaration.</li>
 *         <li>The encoding was detected using the first four bytes of the stream, as described
 *             in appendix of the XML specifications.</li>
 *       </ul>
 *       </li>
 * </ul>
 * <p>
 * Note that there are several ambiguities in the StAX specification which are not addressed by
 * the different dialect implementations:
 * <ul>
 *   <li>It is not clear whether {@link javax.xml.stream.XMLStreamReader#getAttributePrefix(int)}
 *       should return <code>null</code> or an empty string if the attribute doesn't have a
 *       prefix. Consistency with {@link javax.xml.stream.XMLStreamReader#getPrefix()} would
 *       imply that it should return <code>null</code>, but some implementations return an empty
 *       string.</li>
 *   <li>There is a contradicting in the documentation of the
 *       {@link javax.xml.stream.XMLStreamReader#next()} about the exception that is thrown when
 *       this method is called after {@link javax.xml.stream.XMLStreamReader#hasNext()} returns
 *       false. It can either be {@link IllegalStateException} or
 *       {@link java.util.NoSuchElementException}.
 *       <p>
 *       Note that some implementations (including the reference implementation) throw an
 *       {@link javax.xml.stream.XMLStreamException} in this case. This is considered as a
 *       violation of the specifications because this exception should only be used
 *       "if there is an error processing the underlying XML source", which is not the case.</li>
 *   <li>An XML document may contain a namespace declaration such as <tt>xmlns=""</tt>. In this
 *       case, it is not clear if {@link javax.xml.stream.XMLStreamReader#getNamespaceURI(int)}
 *       should return <code>null</code> or an empty string.</li>
 * </ul>
 */
public interface StAXDialect {
    /**
     * Get the name of this dialect.
     * 
     * @return the name of the dialect
     */
    String getName();
    
    /**
     * Configure the given factory to enable reporting of CDATA sections by stream readers created
     * from it. The example in the documentation of the
     * {@link java.xml.stream.XMLStreamReader#next()} method suggests that even if the parser is non
     * coalescing, CDATA sections should be reported as CHARACTERS events. Some implementations
     * strictly follow the example, while for others it is sufficient to make the parser non
     * coalescing.
     * 
     * @param factory
     *            the factory to configure; this may be an already normalized factory or a "raw"
     *            factory object
     * @throws UnsupportedOperationException
     *             if reporting of CDATA sections is not supported
     */
    void enableCDataReporting(XMLInputFactory factory);
    
    /**
     * Make an {@link XMLInputFactory} object thread safe. The implementation may do this either by
     * configuring the factory or by creating a thread safe wrapper. The returned factory must be
     * thread safe for all method calls that don't change the (visible) state of the factory. This
     * means that thread safety is not required for
     * {@link XMLInputFactory#setEventAllocator(javax.xml.stream.util.XMLEventAllocator)},
     * {@link XMLInputFactory#setProperty(String, Object)},
     * {@link XMLInputFactory#setXMLReporter(javax.xml.stream.XMLReporter)} and
     * {@link XMLInputFactory#setXMLResolver(javax.xml.stream.XMLResolver)}.
     * 
     * @param factory
     *            the factory to make thread safe
     * @return the thread safe factory
     */
    XMLInputFactory makeThreadSafe(XMLInputFactory factory);
    
    /**
     * Make an {@link XMLOutputFactory} object thread safe. The implementation may do this either by
     * configuring the factory or by creating a thread safe wrapper. The returned factory must be
     * thread safe for all method calls that don't change the (visible) state, i.e. the properties,
     * of the factory.
     * 
     * @param factory
     *            the factory to make thread safe
     * @return the thread safe factory
     */
    XMLOutputFactory makeThreadSafe(XMLOutputFactory factory);
    
    /**
     * Normalize an {@link XMLInputFactory}. This will make sure that the readers created from the
     * factory conform to the StAX specifications.
     * 
     * @param factory
     *            the factory to normalize
     * @return the normalized factory
     */
    XMLInputFactory normalize(XMLInputFactory factory);
    
    /**
     * Normalize an {@link XMLOutputFactory}. This will make sure that the writers created from the
     * factory conform to the StAX specifications.
     * 
     * @param factory
     *            the factory to normalize
     * @return the normalized factory
     */
    XMLOutputFactory normalize(XMLOutputFactory factory);
}
