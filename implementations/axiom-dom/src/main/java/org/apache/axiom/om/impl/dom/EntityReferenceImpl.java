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
package org.apache.axiom.om.impl.dom;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.dom.DOMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.AxiomEntityReference;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EntityReferenceImpl extends ChildNode implements AxiomEntityReference, DOMEntityReference {
    private final String name;
    private final String replacementText;

    public EntityReferenceImpl(String name, String replacementText, OMFactory factory) {
        super(factory);
        this.name = name;
        this.replacementText = replacementText;
    }

    public int getType() {
        return OMNode.ENTITY_REFERENCE_NODE;
    }

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        serializer.writeEntityRef(name);
    }

    public String getName() {
        return name;
    }

    public String getReplacementText() {
        return replacementText;
    }

    ChildNode createClone() {
        return new EntityReferenceImpl(name, replacementText, getOMFactory());
    }

    public String getNodeName() {
        return name;
    }

    public short getNodeType() {
        return Node.ENTITY_REFERENCE_NODE;
    }

    public final boolean hasChildNodes() {
        throw new UnsupportedOperationException();
    }

    public Node getFirstChild() {
        throw new UnsupportedOperationException();
    }

    public Node getLastChild() {
        throw new UnsupportedOperationException();
    }

    public final NodeList getChildNodes() {
        throw new UnsupportedOperationException();
    }

    public final Node appendChild(Node newChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public final Node removeChild(Node oldChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public final Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public final Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public String getTextContent() throws DOMException {
        throw new UnsupportedOperationException();
    }

    void getTextContent(StringBuffer buf) {
        throw new UnsupportedOperationException();
    }
}
