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

package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Interface OMNodeEx
 * <p/>
 * Internal Implementation detail. Adding special interface to stop folks from accidently using
 * OMNode. Please use at your own risk. May corrupt the data integrity.
 */
public interface OMNodeEx extends OMNode {
    public void setNextOMSibling(OMNode node);

    public void setPreviousOMSibling(OMNode previousSibling);

    public void setParent(OMContainer element);

    public void setComplete(boolean state);

    public void setType(int nodeType) throws OMException;

    /**
     * Serializes the node with caching.
     *
     * @param writer
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    public void internalSerialize(XMLStreamWriter writer)
            throws XMLStreamException;

    /**
     * Serializes the node without caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException;
}
