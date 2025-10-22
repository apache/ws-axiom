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

import javax.xml.stream.XMLStreamWriter;

/**
 * Exception indicating that a requested node cannot be returned because it is no longer available.
 * A node may become unavailable because it has been consumed by a method such as {@link
 * OMContainer#serializeAndConsume(XMLStreamWriter)} or {@link
 * OMContainer#getXMLStreamReaderWithoutCaching()}, or because one of its ancestors has been
 * discarded using {@link OMNode#discard()}.
 */
public class NodeUnavailableException extends OMException {
    private static final long serialVersionUID = -9034004432518092807L;

    public NodeUnavailableException() {}
}
