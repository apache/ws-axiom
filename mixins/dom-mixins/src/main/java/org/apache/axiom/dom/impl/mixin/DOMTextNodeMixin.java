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
package org.apache.axiom.dom.impl.mixin;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.dom.DOMText;
import org.apache.axiom.dom.DOMTextNode;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

@Mixin
public abstract class DOMTextNodeMixin implements DOMTextNode {
    private DOMTextNode getWholeTextStartNode() {
        DOMTextNode first = this;
        while (true) {
            CoreChildNode sibling = first.coreGetPreviousSibling();
            if (sibling instanceof DOMTextNode) {
                first = (DOMTextNode) sibling;
            } else {
                break;
            }
        }
        return first;
    }

    private DOMTextNode getWholeTextEndNode() {
        try {
            DOMTextNode last = this;
            while (true) {
                CoreChildNode sibling = last.coreGetNextSibling();
                if (sibling instanceof DOMTextNode) {
                    last = (DOMTextNode) sibling;
                } else {
                    break;
                }
            }
            return last;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final String getWholeText() {
        try {
            DOMTextNode first = getWholeTextStartNode();
            DOMTextNode last = getWholeTextEndNode();
            if (first == last) {
                return first.getData();
            } else {
                StringBuilder buffer = new StringBuilder();
                DOMTextNode current = first;
                while (true) {
                    buffer.append(current.getData());
                    if (current == last) {
                        break;
                    } else {
                        current = (DOMTextNode) current.coreGetNextSibling();
                    }
                }
                return buffer.toString();
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Text replaceWholeText(String content) throws DOMException {
        try {
            DOMText newText;
            if (content.length() > 0) {
                newText = getDOMNodeFactory().createCharacterDataNode();
                newText.coreSetCharacterData(content);
            } else {
                newText = null;
            }
            if (coreHasParent()) {
                DOMTextNode first = getWholeTextStartNode();
                DOMTextNode last = getWholeTextEndNode();
                if (newText != null) {
                    first.coreInsertSiblingBefore(newText);
                }
                DOMTextNode current = first;
                DOMTextNode next;
                do {
                    next = current == last ? null : (DOMTextNode) current.coreGetNextSibling();
                    current.coreDetach(DOMSemantics.INSTANCE);
                    current = next;
                } while (next != null);
            }
            return newText;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Text splitText(int offset) throws DOMException {
        try {
            String value = getData();
            if (offset < 0 || offset > value.length()) {
                throw newDOMException(DOMException.INDEX_SIZE_ERR);
            }
            String newValue = value.substring(offset);
            deleteData(offset, value.length());
            DOMText newText = getDOMNodeFactory().createCharacterDataNode();
            newText.coreSetCharacterData(newValue);
            if (coreHasParent()) {
                coreInsertSiblingAfter(newText);
            } else {
                newText.coreSetOwnerDocument(coreGetOwnerDocument(true));
            }
            return newText;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public String toString() {
        String value = getData();
        return value != null ? value : "";
    }
}
