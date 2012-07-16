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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

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
public class OMElementImplUtil {
    private OMElementImplUtil() {}
    
    public static NamespaceContext getNamespaceContext(OMElement element, boolean detached) {
        if (detached) {
            Map namespaces = new HashMap();
            for (Iterator it = element.getNamespacesInScope(); it.hasNext(); ) {
                OMNamespace ns = (OMNamespace)it.next();
                namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
            }
            return new MapBasedNamespaceContext(namespaces);
        } else {
            return new LiveNamespaceContext(element);
        }
    }
    
    public static String getText(OMElement element) {
        String childText = null;
        StringBuffer buffer = null;
        OMNode child = element.getFirstOMChild();

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
    
    public static Reader getTextAsStream(OMElement element, boolean cache) {
        // If the element is not an OMSourcedElement and has not more than one child, then the most
        // efficient way to get the Reader is to build a StringReader
        if (!(element instanceof OMSourcedElement) && (!cache || element.isComplete())) {
            OMNode child = element.getFirstOMChild();
            if (child == null) {
                return new StringReader("");
            } else if (child.getNextOMSibling() == null) {
                return new StringReader(child instanceof OMText ? ((OMText)child).getText() : "");
            }
        }
        // In all other cases, extract the data from the XMLStreamReader
        try {
            XMLStreamReader reader = element.getXMLStreamReader(cache);
            if (reader.getEventType() == XMLStreamReader.START_DOCUMENT) {
                reader.next();
            }
            return XMLStreamReaderUtils.getElementTextAsStream(reader, true);
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }
    
    public static void writeTextTo(OMElement element, Writer out, boolean cache) throws IOException {
        try {
            XMLStreamReader reader = element.getXMLStreamReader(cache);
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
    
    public static void discard(IElement that) {
        if (that.getState() == IParentNode.INCOMPLETE && that.getBuilder() != null) {
            ((StAXOMBuilder)that.getBuilder()).discard((OMContainer)that);
        }
        that.detach();
    }
}
