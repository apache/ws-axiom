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

import org.apache.axiom.attachments.utils.DataHandlerUtils;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMNamespaceImpl;
import org.apache.axiom.om.impl.builder.XOPBuilder;
import org.apache.axiom.om.util.TextHelper;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axiom.util.stax.XMLStreamWriterUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;

public abstract class TextNodeImpl extends CharacterImpl implements Text, OMText {
    private String mimeType;

    private boolean optimize;

    private boolean isBinary;

    private String contentID = null;

    protected OMNamespace textNS = null;

    protected char[] charArray;

    /**
     * Field dataHandler contains the DataHandler. Declaring as Object to remove the dependency on
     * Javax.activation.DataHandler
     */
    private Object dataHandlerObject = null;

    /** Field nameSpace is used when serializing Binary stuff as MTOM optimized. */
    protected OMNamespace ns = null;

    /** Field localName is used when serializing Binary stuff as MTOM optimized. */
    protected String localName = "Include";

    /** Field attribute is used when serializing Binary stuff as MTOM optimized. */
    protected OMAttribute attribute;

    /** Field nameSpace used when serializing Binary stuff as MTOM optimized. */
    public static final OMNamespace XOP_NS = new OMNamespaceImpl(
            "http://www.w3.org/2004/08/xop/include", "xop");

    /**
     * Creates a text node with the given text required by the OMDOMFactory. The owner document
     * should be set properly when appending this to a DOM tree.
     *
     * @param text
     */
    public TextNodeImpl(String text, OMFactory factory) {
        super(factory);
        this.textValue = (text != null) ? new StringBuffer(text)
                : new StringBuffer("");
        this.done = true;
        this.ns = XOP_NS;
    }

    /**
     * @param contentID
     * @param parent
     * @param builder   Used when the builder is encountered with a XOP:Include tag Stores a
     *                  reference to the builder and the content-id. Supports deffered parsing of
     *                  MIME messages
     */
    public TextNodeImpl(String contentID, OMContainer parent,
                    OMXMLParserWrapper builder, OMFactory factory) {
        super((DocumentImpl) ((ParentNode) parent).getOwnerDocument(), factory);
        this.contentID = contentID;
        this.optimize = true;
        this.isBinary = true;
        this.done = true;
        this.builder = builder;
        this.ns = XOP_NS;
    }
    
    /**
     * Construct TextImpl that is a copy of the source OMTextImpl
     * @param parent
     * @param source TextImpl
     * @param factory
     */
    public TextNodeImpl(OMContainer parent, TextNodeImpl source, OMFactory factory) {
        super((DocumentImpl) ((ParentNode) parent).getOwnerDocument(), factory);
        this.done = true;
        
        // Copy the value of the text
        if (source.textValue != null) {
            this.textValue = new StringBuffer();
            this.textValue.append(source.textValue.toString());
        }
        
        // Clone the charArray (if it exists)
        if (source.charArray != null) {
            this.charArray = new char[source.charArray.length];
            for (int i=0; i<source.charArray.length; i++) {
                this.charArray[i] = source.charArray[i];
            }
        }
        
        
        // Turn off textNS...the namespace will need to be recalculated
        // in the new tree's context.
        this.textNS = null;
        
        // Copy the optimized related settings.
        this.optimize = source.optimize;
        this.mimeType = source.mimeType;
        this.isBinary = source.isBinary;
        
        // TODO
        // Do we need a deep copy of the data-handler 
        this.contentID = source.contentID;
        this.dataHandlerObject = source.dataHandlerObject;
        
        this.localName = source.localName;
        if (source.ns != null) {
            this.ns = new OMNamespaceImpl(source.ns.getNamespaceURI(), 
                                          source.ns.getPrefix());
        }
        if (source.attribute != null) {
            this.attribute = factory.createOMAttribute(source.attribute.getLocalName(),
                                                       source.attribute.getNamespace(),
                                                       source.attribute.getAttributeValue());
        }
    }

    public TextNodeImpl(String text, String mimeType, boolean optimize,
                    OMFactory factory) {
        this(text, mimeType, optimize, true, factory);
    }

