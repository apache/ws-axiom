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
package org.apache.axiom.om.impl.common.serializer.push.stax;

import java.io.IOException;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.serializer.push.SerializerImpl;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StAXSerializer extends SerializerImpl {
    private static final Log log = LogFactory.getLog(StAXSerializer.class);
    
    private final XMLStreamWriter writer;
    private DataHandlerWriter dataHandlerWriter;
    
    public StAXSerializer(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public XMLStreamWriter getWriter() {
        return writer;
    }

    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            boolean standalone) throws StreamException {
        try {
            if (xmlEncoding == null) {
                writer.writeStartDocument(xmlVersion);
            } else {
                writer.writeStartDocument(xmlEncoding, xmlVersion);
            }
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        try {
            XMLStreamWriterUtils.writeDTD(writer, rootName, publicId, systemId, internalSubset);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        try {
            writer.writeStartElement(prefix, localName, namespaceURI);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        try {
            if (prefix.length() != 0) {
                writer.writeNamespace(prefix, namespaceURI);
            } else {
                writer.writeDefaultNamespace(namespaceURI);
            }
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        try {
            writer.writeAttribute(prefix, namespaceURI, localName, value);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void attributesCompleted() throws StreamException {
        // Nothing to do here
    }

    /**
     * @param prefix 
     * @param namespace
     * @return true if the prefix is associated with the namespace in the current context
     */
    protected boolean isAssociated(String prefix, String namespace) throws StreamException {
        try {
            // The "xml" prefix is always (implicitly) associated. Returning true here makes sure that
            // we never write a declaration for the xml namespace. See AXIOM-37 for a discussion
            // of this issue.
            if ("xml".equals(prefix)) {
                return true;
            }
            
            // NOTE: Calling getNamespaceContext() on many XMLStreamWriter implementations is expensive.
            // Please use other writer methods first.
            
            // For consistency, convert null arguments.
            // This helps get around the parser implementation differences.
            // In addition, the getPrefix/getNamespace methods cannot be called with null parameters.
            prefix = (prefix == null) ? "" : prefix;
            namespace = (namespace == null) ? "" : namespace;
            
            if (namespace.length() > 0) {
                // QUALIFIED NAMESPACE
                // Get the namespace associated with the prefix
                String writerPrefix = writer.getPrefix(namespace);
                if (prefix.equals(writerPrefix)) {
                    return true;
                }
                
                // It is possible that the namespace is associated with multiple prefixes,
                // So try getting the namespace as a second step.
                if (writerPrefix != null) {
                    NamespaceContext nsContext = writer.getNamespaceContext();
                    if(nsContext != null) {
                        String writerNS = nsContext.getNamespaceURI(prefix);
                        return namespace.equals(writerNS);
                    }
                }
                return false;
            } else {
                // UNQUALIFIED NAMESPACE
                
                // Neither XML 1.0 nor XML 1.1 allow to associate a prefix with an unqualified name (see also AXIOM-372).
                if (prefix.length() > 0) {
                    throw new OMException("Invalid namespace declaration: Prefixed namespace bindings may not be empty.");  
                }
                
                // Get the namespace associated with the prefix.
                // It is illegal to call getPrefix with null, but the specification is not
                // clear on what happens if called with "".  So the following code is 
                // protected
                try {
                    String writerPrefix = writer.getPrefix("");
                    if (writerPrefix != null && writerPrefix.length() == 0) {
                        return true;
                    }
                } catch (Throwable t) {
                    if (log.isDebugEnabled()) {
                        log.debug("Caught exception from getPrefix(\"\"). Processing continues: " + t);
                    }
                }
                
                
                
                // Fallback to using the namespace context
                NamespaceContext nsContext = writer.getNamespaceContext();
                if (nsContext != null) {
                    String writerNS = nsContext.getNamespaceURI("");
                    if (writerNS != null && writerNS.length() > 0) {
                        return false;
                    }
                }
                return true;
            }
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void endElement() throws StreamException {
        try {
            writer.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        try {
            if (data instanceof TextContent) {
                TextContent textContent = (TextContent)data;
                if (textContent.isBinary()) {
                    Object dataHandlerObject = textContent.getDataHandlerObject();
                    if (dataHandlerObject instanceof DataHandlerProvider) {
                        getDataHandlerWriter().writeDataHandler((DataHandlerProvider)dataHandlerObject, textContent.getContentID(), textContent.isOptimize());
                    } else {
                        getDataHandlerWriter().writeDataHandler(textContent.getDataHandler(), textContent.getContentID(), textContent.isOptimize());
                    }
                    return;
                }
            }
            writer.writeCharacters(data.toString());
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }
    
    @Override
    public void processCDATASection(String content) throws StreamException {
        try {
            writer.writeCData(content);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processComment(String data) throws StreamException {
        try {
            writer.writeComment(data);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processProcessingInstruction(String target, String data) throws StreamException {
        try {
            writer.writeProcessingInstruction(target, data);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    public void processEntityReference(String name, String replacementText) throws StreamException {
        try {
            writer.writeEntityRef(name);
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
    }

    private DataHandlerWriter getDataHandlerWriter() {
        // We only retrieve/create the DataHandlerWriter if necessary
        if (dataHandlerWriter == null) {
            dataHandlerWriter = XMLStreamWriterUtils.getDataHandlerWriter(writer);
        }
        return dataHandlerWriter;
    }

    public void endDocument() throws StreamException {
        // TODO: the original StAX serialization code newer called writeEndDocument; this is probably a mistake
    }
}
