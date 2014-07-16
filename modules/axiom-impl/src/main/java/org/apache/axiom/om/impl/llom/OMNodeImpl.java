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

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.INode;

/** Class OMNodeImpl */
public abstract class OMNodeImpl extends OMSerializableImpl implements OMNode {
    /**
     * Constructor OMNodeImpl
     *
     * @param factory The <code>OMFactory</code> that created this
     */
    public OMNodeImpl(OMFactory factory) {
        super(factory);
    }

    /**
     * Method setParent.
     *
     * @param element
     */
    public void coreSetParent(CoreParentNode element) {
        CoreParentNode currentParent = coreGetParent();

        if (currentParent == element) {
            return;
        }

        //If we are asked to assign a new parent in place
        //of an existing one. We should detach this node
        //from the previous parent.
        if (element != null) {
            if (currentParent != null) {
                this.detach();
            }
            internalSetParent(element);
        } else {
            internalUnsetParent(null);
        }
    }

    /**
     * Method setNextOMSibling.
     *
     * @param node
     */
    public void setNextOMSibling(OMNode node) {
        coreSetNextSibling((OMNodeImpl)node);
    }

    /**
     * Removes this information item and its children, from the model completely.
     *
     * @throws OMException
     */
    public OMNode detach() throws OMException {
        CoreParentNode parent = coreGetParent();
        if (parent == null) {
            throw new OMException(
                    "Nodes that don't have a parent can not be detached");
        }
        // Note that we don't need to force creation of the next sibling because the
        // builder will always add new nodes to the end of list of children of the
        // document or element being built.
        INode nextSibling = (INode)getNextOMSiblingIfAvailable();
        INode previousSibling = (INode)coreGetPreviousSibling();
        if (previousSibling == null) {
            parent.coreSetFirstChild(nextSibling);
        } else {
            previousSibling.coreSetNextSibling(nextSibling);
        }
        if (nextSibling == null) {
            parent.coreSetLastChild(previousSibling);
        } else {
            nextSibling.coreSetPreviousSibling(previousSibling);
        }

        coreSetPreviousSibling(null);
        coreSetNextSibling(null);
        internalUnsetParent(null);
        return this;
    }

    /**
     * Method setPreviousOMSibling.
     *
     * @param previousSibling
     */
    public void setPreviousOMSibling(OMNode previousSibling) {
        coreSetPreviousSibling((OMNodeImpl)previousSibling);
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

    public OMInformationItem clone(OMCloneOptions options) {
        return clone(options, null);
    }

    abstract OMNode clone(OMCloneOptions options, OMContainer targetParent);
}