    public TextNodeImpl(String text, String mimeType, boolean optimize,
                    boolean isBinary, OMFactory factory) {
        this(text, factory);
        this.mimeType = mimeType;
        this.optimize = optimize;
        this.isBinary = isBinary;
    }

    /**
     * @param dataHandler
     * @param optimize    To send binary content. Created progrmatically.
     */
    public TextNodeImpl(DocumentImpl ownerNode, Object dataHandler, boolean optimize,
                    OMFactory factory) {
        super(ownerNode, factory);
        this.dataHandlerObject = dataHandler;
        this.isBinary = true;
        this.optimize = optimize;
        done = true;
        this.ns = XOP_NS;
    }

    /**
     * Constructor.
     * 
     * @param contentID
     * @param dataHandlerProvider
     * @param optimize
     * @param factory
     */
    public TextNodeImpl(DocumentImpl ownerNode, String contentID, DataHandlerProvider
            dataHandlerProvider, boolean optimize, OMFactory factory) {
        super(ownerNode, factory);
        this.contentID = contentID;
        dataHandlerObject = dataHandlerProvider;
        isBinary = true;
        this.optimize = optimize;
        done = true;
    }

    /** @param ownerNode  */
    public TextNodeImpl(DocumentImpl ownerNode, OMFactory factory) {
        super(ownerNode, factory);
        this.done = true;
        this.ns = XOP_NS;
    }

    /**
     * @param ownerNode
     * @param value
     */
    public TextNodeImpl(DocumentImpl ownerNode, String value, OMFactory factory) {
        super(ownerNode, value, factory);
        this.done = true;
        this.ns = XOP_NS;
    }


    public TextNodeImpl(DocumentImpl ownerNode, char[] value, OMFactory factory) {
        super(ownerNode, factory);
        this.charArray = value;
        this.done = true;
        this.ns = XOP_NS;
    }

    /**
     * @param ownerNode
     * @param value
     */
    public TextNodeImpl(DocumentImpl ownerNode, String value, String mimeType,
                    boolean optimize, OMFactory factory) {
        this(ownerNode, value, factory);
        this.mimeType = mimeType;
        this.optimize = optimize;
        this.isBinary = true;
        done = true;
    }

    public TextNodeImpl(OMContainer parent, QName text, OMFactory factory) {
        this(parent, text, OMNode.TEXT_NODE, factory);

    }

    public TextNodeImpl(OMContainer parent, QName text, int nodeType,
                    OMFactory factory) {
        this(((ElementImpl) parent).ownerNode, factory);
        if (text != null) {
            this.textNS =
                    ((ElementImpl) parent).findNamespace(text.getNamespaceURI(), text.getPrefix());
        } else {

        }
        this.textValue = new StringBuffer((text == null) ? "" : text.getLocalPart());
        this.done = true;
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
        if (this.isReadonly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                   DOMMessageFormatter.formatMessage(
                                           DOMMessageFormatter.DOM_DOMAIN,
                                           "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (offset < 0 || offset > this.textValue.length()) {
            throw new DOMException(DOMException.INDEX_SIZE_ERR,
                                   DOMMessageFormatter.formatMessage(
                                           DOMMessageFormatter.DOM_DOMAIN, "INDEX_SIZE_ERR",
                                           null));
        }
        String newValue = this.textValue.substring(offset);
        this.deleteData(offset, this.textValue.length());

        TextImpl newText = (TextImpl) this.getOwnerDocument().createTextNode(
                newValue);

        if (this.parentNode != null) {
            newText.setParent(this.parentNode);
        }

        this.insertSiblingAfter(newText);

        return newText;
    }

    // /
    // /org.w3c.dom.Node methods
    // /
    public String getNodeName() {
        return "#text";
    }

    public short getNodeType() {
        return Node.TEXT_NODE;
    }

    // /
    // /OMNode methods
    // /

    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        internalSerializeLocal(writer);
    }

    public void internalSerializeAndConsume(XMLStreamWriter writer)
            throws XMLStreamException {
        internalSerializeLocal(writer);
    }

