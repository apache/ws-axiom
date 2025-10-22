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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * Optional interface implemented by {@link OMDataSource} implementations that have knowledge about
 * the local name, namespace URI and/or namespace prefix of the element they represent. This
 * interface is used by {@link OMSourcedElement} to lazily determine the name of the element without
 * expanding it. The information returned by the implementation may be partial; e.g. the data source
 * may know about the local name and namespace URI but it may be unable to predict the namespace
 * prefix. The returned information must be accurate, i.e. it must match the name of the root
 * element in the document returned by {@link OMDataSource#getReader()}.
 *
 * <p>This interface should be implemented by {@link OMDataSource} implementations that have an
 * efficient way to determine the root element name (or part of it) from the information effectively
 * used by {@link OMDataSource#getReader()} to construct the {@link XMLStreamReader} instance. In
 * practice this applies to {@link OMDataSource} implementations that satisfy one of the following
 * conditions:
 *
 * <ul>
 *   <li>The data source wraps another type of object and is able to determine the element name from
 *       that object in an efficient way. E.g. this is often the case if the data source uses a
 *       Java-to-XML mapping framework to transform the wrapped object into XML.
 *   <li>The QName of the element is configurable and supplied by the application code when the data
 *       source is instantiated.
 * </ul>
 *
 * This interface should not be implemented if the returned information would be supplied by the
 * application code when the data source is instantiate, without the data source being able to
 * guarantee that the information is accurate (i.e. without {@link OMDataSource#getReader()}
 * actually using that information). In fact, in this case the application code should use {@link
 * OMFactory#createOMElement(OMDataSource, String, OMNamespace)} or {@link
 * OMFactory#createOMElement(OMDataSource, QName)} to supply the QName information it has.
 */
public interface QNameAwareOMDataSource extends OMDataSource {
    /**
     * Get the local name of the element represented by this data source.
     *
     * @return the local name of the element or <code>null</code> if the local name is not known
     */
    String getLocalName();

    /**
     * Get the namespace URI of the element represented by this data source.
     *
     * @return the namespace URI of the element, the empty string if the element has no namespace or
     *     <code>null</code> if the namespace URI is not known
     */
    String getNamespaceURI();

    /**
     * Get the namespace prefix of the element represented by this data source.
     *
     * @return the prefix of the element, the empty string if the element has no prefix or <code>
     *     null</code> if the prefix is not known; the implementation is expected to return an empty
     *     string if {@link #getNamespaceURI()} returns an empty string (because an element without
     *     namespace must not have a prefix)
     */
    String getPrefix();
}
