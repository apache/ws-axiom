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
import javax.xml.stream.XMLStreamReader;

/**
 * Encapsulates the specific characteristics of a particular StAX implementation.
 * In particular, an implementation of this interface is able to wrap (if necessary) the
 * readers and writers produced by the StAX implementation to make them conform to the
 * StAX specifications. This is called <em>normalization</em>.
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
 *       {@link java.util.NoSuchElementException}.</li>
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
     * from it. The example in the documentation of the {@link XMLStreamReader#next()} method
     * suggests that even if the parser is non coalescing, CDATA sections should be reported as
     * CHARACTERS events. Some implementations strictly follow the example, while for others it is
     * sufficient to make the parser non coalescing.
     * 
     * @param factory
     *            the factory to configure; this may be an already normalized factory or a "raw"
     *            factory object
     * @throws UnsupportedOperationException
     *             if reporting of CDATA sections is not supported
     */
    void enableCDataReporting(XMLInputFactory factory);
    
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
