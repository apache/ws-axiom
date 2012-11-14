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

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

public class OMTextImpl extends OMLeafNode implements OMText, OMConstants {
    /** Field nameSpace used when serializing Binary stuff as MTOM optimized. */
    public static final OMNamespace XOP_NS = new OMNamespaceImpl(
            "http://www.w3.org/2004/08/xop/include", "xop");

    private int nodeType;

    protected String value = null;
    protected char[] charArray;

    private boolean calcNS;  // Set to true after textNS is calculated
    protected OMNamespace textNS;

    protected String mimeType;

    protected boolean optimize = false;

    protected boolean isBinary = false;

    /** Field contentID for the mime part used when serializing Binary stuff as MTOM optimized. */
    private String contentID = null;

    /**
     * Field dataHandler contains the DataHandler Declaring as Object to remove the dependency on
     * Javax.activation.DataHandler
     */
    private Object dataHandlerObject = null;

    private static final String EMTPY_STRING = "";

    /**
     * Constructor OMTextImpl.
     *
     * @param s
     */
    public OMTextImpl(String s, OMFactory factory) {
        this(s, TEXT_NODE, factory);
    }

    /**
     * @param s
     * @param nodeType - OMText can handle CHARACTERS, SPACES, CDATA and ENTITY REFERENCES.
     *                 Constants for this can be found in OMNode.
     */
    public OMTextImpl(String s, int nodeType, OMFactory factory) {
        this(null, s, nodeType, factory, false);
    }

    /**
     * Constructor OMTextImpl.
     *
     * @param parent
     * @param text
     */
    public OMTextImpl(OMContainer parent, String text, OMFactory factory) {
        this(parent, text, TEXT_NODE, factory, false);
    }
    
    /**
     * Construct OMTextImpl that is a copy of the source OMTextImpl
     * @param parent
     * @param source OMTextImpl
     * @param factory
     */
    public OMTextImpl(OMContainer parent, OMTextImpl source, OMFactory factory) {
        super(parent, factory, false);
        // Copy the value of the text
        this.value = source.value;
        this.nodeType = source.nodeType;
        
        // Clone the charArray (if it exists)
        if (source.charArray != null) {
            this.charArray = new char[source.charArray.length];
            System.arraycopy(source.charArray, 0, this.charArray, 0, source.charArray.length);
        }
        
        // Turn off calcNS...the namespace will need to be recalculated
        // in the new tree's context.
        this.calcNS = false;
        this.textNS = null;
        
        // Copy the optimized related settings.
        this.optimize = source.optimize;
        this.mimeType = source.mimeType;
        this.isBinary = source.isBinary;
        
        // TODO
        // Do we need a deep copy of the data-handler 
        this.contentID = source.contentID;
        this.dataHandlerObject = source.dataHandlerObject;
    }

    public OMTextImpl(OMContainer parent, String text, int nodeType,
                      OMFactory factory, boolean fromBuilder) {
        super(parent, factory, fromBuilder);
        this.value = text == null ? EMTPY_STRING : text;
        this.nodeType = nodeType;
    }

    public OMTextImpl(OMContainer parent, char[] charArray, int nodeType,
                      OMFactory factory) {
        super(parent, factory, false);
        this.charArray = charArray;
        this.nodeType = nodeType;
    }


    public OMTextImpl(OMContainer parent, QName text, OMFactory factory) {
        this(parent, text, TEXT_NODE, factory);
    }

