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
package org.apache.axiom.ext.stax;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Optional interface implemented by {@link XMLStreamReader} implementations that provide additional
 * data about {@link XMLStreamConstants#DTD} events.
 *
 * <p>All the requirements outlined in {@link org.apache.axiom.ext.stax} apply to this extension
 * interface. In particular, to get a reference to the extension, the consumer MUST call {@link
 * XMLStreamReader#getProperty(String)} with {@link #PROPERTY} as the property name.
 */
public interface DTDReader {
    /**
     * The name of the property used to look up this extension interface from a {@link
     * XMLStreamReader} implementation.
     */
    String PROPERTY = DTDReader.class.getName();

    /**
     * Get the root name of the DTD, i.e. the name immediately following the {@code DOCTYPE}
     * keyword.
     *
     * @return the root name; must not be <code>null</code>
     * @throws IllegalStateException if the current event is not {@link XMLStreamConstants#DTD}
     */
    String getRootName();

    /**
     * Get the public ID of the external subset.
     *
     * @return the public ID, or <code>null</code> if there is no external subset or no public ID
     *     has been specified for the external subset
     * @throws IllegalStateException if the current event is not {@link XMLStreamConstants#DTD}
     */
    String getPublicId();

    /**
     * Get the system ID of the external subset.
     *
     * @return the system ID, or <code>null</code> if there is no external subset
     * @throws IllegalStateException if the current event is not {@link XMLStreamConstants#DTD}
     */
    String getSystemId();
}
