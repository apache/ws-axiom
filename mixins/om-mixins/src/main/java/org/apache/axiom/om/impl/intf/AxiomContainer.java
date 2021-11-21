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
package org.apache.axiom.om.impl.intf;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;

public interface AxiomContainer extends OMContainer, AxiomCoreParentNode {
    /**
     * Check if the node can be added as a child of this container.
     * 
     * @param child
     *            the child that will be added
     * @throws OMException
     *             if the node is not allowed as a child of the container
     */
    void checkChild(OMNode child);

    AxiomChildNode prepareNewChild(OMNode omNode);
    XMLStreamReader defaultGetXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration);
    CoreElement getContextElement();
}
