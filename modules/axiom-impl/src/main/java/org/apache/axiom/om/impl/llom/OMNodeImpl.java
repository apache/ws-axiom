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

package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.common.IChildNode;
import org.apache.axiom.om.impl.common.IContainer;
import org.apache.axiom.om.impl.common.IParentNode;
import org.apache.axiom.om.impl.common.OMNodeHelper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

/** Class OMNodeImpl */
public abstract class OMNodeImpl extends OMSerializableImpl implements IChildNode {
    
    /** Field parent */
    protected IContainer parent;

    /** Field nextSibling */
    protected OMNodeImpl nextSibling;

    /** Field previousSibling */
    protected OMNodeImpl previousSibling;

    /**
     * Constructor OMNodeImpl
     *
     * @param factory The <code>OMFactory</code> that created this
     */
    public OMNodeImpl(OMFactory factory) {
        super(factory);
    }

    /**
     * Returns the immediate parent of the node. Parent is always an Element.
     *
     * @return Returns OMContainer.
     * @throws OMException
     */
    public OMContainer getParent() {
        return parent;
    }

    public IParentNode getIParentNode() {
        return parent;
    }

    /**
     * Method setParent.
     *
     * @param element
     */
    public void setParent(OMContainer element) {

        if ((this.parent) == element) {
            return;
        }

        //If we are asked to assign a new parent in place
        //of an existing one. We should detach this node
        //from the previous parent.
        if (element != null) {
            if (this.parent != null) {
                this.detach();
            }
            this.parent = (IContainer) element;
        } else {
            this.parent = null;
        }
    }

    /**
     * Returns the next sibling. This can be an OMAttribute or OMText or OMElement for others.
     *
     * @return Returns OMNode.
     * @throws org.apache.axiom.om.OMException
     *
     */
    public OMNode getNextOMSibling() throws OMException {
        return OMNodeHelper.getNextOMSibling(this);
    }

    public OMNode getNextOMSiblingIfAvailable() {
        return nextSibling;
    }

    /**
     * Method setNextOMSibling.
     *
     * @param node
     */
    public void setNextOMSibling(OMNode node) {
        if (node == null || node.getOMFactory() instanceof OMLinkedListImplFactory) {
            this.nextSibling = (OMNodeImpl) node;
        } else {
            this.nextSibling = (OMNodeImpl) ((OMFactoryEx)factory).importNode(node);
        }
        this.nextSibling = (OMNodeImpl) node;
    }

    /**
     * Removes this information item and its children, from the model completely.
     *
     * @throws OMException
     */
    public OMNode detach() throws OMException {
        if (parent == null) {
            throw new OMException(
                    "Nodes that don't have a parent can not be detached");
        }
        OMNodeImpl nextSibling = (OMNodeImpl) getNextOMSibling();
        if (previousSibling == null) {
            parent.setFirstChild(nextSibling);
        } else {
            ((OMNodeEx) getPreviousOMSibling()).setNextOMSibling(nextSibling);
        }
        if (nextSibling != null) {
            nextSibling.setPreviousOMSibling(getPreviousOMSibling());
        }

        if ((parent instanceof OMElementImpl) && ((OMElementImpl) parent).lastChild == this) {
            ((OMElementImpl) parent).lastChild = getPreviousOMSibling();
        }

        this.previousSibling = null;
        this.nextSibling = null;
        this.parent = null;
        return this;
    }

    /**
     * Inserts a sibling just after the current information item.
     *
     * @param sibling
     * @throws OMException
     */
    public void insertSiblingAfter(OMNode sibling) throws OMException {
        if (parent == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        ((OMNodeEx) sibling).setParent(parent);
        if (sibling instanceof OMNodeImpl) {
            OMNodeImpl siblingImpl = (OMNodeImpl) sibling;
            if (nextSibling == null) {
                getNextOMSibling();
            }
            siblingImpl.setPreviousOMSibling(this);
            if (nextSibling == null) {
                parent.setLastChild(sibling);
            } else {
                nextSibling.setPreviousOMSibling(sibling);
            }
            ((OMNodeEx) sibling).setNextOMSibling(nextSibling);
            nextSibling = siblingImpl;
        }
    }

    /**
     * Inserts a sibling just before the current information item.
     *
     * @param sibling
     * @throws OMException
     */
    public void insertSiblingBefore(OMNode sibling) throws OMException {
        if (parent == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        if (sibling instanceof OMNodeImpl) {
            OMNodeImpl siblingImpl = (OMNodeImpl) sibling;
            
            if (previousSibling == null) {
                parent.setFirstChild(siblingImpl);
                siblingImpl.nextSibling = this;
                siblingImpl.previousSibling = null;
            } else {
                siblingImpl.setParent(parent);
                siblingImpl.nextSibling = this;
                previousSibling.setNextOMSibling(siblingImpl);
                siblingImpl.setPreviousOMSibling(previousSibling);
            }
            previousSibling = siblingImpl;

        }
    }

    /**
     * Gets the previous sibling.
     *
     * @return boolean
     */
    public OMNode getPreviousOMSibling() {
        return previousSibling;
    }

    /**
     * Method setPreviousOMSibling.
     *
     * @param previousSibling
     */
    public void setPreviousOMSibling(OMNode previousSibling) {
        if (previousSibling == null ||
                previousSibling.getOMFactory() instanceof OMLinkedListImplFactory) {
            this.previousSibling = (OMNodeImpl) previousSibling;
        } else {
            this.previousSibling = (OMNodeImpl) ((OMFactoryEx)factory).importNode(previousSibling);
        }
    }

    /**
     * Parses this node and builds the object structure in memory. AXIOM supports two levels of
     * deffered building. First is deffered building of AXIOM using StAX. Second level is the
     * deffered building of attachments. AXIOM reads in the attachements from the stream only when
     * user asks by calling getDataHandler(). build() method builds the OM without the attachments.
     * buildAll() builds the OM together with attachement data. This becomes handy when user wants
     * to free the input stream.
     */
    public void buildWithAttachments() {
        if (!isComplete()) {
            this.build();
        }
    }

    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer, true);
    }

    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer, false);
    }
    
    abstract OMNode clone(OMCloneOptions options, OMContainer targetParent);
}
