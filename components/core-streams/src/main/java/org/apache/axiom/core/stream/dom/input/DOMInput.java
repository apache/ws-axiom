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
package org.apache.axiom.core.stream.dom.input;

import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.XmlReader;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

public class DOMInput implements XmlInput {
    private final Node node;
    private final boolean expandEntityReferences;
    
    /**
     * Constructor.
     * 
     * @param node
     *            The root node of the tree from which events will be generated.
     * @param expandEntityReferences
     *            Determines how {@link EntityReference} nodes are handled by this instance. When
     *            set to {@code false}, a single
     *            {@link XmlHandler#processEntityReference(String, String)} event will be emitted
     *            for each {@link EntityReference}. When set to {@code true}, no
     *            {@link XmlHandler#processEntityReference(String, String)} events are generated.
     *            Instead, the implementation will traverse the descendants of the
     *            {@link EntityReference} nodes (which effectively expands these entity references).
     */
    public DOMInput(Node node, boolean expandEntityReferences) {
        this.node = node;
        this.expandEntityReferences = expandEntityReferences;
    }

    @Override
    public XmlReader createReader(XmlHandler handler) {
        return new DOMReader(handler, node, expandEntityReferences);
    }
}
