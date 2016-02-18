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
package org.apache.axiom.om.impl.common.serializer.push;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.om.impl.stream.StreamException;
import org.apache.axiom.om.impl.stream.XmlHandler;

public abstract class SerializerImpl implements XmlHandler {
    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the object model subtree that is being serialized; this
     *            information is used by the serializer in scenarios that require access to the
     *            namespace context of the parent of the root node
     * @param namespaceRepairing
     *            indicates if the serializer should perform namespace repairing
     * @param preserveNamespaceContext
     *            indicates if the namespace context determined by the ancestors of the root node
     *            should be strictly preserved in the output
     */
    public XmlHandler buildHandler(OMSerializable root, boolean namespaceRepairing, boolean preserveNamespaceContext) {
        OMElement contextElement;
        if (root instanceof OMNode) {
            OMContainer parent = ((OMNode)root).getParent();
            if (parent instanceof OMElement) {
                contextElement = (OMElement)parent; 
            } else {
                contextElement = null;
            }
        } else {
            contextElement = null;
        }
        XmlHandler handler = this;
        if (contextElement != null) {
            if (preserveNamespaceContext) {
                handler = new NamespaceContextPreservationFilterHandler(handler, (CoreElement)contextElement);
            } else {
                handler = new XsiTypeFilterHandler(handler, (CoreElement)contextElement);
            }
        }
        if (namespaceRepairing) {
            handler = new NamespaceHelper(this, handler);
        }
        return handler;
    }

    public final void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (data instanceof TextContent) {
            TextContent textContent = (TextContent)data;
            if (textContent.isBinary()) {
                Object dataHandlerObject = textContent.getDataHandlerObject();
                if (dataHandlerObject instanceof DataHandlerProvider) {
                    writeDataHandler((DataHandlerProvider)dataHandlerObject, textContent.getContentID(), textContent.isOptimize());
                } else {
                    writeDataHandler(textContent.getDataHandler(), textContent.getContentID(), textContent.isOptimize());
                }
                return;
            }
        }
        writeText(ignorable ? OMNode.SPACE_NODE : OMNode.TEXT_NODE, data.toString());
    }
    
    public final void processCDATASection(String content) throws StreamException {
        writeText(OMNode.CDATA_SECTION_NODE, content.toString());
    }
    
    protected abstract boolean isAssociated(String prefix, String namespace) throws StreamException;
    
    public abstract void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException;
    
    /**
     * Prepare to write an element start tag. A call to this method will be followed by zero or more
     * calls to {@link #processNamespaceDeclaration(String, String)} and
     * {@link #processAttribute(String, String, String, String, String)} and a single call to
     * {@link #attributesCompleted()}.
     * 
     * @param namespaceURI
     *            the namespace URI of the element; never <code>null</code>
     * @param localName
     *            the local name of the element; never <code>null</code>
     * @param prefix
     *            the prefix of the element; never <code>null</code>
     * @throws StreamException
     */
    public abstract void startElement(String namespaceURI, String localName, String prefix) throws StreamException;
    
    /**
     * Add the given namespace to the element. The implementation of this method must take the
     * appropriate actions such that the following two conditions are satisfied:
     * <ul>
     * <li>A namespace declaration is written to the output.
     * <li>The namespace binding defined by the parameters is visible in the namespace context of
     * the {@link XMLStreamWriter} that {@link #serializePushOMDataSource(OMDataSource)} passes to
     * {@link OMDataSource#serialize(XMLStreamWriter)} if an {@link OMDataSource} is serialized in
     * the scope of the current element (and of course unless the namespace binding is hidden by a
     * namespace defined on a nested element).
     * </ul>
     * 
     * @param prefix
     *            the namespace prefix; never <code>null</code>
     * @param namespaceURI
     *            the namespace URI; never <code>null</code>
     * @throws StreamException
     */
    public abstract void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException;
    
    /**
     * Add the given attribute to the element.
     * 
     * @param namespaceURI
     *            the namespace URI or the attribute; never <code>null</code>
     * @param localName
     *            the local name of the attribute; never <code>null</code>
     * @param prefix
     *            the namespace prefix of the attribute; never <code>null</code>
     * @param value
     *            the value of the attribute; never <code>null</code>
     * @param type
     *            the attribute type (e.g. <tt>CDATA</tt>); never <code>null</code>
     * @throws StreamException
     */
    public abstract void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException;
    
    public abstract void attributesCompleted() throws StreamException;
    
    public abstract void endElement() throws StreamException;
    
    protected abstract void writeText(int type, String data) throws StreamException;
    
    public abstract void processComment(String data) throws StreamException;

    public abstract void processProcessingInstruction(String target, String data) throws StreamException;
    
    public abstract void processEntityReference(String name, String replacementText) throws StreamException;
    
    protected abstract void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize) throws StreamException;

    protected abstract void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID, boolean optimize) throws StreamException;

    public abstract void endDocument() throws StreamException;
}
