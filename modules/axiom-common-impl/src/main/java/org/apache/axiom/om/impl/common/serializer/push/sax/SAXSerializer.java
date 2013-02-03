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
package org.apache.axiom.om.impl.common.serializer.push.sax;

import javax.activation.DataHandler;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SAXSerializer extends Serializer {
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    private final ScopedNamespaceContext nsContext = new ScopedNamespaceContext();
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private final AttributesImpl attributes = new AttributesImpl();
    
    public SAXSerializer(OMSerializable contextNode, ContentHandler contentHandler, LexicalHandler lexicalHandler) {
        super(contextNode);
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }

    protected boolean isAssociated(String prefix, String namespace) throws OutputException {
        return nsContext.getNamespaceURI(prefix).equals(namespace);
    }

    protected void setPrefix(String prefix, String namespaceURI) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartDocument(String version) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeStartDocument(String encoding, String version) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeDTD(String rootName, String publicId, String systemId, String internalSubset)
            throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void beginStartElement(String prefix, String namespaceURI, String localName) throws OutputException {
        elementURI = namespaceURI;
        elementLocalName = localName;
        if (prefix.length() == 0) {
            elementQName = localName;
        } else {
            elementQName = prefix + ":" + localName;
        }
    }

    protected void writeNamespace(String prefix, String namespaceURI) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void finishStartElement() throws OutputException {
        try {
            contentHandler.startElement(elementURI, elementLocalName, elementQName, attributes);
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
        elementURI = null;
        elementLocalName = null;
        elementQName = null;
        attributes.clear();
    }

    public void writeEndElement() throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeText(int type, String data) throws OutputException {
        char[] ch = data.toCharArray();
        try {
            switch (type) {
                case OMNode.TEXT_NODE:
                    contentHandler.characters(ch, 0, ch.length);
                    break;
                case OMNode.CDATA_SECTION_NODE:
                    if (lexicalHandler != null) {
                        lexicalHandler.startCDATA();
                    }
                    contentHandler.characters(ch, 0, ch.length);
                    if (lexicalHandler != null) {
                        lexicalHandler.endCDATA();
                    }
                    break;
                case OMNode.SPACE_NODE:
                    contentHandler.ignorableWhitespace(ch, 0, ch.length);
            }
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }

    public void writeComment(String data) throws OutputException {
        if (lexicalHandler != null) {
            char[] ch = data.toCharArray();
            try {
                lexicalHandler.comment(ch, 0, ch.length);
            } catch (SAXException ex) {
                throw new SAXOutputException(ex);
            }
        }
    }

    public void writeProcessingInstruction(String target, String data) throws OutputException {
        try {
            contentHandler.processingInstruction(target, data);
        } catch (SAXException ex) {
            throw new SAXOutputException(ex);
        }
    }

    public void writeEntityRef(String name) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize)
            throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID,
            boolean optimize) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void serializePushOMDataSource(OMDataSource dataSource) throws OutputException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
