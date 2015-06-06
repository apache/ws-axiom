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

import org.apache.axiom.dom.DOMTextNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMNodeEx;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

import javax.activation.DataHandler;

public abstract class TextNodeImpl extends LeafNode implements DOMTextNode, OMText, OMNodeEx {
    public TextNodeImpl(OMFactory factory) {
        super(factory);
    }

    /** Sets the text value of data. */
    public void setData(String data) throws DOMException {
        this.value = data;
    }

    /**
     * Breaks this node into two nodes at the specified offset, keeping both in the tree as
     * siblings. After being split, this node will contain all the content up to the offset point. A
     * new node of the same type, which contains all the content at and after the offset point, is
     * returned. If the original node had a parent node, the new node is inserted as the next
     * sibling of the original node. When the offset is equal to the length of this node, the new
     * node has no data.
     */
    public Text splitText(int offset) throws DOMException {
        if (offset < 0 || offset > this.value.length()) {
            throw newDOMException(DOMException.INDEX_SIZE_ERR);
        }
        String newValue = this.value.substring(offset);
        this.deleteData(offset, this.value.length());

        TextImpl newText = (TextImpl) this.getOwnerDocument().createTextNode(
                newValue);

        ParentNode parentNode = (ParentNode)coreGetParent();
        if (parentNode != null) {
            this.insertSiblingAfter(newText);
        }

        return newText;
    }

    // /
    // /OMNode methods
    // /

    public String getData() throws DOMException {
        return this.getText();
    }

    /*
    * DOM-Level 3 methods
    */

    public boolean isElementContentWhitespace() {
        // TODO TODO
        return false;
    }

    public String toString() {
        return (this.value != null) ? value : "";
    }

    void beforeClone(OMCloneOptions options) {
        if (isBinary() && options.isFetchDataHandlers()) {
            // Force loading of the reference to the DataHandler and ensure that its content is
            // completely fetched into memory (or temporary storage).
            ((DataHandler)getDataHandler()).getDataSource();
        }
    }

    final ChildNode createClone() {
        return (ChildNode)doClone();
    }
}
