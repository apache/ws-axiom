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
package org.apache.axiom.om.impl.common;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

/**
 * Utility class with default implementations for some of the methods defined by the
 * {@link OMElement} interface.
 */
public aspect AxiomElementSupport {
    declare parents: (InformationItem+ && OMElement+) implements AxiomElement;
    
    public final int AxiomElement.getType() {
        return OMNode.ELEMENT_NODE;
    }
    
    public void AxiomElement.setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        internalSetNamespace(namespace);
    }

    public void AxiomElement.setNamespace(OMNamespace namespace, boolean decl) {
        internalSetNamespace(handleNamespace(this, namespace, false, decl));
    }

    public final OMElement AxiomElement.getFirstElement() {
        OMNode node = getFirstOMChild();
        while (node != null) {
            if (node.getType() == OMNode.ELEMENT_NODE) {
                return (OMElement) node;
            } else {
                node = node.getNextOMSibling();
            }
        }
        return null;
    }

    public final Iterator AxiomElement.getChildElements() {
        return new OMChildElementIterator(getFirstElement());
    }

    public final Iterator AxiomElement.getNamespacesInScope() {
        return new NamespaceIterator(this);
    }

    public NamespaceContext AxiomElement.getNamespaceContext(boolean detached) {
        if (detached) {
            Map namespaces = new HashMap();
            for (Iterator it = getNamespacesInScope(); it.hasNext(); ) {
                OMNamespace ns = (OMNamespace)it.next();
                namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
            }
            return new MapBasedNamespaceContext(namespaces);
        } else {
            return new LiveNamespaceContext(this);
        }
    }
    
    public final QName AxiomElement.resolveQName(String qname) {
        int idx = qname.indexOf(':');
        if (idx == -1) {
            OMNamespace ns = getDefaultNamespace();
            return ns == null ? new QName(qname) : new QName(ns.getNamespaceURI(), qname, "");
        } else {
            String prefix = qname.substring(0, idx);
            OMNamespace ns = findNamespace(null, prefix);
            return ns == null ? null : new QName(ns.getNamespaceURI(), qname.substring(idx+1), prefix);
        }
    }

    public String AxiomElement.getText() {
        String childText = null;
        StringBuffer buffer = null;
        OMNode child = getFirstOMChild();

        while (child != null) {
            final int type = child.getType();
            if (type == OMNode.TEXT_NODE || type == OMNode.CDATA_SECTION_NODE) {
                OMText textNode = (OMText) child;
                String textValue = textNode.getText();
                if (textValue != null && textValue.length() != 0) {
                    if (childText == null) {
                        // This is the first non empty text node. Just save the string.
                        childText = textValue;
                    } else {
                        // We've already seen a non empty text node before. Concatenate using
                        // a StringBuffer.
                        if (buffer == null) {
                            // This is the first text node we need to append. Initialize the
                            // StringBuffer.
                            buffer = new StringBuffer(childText);
                        }
                        buffer.append(textValue);
                    }
                }
            }
            child = child.getNextOMSibling();
        }

        if (childText == null) {
            // We didn't see any text nodes. Return an empty string.
            return "";
        } else if (buffer != null) {
            return buffer.toString();
        } else {
            return childText;
        }
    }
    
    // Note: must not be final because it is (incorrectly) overridden in the SOAPFaultCode implementation for SOAP 1.2
    public QName AxiomElement.getTextAsQName() {
        String childText = getText().trim();
        return childText.length() == 0 ? null : resolveQName(childText);
    }

    public Reader AxiomElement.getTextAsStream(boolean cache) {
        // If the element is not an OMSourcedElement and has not more than one child, then the most
        // efficient way to get the Reader is to build a StringReader
        if (!(this instanceof OMSourcedElement) && (!cache || isComplete())) {
            OMNode child = getFirstOMChild();
            if (child == null) {
                return new StringReader("");
            } else if (child.getNextOMSibling() == null) {
                return new StringReader(child instanceof OMText ? ((OMText)child).getText() : "");
            }
        }
        // In all other cases, extract the data from the XMLStreamReader
        try {
            XMLStreamReader reader = getXMLStreamReader(cache);
            if (reader.getEventType() == XMLStreamReader.START_DOCUMENT) {
                reader.next();
            }
            return XMLStreamReaderUtils.getElementTextAsStream(reader, true);
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }
    
    public void AxiomElement.writeTextTo(Writer out, boolean cache) throws IOException {
        try {
            XMLStreamReader reader = getXMLStreamReader(cache);
            int depth = 0;
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.CHARACTERS:
                    case XMLStreamReader.CDATA:
                        if (depth == 1) {
                            out.write(reader.getText());
                        }
                        break;
                    case XMLStreamReader.START_ELEMENT:
                        depth++;
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        depth--;
                }
            }
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }
    
    // Not final because overridden in Abdera
    public void AxiomElement.setText(String text) {
        removeChildren();
        // Add a new text node
        if (text != null && text.length() > 0) {
            getOMFactory().createOMText(this, text);
        }
    }

    public final void AxiomElement.setText(QName qname) {
        removeChildren();
        // Add a new text node
        if (qname != null) {
            OMNamespace ns = handleNamespace(qname.getNamespaceURI(), qname.getPrefix());
            getOMFactory().createOMText(this,
                    ns == null ? qname.getLocalPart() : ns.getPrefix() + ":" + qname.getLocalPart());
        }
    }

    public void AxiomElement.discard() {
        if (getState() == CoreParentNode.INCOMPLETE && getBuilder() != null) {
            ((StAXOMBuilder)getBuilder()).discard((OMContainer)this);
        }
        detach();
    }
    
    public void AxiomElement.detachAndDiscardParent() {
        internalUnsetParent(null);
        coreSetPreviousSibling(null);
        coreSetNextSibling(null);
    }
    
    public void AxiomElement.insertChild(Class[] sequence, int pos, OMNode newChild) {
        if (!sequence[pos].isInstance(newChild)) {
            throw new IllegalArgumentException();
        }
        OMNode child = getFirstOMChild();
        while (child != null) {
            if (child instanceof OMElement) {
                if (child == newChild) {
                    // The new child is already a child of the element and it is at
                    // the right position
                    return;
                }
                if (sequence[pos].isInstance(child)) {
                    // Replace the existing child
                    child.insertSiblingAfter(newChild);
                    child.detach();
                    return;
                }
                // isAfter indicates if the new child should be inserted after the current child
                boolean isAfter = false;
                for (int i=0; i<pos; i++) {
                    if (sequence[i].isInstance(child)) {
                        isAfter = true;
                        break;
                    }
                }
                if (!isAfter) {
                    // We found the right position to insert the new child
                    child.insertSiblingBefore(newChild);
                    return;
                }
            }
            child = child.getNextOMSibling();
        }
        // Else, add the new child at the end
        addChild(newChild);
    }

    public final OMNamespace AxiomElement.handleNamespace(String namespaceURI, String prefix) {
        if (prefix.length() == 0 && namespaceURI.length() == 0) {
            OMNamespace namespace = getDefaultNamespace();
            if (namespace != null) {
                declareDefaultNamespace("");
            }
            return null;
        } else {
            OMNamespace namespace = findNamespace(namespaceURI,
                                                  prefix);
            if (namespace == null) {
                namespace = declareNamespace(namespaceURI, prefix.length() > 0 ? prefix : null);
            }
            return namespace;
        }
    }
}
