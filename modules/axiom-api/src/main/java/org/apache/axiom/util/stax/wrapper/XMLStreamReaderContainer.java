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
package org.apache.axiom.util.stax.wrapper;

import javax.xml.stream.XMLStreamReader;

/**
 * Marker interface for Axiom {@link XMLStreamReader} classes that
 * wrap or delegate to another (parent) XMLStreamReader.
 * <p>
 * The marker interface is necessary so that consumers 
 * can access the original parser.
 * <p>
 * Note that the only the {@link #getParent()} method is applicable.
 * Please do not add a <code>setParent()</code> method since that would
 * violate the immutable characteristic of the {@link XMLStreamReaderWrapper}.
 * 
 * @see org.apache.axiom.util.stax.XMLStreamReaderUtils
 */
public interface XMLStreamReaderContainer {
    XMLStreamReader getParent();
}