    public boolean isOptimized() {
        return this.optimize;
    }

    public void setOptimize(boolean value) {
        this.optimize = value;
        if (value) {
            isBinary = true;
        }
    }

    public void discard() throws OMException {
        if (done) {
            this.detach();
        }
    }

    /**
     * Writes the relevant output.
     *
     * @param writer
     * @throws XMLStreamException
     */
    private void writeOutput(XMLStreamWriter writer) throws XMLStreamException {
        int type = getType();
        if (type == OMNode.TEXT_NODE || type == SPACE_NODE) {
            writer.writeCharacters(this.getText());
        } else if (type == OMNode.CDATA_SECTION_NODE) {
            writer.writeCData(this.getText());
        } else if (type == OMNode.ENTITY_REFERENCE_NODE) {
            writer.writeEntityRef(this.getText());
        }
    }

    public String getText() {
        if (this.textNS != null) {
            return getTextString();
        } else if (this.charArray != null || this.textValue != null) {
            return getTextFromProperPlace();
        } else {
            try {
                return Base64Utils.encode((DataHandler)getDataHandler());
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
    }

    public String getData() throws DOMException {
        return this.getText();
    }
    
    public char[] getTextCharacters() {
        return charArray != null ? charArray : this.textValue.toString()
                .toCharArray();
    }

    public boolean isCharacters() {
        return charArray != null;
    }

    private String getTextFromProperPlace() {
        return charArray != null ? new String(charArray) : textValue.toString();
    }

    private String getTextString() {
        if (textNS != null) {
            String prefix = textNS.getPrefix();
            if (prefix == null || "".equals(prefix)) {
                return getTextFromProperPlace();
            } else {
                return prefix + ":" + getTextFromProperPlace();
            }
        }

        return null;
    }

    public QName getTextAsQName() {
        if (textNS != null) {
            String prefix = textNS.getPrefix();
            String name = textNS.getNamespaceURI();
            if (prefix == null || "".equals(prefix)) {
                return new QName(name, getTextFromProperPlace());
            } else {
                return new QName(textNS.getNamespaceURI(), getTextFromProperPlace(), prefix);
            }
        } else if (this.textValue != null || charArray != null) {
            return new QName(getTextFromProperPlace());
        } else {
            try {
                // TODO: do we really want to build a QName from base64 encoded data?!?
                return new QName(Base64Utils.encode((DataHandler)getDataHandler()));
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
    }

    public String getNodeValue() throws DOMException {
        return this.getText();
    }

    public String getContentID() {
        if (contentID == null) {
            contentID = UUIDGenerator.getUUID() + "@apache.org";
        }
        return this.contentID;
    }

    public Object getDataHandler() {
        /*
         * this should return a DataHandler containing the binary data
         * reperesented by the Base64 strings stored in OMText
         */
        if ((textValue != null || charArray != null || textNS != null) & isBinary) {
            String text = textNS == null ? getTextFromProperPlace() : getTextString();
            return DataHandlerUtils
                    .getDataHandlerFromText(text, mimeType);
        } else {

            if (dataHandlerObject == null) {
                if (contentID == null) {
                    throw new RuntimeException("ContentID is null");
                }
                dataHandlerObject = ((XOPBuilder) builder)
                        .getDataHandler(contentID);
            } else if (dataHandlerObject instanceof DataHandlerProvider) {
                try {
                    dataHandlerObject = ((DataHandlerProvider)dataHandlerObject).getDataHandler();
                } catch (XMLStreamException ex) {
                    throw new OMException(ex);
                }
            }
            return dataHandlerObject;
        }
    }

    public java.io.InputStream getInputStream() throws OMException {
        if (isBinary) {
            if (dataHandlerObject == null) {
                getDataHandler();
            }
            InputStream inStream;
            javax.activation.DataHandler dataHandler =
                    (javax.activation.DataHandler) dataHandlerObject;
            try {
                inStream = dataHandler.getDataSource().getInputStream();
            } catch (IOException e) {
                throw new OMException(
                        "Cannot get InputStream from DataHandler.", e);
            }
            return inStream;
        } else {
            throw new OMException("Unsupported Operation");
        }
    }

    private void internalSerializeLocal(XMLStreamWriter writer2)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = (MTOMXMLStreamWriter) writer2;
        if (!this.isBinary) {
            writeOutput(writer);
        } else {
            if (writer.isOptimized()) {
                if (contentID == null) {
                    contentID = writer.getNextContentId();
                }
                // send binary as MTOM optimised
                this.attribute = new AttrImpl(this.ownerNode, "href",
                                              new NamespaceImpl("", ""),
                                              "cid:" + getContentID(),
                                              this.factory);
                this.serializeStartpart(writer);
                writer.writeOptimized(this);
                writer.writeEndElement();
            } else {
                try {
                    XMLStreamWriterUtil.writeBase64(writer, (DataHandler)getDataHandler());
                } catch (IOException ex) {
                    throw new OMException("Error reading data handler", ex);
                }
            }
        }
    }

    /*
     * Methods to copy from OMSerialize utils.
     */
    private void serializeStartpart(XMLStreamWriter writer)
            throws XMLStreamException {
        String nameSpaceName = XOP_NS.getNamespaceURI();
        String writer_prefix = writer.getPrefix(nameSpaceName);
        String prefix = XOP_NS.getPrefix();
        if (writer_prefix != null) {
            writer.writeStartElement(nameSpaceName, this
                    .getLocalName());
        } else {
            writer.writeStartElement(prefix, this.getLocalName(),
                                     nameSpaceName);
            writer.setPrefix(prefix, nameSpaceName);
        }
        // add the elements attribute "href"
        serializeAttribute(this.attribute, writer);
        // add the namespace
        serializeNamespace(XOP_NS, writer);
    }

    /**
     * Method serializeAttribute.
     *
     * @param attr
     * @param writer
     * @throws XMLStreamException
     */
    static void serializeAttribute(OMAttribute attr, XMLStreamWriter writer)
            throws XMLStreamException {
        // first check whether the attribute is associated with a namespace
        OMNamespace ns = attr.getNamespace();
        String prefix;
        String namespaceName;
        if (ns != null) {
            // add the prefix if it's availble
            prefix = ns.getPrefix();
            namespaceName = ns.getNamespaceURI();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName, attr
                        .getLocalName(), attr.getAttributeValue());
            } else {
                writer.writeAttribute(namespaceName, attr.getLocalName(), attr
                        .getAttributeValue());
            }
        } else {
            writer
                    .writeAttribute(attr.getLocalName(), attr
                            .getAttributeValue());
        }
    }

    /**
     * Method serializeNamespace.
     *
     * @param namespace
     * @param writer
     * @throws XMLStreamException
     */
    static void serializeNamespace(OMNamespace namespace, XMLStreamWriter writer)
            throws XMLStreamException {
        if (namespace != null) {
            String uri = namespace.getNamespaceURI();
            String ns_prefix = namespace.getPrefix();
            writer.writeNamespace(ns_prefix, namespace.getNamespaceURI());
            writer.setPrefix(ns_prefix, uri);
        }
    }

    public String getLocalName() {
        return this.localName;
    }

    /*
    * DOM-Level 3 methods
    */


    public String getWholeText() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isElementContentWhitespace() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public Text replaceWholeText(String arg0) throws DOMException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public String toString() {
        return (this.textValue != null) ? textValue.toString() : "";
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMNode#buildAll()
      */
    public void buildWithAttachments() {
        this.build();
        if (isOptimized()) {
            this.getDataHandler();
        }
    }

    public boolean isBinary() {
        return isBinary;
    }

    /**
     * Receiving binary can happen as either MTOM attachments or as Base64 Text In the case of Base64
     * user has to explicitly specify that the content is binary, before calling getDataHandler(),
     * getInputStream()....
     */
    public void setBinary(boolean value) {
        this.isBinary = value;

    }

    public OMNamespace getNamespace() {
        return textNS;
    }
    
    public void setContentID(String cid) {
        this.contentID = cid;
    }
}
