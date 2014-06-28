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
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.base64.Base64Utils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.io.IOException;

public abstract class TextNodeImpl extends CharacterImpl implements Text, OMText {
    private String mimeType;

    private boolean optimize;

    private boolean isBinary;

    private String contentID;

    protected char[] charArray;

    /**
     * Contains a {@link DataHandler} or {@link DataHandlerProvider} object if the text node
     * represents base64 encoded binary data.
     */
    private Object dataHandlerObject;

    /**
     * Creates a text node with the given text required by the OMDOMFactory. The owner document
     * should be set properly when appending this to a DOM tree.
     *
     * @param text
     */
    public TextNodeImpl(String text, OMFactory factory) {
        super(factory);
        //this.textValue = (text != null) ? new StringBuffer(text)
        //        : new StringBuffer("");
        this.textValue = (text != null) ? text : "";
    }

    /**
     * Construct TextImpl that is a copy of the source OMTextImpl
     *
     * @param parent
     * @param source  TextImpl
     * @param factory
     */
    public TextNodeImpl(TextNodeImpl source, OMFactory factory) {
        super(factory);

        // Copy the value of the text
        if (source.textValue != null) {
            this.textValue = source.textValue;
        }

        // Clone the charArray (if it exists)
        if (source.charArray != null) {
            this.charArray = new char[source.charArray.length];
            for (int i = 0; i < source.charArray.length; i++) {
                this.charArray[i] = source.charArray[i];
            }
        }

        // Copy the optimized related settings.
        this.optimize = source.optimize;
        this.mimeType = source.mimeType;
        this.isBinary = source.isBinary;

        // TODO
        // Do we need a deep copy of the data-handler 
        this.contentID = source.contentID;
        this.dataHandlerObject = source.dataHandlerObject;
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
    public TextNodeImpl(Object dataHandler, boolean optimize,
                        OMFactory factory) {
        super(factory);
        this.dataHandlerObject = dataHandler;
        this.isBinary = true;
        this.optimize = optimize;
    }

    /**
     * Constructor.
     *
     * @param contentID
     * @param dataHandlerProvider
     * @param optimize
     * @param factory
     */
    public TextNodeImpl(String contentID, DataHandlerProvider
            dataHandlerProvider, boolean optimize, OMFactory factory) {
        super(factory);
        this.contentID = contentID;
        dataHandlerObject = dataHandlerProvider;
        isBinary = true;
        this.optimize = optimize;
    }

    /**
     * @param ownerNode
     */
    public TextNodeImpl(OMFactory factory) {
        super(factory);
    }

    public TextNodeImpl(char[] value, OMFactory factory) {
        super(factory);
        this.charArray = value;
    }

    public TextNodeImpl(OMContainer parent, QName text, OMFactory factory) {
        this(parent, text, OMNode.TEXT_NODE, factory);

    }

    public TextNodeImpl(OMContainer parent, QName text, int nodeType,
                        OMFactory factory) {
        this(factory);
        OMNamespace textNS =
                ((ElementImpl) parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        this.textValue = textNS == null ? text.getLocalPart() : textNS.getPrefix() + ":" + text.getLocalPart();
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
        if (offset < 0 || offset > this.textValue.length()) {
            throw DOMUtil.newDOMException(DOMException.INDEX_SIZE_ERR);
        }
        String newValue = this.textValue.substring(offset);
        this.deleteData(offset, this.textValue.length());

        TextImpl newText = (TextImpl) this.getOwnerDocument().createTextNode(
                newValue);

        ParentNode parentNode = parentNode();
        if (parentNode != null) {
            newText.setParent((OMContainer)parentNode);
        }

        this.insertSiblingAfter(newText);

        return newText;
    }

    // /
    // /OMNode methods
    // /

    public boolean isOptimized() {
        return this.optimize;
    }

    public void setOptimize(boolean value) {
        this.optimize = value;
        if (value) {
            isBinary = true;
        }
    }

    public String getText() {
        if (this.charArray != null || this.textValue != null) {
            return getTextFromProperPlace();
        } else {
            try {
                return Base64Utils.encode((DataHandler) getDataHandler());
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
    }

    public String getData() throws DOMException {
        return this.getText();
    }

    public char[] getTextCharacters() {
        if (charArray != null) {
            return charArray;
        } else if (textValue != null) {
            return textValue.toCharArray();
        } else {
            try {
                return Base64Utils.encodeToCharArray((DataHandler)getDataHandler());
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        }
    }

    public boolean isCharacters() {
        return charArray != null;
    }

    private String getTextFromProperPlace() {
        return charArray != null ? new String(charArray) : textValue;
    }

    public QName getTextAsQName() {
        return ((OMElement)parentNode()).resolveQName(getTextFromProperPlace());
    }

    public String getContentID() {
        if (contentID == null) {
            contentID = UIDGenerator.generateContentId();
        }
        return this.contentID;
    }

    public Object getDataHandler() {
        /*
         * this should return a DataHandler containing the binary data
         * reperesented by the Base64 strings stored in OMText
         */
        if ((textValue != null || charArray != null) & isBinary) {
            return DataHandlerUtils.getDataHandlerFromText(getTextFromProperPlace(), mimeType);
        } else {

            if (dataHandlerObject == null) {
                throw new OMException("No DataHandler available");
            } else if (dataHandlerObject instanceof DataHandlerProvider) {
                try {
                    dataHandlerObject = ((DataHandlerProvider) dataHandlerObject).getDataHandler();
                } catch (IOException ex) {
                    throw new OMException(ex);
                }
            }
            return dataHandlerObject;
        }
    }

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        if (!this.isBinary) {
            serializer.writeText(getType(), getText());
        } else if (dataHandlerObject instanceof DataHandlerProvider) {
            serializer.writeDataHandler((DataHandlerProvider)dataHandlerObject, contentID, optimize);
        } else {
            serializer.writeDataHandler((DataHandler)getDataHandler(), contentID, optimize);
        }
    }

    /*
    * DOM-Level 3 methods
    */

    public String getWholeText() {
        // Locate the first sibling that we need to include in the concatenation
        OMNode sibling = this;
        OMNode previousSibling;
        while ((previousSibling = sibling.getPreviousOMSibling()) instanceof OMText) {
            sibling = previousSibling;
        }
        
        String text = ((OMText)sibling).getText();
        StringBuffer buffer = null;
        while ((sibling = sibling.getNextOMSibling()) instanceof OMText) {
            if (buffer == null) {
                buffer = new StringBuffer(text);
            }
            buffer.append(((OMText)sibling).getText());
        }

        if (buffer != null) {
            return buffer.toString();
        } else {
            return text;
        }
    }

    public boolean isElementContentWhitespace() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public Text replaceWholeText(String content) throws DOMException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public String toString() {
        return (this.textValue != null) ? textValue : "";
    }

    public void buildWithAttachments() {
        this.build();
        if (isOptimized()) {
            // The call to getDataSource ensures that the MIME part is completely read
            ((DataHandler)this.getDataHandler()).getDataSource();
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
        // Note: efficiency is not important here; the method is deprecated anyway
        QName qname = getTextAsQName();
        if (qname == null) {
            return null;
        } else {
            String namespaceURI = qname.getNamespaceURI();
            return namespaceURI.length() == 0 ? null : new OMNamespaceImpl(namespaceURI, qname.getPrefix());
        }
    }

    public void setContentID(String cid) {
        this.contentID = cid;
    }

    void beforeClone(OMCloneOptions options) {
        if (isBinary && options.isFetchDataHandlers()) {
            // Force loading of the reference to the DataHandler and ensure that its content is
            // completely fetched into memory (or temporary storage).
            ((DataHandler)getDataHandler()).getDataSource();
        }
    }
}
