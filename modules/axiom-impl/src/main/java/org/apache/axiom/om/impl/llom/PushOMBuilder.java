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

import java.io.IOException;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

public class PushOMBuilder extends AbstractXMLStreamWriter implements DataHandlerWriter {
    private final OMSourcedElementImpl root;
    private final OMFactory factory;
    private OMElement parent;
    
    public PushOMBuilder(OMSourcedElementImpl root) throws XMLStreamException {
        this.root = root;
        factory = root.getOMFactory();
        // Seed the namespace context with the namespace context from the parent
        OMContainer parent = root.getParent();
        if (parent instanceof OMElement) {
            for (Iterator it = ((OMElement)parent).getNamespacesInScope(); it.hasNext(); ) {
                OMNamespace ns = (OMNamespace)it.next();
                setPrefix(ns.getPrefix(), ns.getNamespaceURI());
            }
        }
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        } else {
            throw new IllegalArgumentException("Unsupported property " + name);
        }
    }

    protected void doWriteStartDocument() {
        // Do nothing
    }

    protected void doWriteStartDocument(String encoding, String version) {
        // Do nothing
    }

    protected void doWriteStartDocument(String version) {
        // Do nothing
    }

    protected void doWriteEndDocument() {
        // Do nothing
    }

    protected void doWriteDTD(String dtd) throws XMLStreamException {
        throw new XMLStreamException("A DTD must not appear in element content");
    }

    private OMNamespace getOMNamespace(String prefix, String namespaceURI, boolean isDecl) {
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (!isDecl && namespaceURI.length() == 0) {
            return null;
        } else {
            if (parent != null) {
                // If possible, locate an existing OMNamespace object
                OMNamespace ns = parent.findNamespaceURI(prefix);
                if (ns != null && ns.getNamespaceURI().equals(namespaceURI)) {
                    return ns;
                }
            }
            return factory.createOMNamespace(namespaceURI, prefix);
        }
    }
    
    protected void doWriteStartElement(String prefix, String localName, String namespaceURI) {
        // Get the OMNamespace object before we change the parent
        OMNamespace ns = getOMNamespace(prefix, namespaceURI, false);
        if (parent == null) {
            root.validateName(prefix, localName, namespaceURI);
            parent = root;
        } else {
            // We use the createOMElement variant that takes a OMXMLParserWrapper parameter and
            // don't pass the namespace. This avoids creation of a namespace declaration.
            parent = factory.createOMElement(localName, null, parent, null);
        }
        if (ns != null) {
            parent.setNamespaceWithNoFindInCurrentScope(ns);
        }
    }

    protected void doWriteStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartElement(String)");
    }

    protected void doWriteEndElement() {
        if (parent == root) {
            parent = null;
        } else {
            // Since we use the createOMElement variant that takes a OMXMLParserWrapper parameter,
            // we need to update the completion status.
            ((OMContainerEx)parent).setComplete(true);
            parent = (OMElement)parent.getParent();
        }
    }

    protected void doWriteEmptyElement(String prefix, String localName, String namespaceURI) {
        doWriteStartElement(prefix, localName, namespaceURI);
        doWriteEndElement();
    }

    protected void doWriteEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEmptyElement(String)");
    }

    protected void doWriteAttribute(String prefix, String namespaceURI, String localName, String value) {
        OMAttribute attr = factory.createOMAttribute(localName, getOMNamespace(prefix, namespaceURI, false), value);
        // Use the internal appendAttribute method instead of addAttribute in order to avoid
        // automatic of a namespace declaration (the OMDataSource is required to produce well formed
        // XML with respect to namespaces, so it will take care of the namespace declarations).
        ((OMElementImpl)parent).appendAttribute(attr);
    }

    protected void doWriteAttribute(String localName, String value) throws XMLStreamException {
        doWriteAttribute(null, null, localName, value);
    }

    protected void doWriteNamespace(String prefix, String namespaceURI) {
        ((OMElementImpl)parent).addNamespaceDeclaration(getOMNamespace(prefix, namespaceURI, true));
    }

    protected void doWriteDefaultNamespace(String namespaceURI) {
        doWriteNamespace(null, namespaceURI);
    }

    protected void doWriteCharacters(char[] text, int start, int len) {
        doWriteCharacters(new String(text, start, len));
    }

    protected void doWriteCharacters(String text) {
        factory.createOMText(parent, text);
    }

    protected void doWriteCData(String data) {
        factory.createOMText(parent, data, OMNode.CDATA_SECTION_NODE);
    }

    protected void doWriteComment(String data) {
        factory.createOMComment(parent, data);
    }

    protected void doWriteEntityRef(String name) throws XMLStreamException {
        // TODO: this is equivalent to what StAXOMBuilder does; however, it doesn't look correct
        factory.createOMText(parent, name, OMNode.ENTITY_REFERENCE_NODE);
    }

    protected void doWriteProcessingInstruction(String target, String data) {
        factory.createOMProcessingInstruction(parent, target, data);
    }

    protected void doWriteProcessingInstruction(String target) {
        doWriteProcessingInstruction(target, "");
    }

    public void flush() throws XMLStreamException {
        // Do nothing
    }

    public void close() throws XMLStreamException {
        // Do nothing
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        OMText child = factory.createOMText(dataHandler, optimize);
        if (contentID != null) {
            child.setContentID(contentID);
        }
        parent.addChild(child);
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID,
            boolean optimize) throws IOException, XMLStreamException {
        parent.addChild(factory.createOMText(contentID, dataHandlerProvider, optimize));
    }
}