    public OMTextImpl(OMContainer parent, QName text, int nodeType,
                      OMFactory factory) {
        super(parent, factory, false);
        if (text == null) throw new IllegalArgumentException("QName text arg cannot be null!");
        this.calcNS = true;
        this.textNS =
                ((OMElementImpl) parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        this.value = textNS == null ? text.getLocalPart() : textNS.getPrefix() + ":" + text.getLocalPart();
        this.nodeType = nodeType;
    }

    /**
     * @param s        - base64 encoded String representation of Binary
     * @param mimeType of the Binary
     */
    public OMTextImpl(String s, String mimeType, boolean optimize,
                      OMFactory factory) {
        this(null, s, mimeType, optimize, factory);
    }

    /**
     * @param parent
     * @param s        - base64 encoded String representation of Binary
     * @param mimeType of the Binary
     */
    public OMTextImpl(OMContainer parent, String s, String mimeType,
                      boolean optimize, OMFactory factory) {
        this(parent, s, factory);
        this.mimeType = mimeType;
        this.optimize = optimize;
        this.isBinary = true;
        this.nodeType = TEXT_NODE;
    }

    /** @param dataHandler To send binary optimised content Created programatically. */
    public OMTextImpl(Object dataHandler, OMFactory factory) {
        this(null, dataHandler, true, factory, false);
    }

    /**
     * @param dataHandler
     * @param optimize    To send binary content. Created progrmatically.
     */
    public OMTextImpl(OMContainer parent, Object dataHandler, boolean optimize, OMFactory factory, boolean fromBuilder) {
        super(parent, factory, fromBuilder);
        this.dataHandlerObject = dataHandler;
        this.isBinary = true;
        this.optimize = optimize;
        this.nodeType = TEXT_NODE;
    }

    /**
     * Constructor.
     * 
     * @param dataHandlerProvider
     * @param optimize
     * @param factory
     */
    public OMTextImpl(String contentID, DataHandlerProvider dataHandlerProvider, boolean optimize,
            OMFactory factory) {
        super(factory);
        this.contentID = contentID;
        dataHandlerObject = dataHandlerProvider;
        isBinary = true;
        this.optimize = optimize;
        nodeType = TEXT_NODE;
    }

    public final int getType() {
        return nodeType;
    }

    /**
     * Writes the relevant output.
     *
     * @param writer
     * @throws XMLStreamException
     */
    private void writeOutput(XMLStreamWriter writer) throws XMLStreamException {
        int type = getType();
        if (type == TEXT_NODE || type == SPACE_NODE) {
            writer.writeCharacters(this.getText());
        } else if (type == CDATA_SECTION_NODE) {
            writer.writeCData(this.getText());
        } else if (type == ENTITY_REFERENCE_NODE) {
            writer.writeEntityRef(this.getText());
        }
    }

    /** Returns the value. */
    public String getText() throws OMException {
        if (charArray != null || this.value != null) {
            return getTextFromProperPlace();
        } else {
            try {
                return Base64Utils.encode((DataHandler)getDataHandler());
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
    }

    public char[] getTextCharacters() {
        if (charArray != null) {
            return charArray;
        } else if (value != null) {
            return value.toCharArray();
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

    /**
     * This OMText contains two data source:value and charArray. This method will return text from
     * correct place.
     */
    private String getTextFromProperPlace() {
        return charArray != null ? new String(charArray) : value;
    }


    /** Returns the value. */
    public QName getTextAsQName() throws OMException {
        return ((OMElement)parent).resolveQName(getTextFromProperPlace());
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMText#getNamespace()
      */
    public OMNamespace getNamespace() {
        // If the namespace has already been determined, return it
        // Otherwise calculate the namespace if the text contains a colon and is not detached.
        if (calcNS) {
            return textNS;
        } else {
            calcNS = true;
            if (getParent() != null) {
                String text = getTextFromProperPlace();
                if (text != null) {
                    int colon = text.indexOf(':');
                    if (colon > 0) {
                        textNS = ((OMElementImpl) getParent()).
                                findNamespaceURI(text.substring(0, colon));
                        if (textNS != null) {
                            charArray = null;
                            value = text.substring(colon + 1);
                        }
                    }
                }
            }
        }
        return textNS;
    }

    public boolean isOptimized() {
        return optimize;
    }

    public void setOptimize(boolean value) {
        this.optimize = value;
        if (value) {
            isBinary = true;
        }
    }

    /**
     * Receiving binary can happen as either MTOM attachments or as Base64 Text In the case of
     * Base64 user has to explicitly specify that the content is binary, before calling
     * getDataHandler(), getInputStream()....
     */
    public void setBinary(boolean value) {
        isBinary = value;
    }

    public boolean isBinary() {
        return isBinary;
    }


    /**
     * Gets the datahandler.
     *
     * @return Returns javax.activation.DataHandler
     */
    public Object getDataHandler() {
        if ((value != null || charArray != null) && isBinary) {
            String text = getTextFromProperPlace();
            return org.apache.axiom.attachments.utils.DataHandlerUtils
                    .getDataHandlerFromText(text, mimeType);
        } else {

            if (dataHandlerObject == null) {
                throw new OMException("No DataHandler available");
            } else if (dataHandlerObject instanceof DataHandlerProvider) {
                try {
                    dataHandlerObject = ((DataHandlerProvider)dataHandlerObject).getDataHandler();
                } catch (IOException ex) {
                    throw new OMException(ex);
                }
            }
            return dataHandlerObject;
        }
    }

    public String getContentID() {
        if (contentID == null) {
            contentID = UIDGenerator.generateContentId();
        }
        return this.contentID;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {

        if (!this.isBinary) {
            writeOutput(writer);
        } else {
            try {
                if (dataHandlerObject instanceof DataHandlerProvider) {
                    XMLStreamWriterUtils.writeDataHandler(writer, (DataHandlerProvider)dataHandlerObject,
                            contentID, optimize);
                } else {
                    XMLStreamWriterUtils.writeDataHandler(writer, (DataHandler)getDataHandler(),
                            contentID, optimize);
                }
            } catch (IOException ex) {
                throw new OMException("Error reading data handler", ex);
            }
        }
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMNode#buildAll()
      */
    public void buildWithAttachments() {
        if (isOptimized()) {
            // The call to getDataSource ensures that the MIME part is completely read
            ((DataHandler)this.getDataHandler()).getDataSource();
        }
    }
    
    public void setContentID(String cid) {
        this.contentID = cid;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        if (isBinary && options.isFetchDataHandlers()) {
            // Force loading of the reference to the DataHandler and ensure that its content is
            // completely fetched into memory (or temporary storage).
            ((DataHandler)getDataHandler()).getDataSource();
        }
        return factory.createOMText(targetParent, this);
    }
}
