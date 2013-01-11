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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.IContainer;
import org.apache.axiom.om.impl.common.OMContainerHelper;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

// TODO: we should not implement IContainer here
public class DocumentFragmentImpl extends RootNode implements
        DocumentFragment, IContainer {

    private ParentNode ownerNode;
    
    /** @param ownerDocument  */
    public DocumentFragmentImpl(OMFactory factory) {
        super(factory);
    }

    final ParentNode internalGetOwnerNode() {
        return ownerNode;
    }

    final void internalSetOwnerNode(ParentNode ownerNode) {
        this.ownerNode = ownerNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return Node.DOCUMENT_FRAGMENT_NODE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        return "#document-fragment";
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter)
            throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        return new DocumentFragmentImpl(factory);
    }

    public final OMXMLParserWrapper getBuilder() {
        return null;
    }

    public final int getState() {
        return COMPLETE;
    }

    public final boolean isComplete() {
        return true;
    }

    public final void setComplete(boolean state) {
        if (state != true) {
            throw new IllegalStateException();
        }
    }

    public final void discarded() {
        throw new UnsupportedOperationException();
    }

    public final void build() {
        // A document fragment doesn't have a builder
    }

    public final Node getNextSibling() {
        return null;
    }

    // TODO: we really shouldn't implement IContainer, but only IParentNode
    public void removeChildren() {
        OMContainerHelper.removeChildren(this);
    }

    public final String lookupNamespaceURI(String specifiedPrefix) {
        return null;
    }

    public final void checkChild(OMNode child) {
    }
}
