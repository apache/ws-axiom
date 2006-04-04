/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;


import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMOutputImpl;
import org.apache.axiom.om.impl.mtom.MTOMStAXSOAPModelBuilder;
import org.apache.axiom.om.util.Base64;
import org.apache.axiom.om.util.UUIDGenerator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;

public class OMTextImpl extends OMNodeImpl implements OMText, OMConstants {
    protected String value = null;

    protected OMNamespace textNS;

    protected String mimeType;

    protected boolean optimize = false;

    protected boolean isBinary = false;

    /**
     * Field contentID for the mime part used when serializing Binary stuff as
     * MTOM optimized.
     */
    private String contentID = null;

    /**
     * Field dataHandler contains the DataHandler
     * Declaring as Object to remove the dependency on
     * Javax.activation.DataHandler
     */
    private Object dataHandlerObject = null;

    /**
     * Field nameSpace used when serializing Binary stuff as MTOM optimized.
     */
    protected OMNamespace xopNS = null;

    /**
     * Field localName used when serializing Binary stuff as MTOM optimized.
     */
    protected String localName = "Include";

    /**
     * Field attributes used when serializing Binary stuff as MTOM optimized.
     */
    protected OMAttribute attribute;

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
        super(factory);
        this.value = s;
        this.nodeType = nodeType;
        this.xopNS = new OMNamespaceImpl(
                "http://www.w3.org/2004/08/xop/include", "xop", factory);
    }

    /**
     * Constructor OMTextImpl.
     *
     * @param parent
     * @param text
     */
    public OMTextImpl(OMElement parent, String text, OMFactory factory) {
        this(parent, text, TEXT_NODE, factory);
    }

    public OMTextImpl(OMElement parent, String text, int nodeType,
                      OMFactory factory) {
        super(parent, factory);
        this.value = text;
        done = true;
        this.nodeType = nodeType;
        this.xopNS = new OMNamespaceImpl(
                "http://www.w3.org/2004/08/xop/include", "xop", factory);
    }


    public OMTextImpl(OMElement parent, QName text, OMFactory factory) {
        this(parent, text, TEXT_NODE, factory);
    }

    public OMTextImpl(OMElement parent, QName text, int nodeType,
                      OMFactory factory) {
        super(parent, factory);
        this.textNS = ((OMElementImpl) parent).handleNamespace(text);
        this.value = text.getLocalPart();
        done = true;
        this.nodeType = nodeType;
        this.xopNS = new OMNamespaceImpl(
                "http://www.w3.org/2004/08/xop/include", "xop", factory);
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
     * @param s        -
     *                 base64 encoded String representation of Binary
     * @param mimeType of the Binary
     */
    public OMTextImpl(OMElement parent, String s, String mimeType,
                      boolean optimize, OMFactory factory) {
        this(parent, s, factory);
        this.mimeType = mimeType;
        this.optimize = optimize;
        this.isBinary = true;
        done = true;
        this.nodeType = TEXT_NODE;
    }

    /**
     * @param dataHandler To send binary optimised content Created programatically.
     */
    public OMTextImpl(Object dataHandler, OMFactory factory) {
        this(dataHandler, true, factory);
    }

    /**
     * @param dataHandler
     * @param optimize    To send binary content. Created progrmatically.
     */
    public OMTextImpl(Object dataHandler, boolean optimize, OMFactory factory) {
        super(factory);
        this.dataHandlerObject = dataHandler;
        this.isBinary = true;
        this.optimize = optimize;
        done = true;
        this.nodeType = TEXT_NODE;
        this.xopNS = new OMNamespaceImpl(
                "http://www.w3.org/2004/08/xop/include", "xop", factory);
    }

    /**
     * @param contentID
     * @param parent
     * @param builder   Used when the builder is encountered with a XOP:Include tag
     *                  Stores a reference to the builder and the content-id. Supports
     *                  deferred parsing of MIME messages.
     */
    public OMTextImpl(String contentID, OMElement parent,
                      OMXMLParserWrapper builder, OMFactory factory) {
        super(parent, factory);
        this.contentID = contentID;
        this.optimize = true;
        this.isBinary = true;
        this.builder = builder;
        this.nodeType = TEXT_NODE;
        this.xopNS = new OMNamespaceImpl(
                "http://www.w3.org/2004/08/xop/include", "xop", factory);
    }

    /**
     * @param omOutput
     * @throws XMLStreamException
     */
    public void internalSerialize(OMOutputImpl omOutput) throws XMLStreamException {
        internalSerializeLocal(omOutput);

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

    /**
     * Returns the value.
     */
    public String getText() throws OMException {
        if (textNS != null) {
            return getTextString();
        }else if (this.value != null) {
            return this.value;
        } else {
            try {
                InputStream inStream;
                inStream = this.getInputStream();
                byte[] data;
                StringBuffer text = new StringBuffer();
                do {
                    data = new byte[1024];
                    int len;
                    while ((len = inStream.read(data)) > 0) {
                        byte[] temp = new byte[len];
                        System.arraycopy(data, 0, temp, 0, len);
                        text.append(Base64.encode(temp));
                    }

                } while (inStream.available() > 0);

                return text.toString();
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
    }


/**
     * Returns the value.
     */
    public QName getTextAsQName() throws OMException {
        if (textNS != null) {
            String prefix = textNS.getPrefix();
            if (prefix == null || "".equals(prefix)) {
                return new QName(textNS.getName(), value);
            }else {
               return new QName(textNS.getName(), value, prefix);
            }
        }else if (this.value != null) {
            return new QName(value);
        } else {
            try {
                InputStream inStream;
                inStream = this.getInputStream();
                byte[] data;
                StringBuffer text = new StringBuffer();
                do {
                    data = new byte[1024];
                    int len;
                    while ((len = inStream.read(data)) > 0) {
                        byte[] temp = new byte[len];
                        System.arraycopy(data, 0, temp, 0, len);
                        text.append(Base64.encode(temp));
                    }

                } while (inStream.available() > 0);

                return new QName(text.toString());
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
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
     * Gets the datahandler.
     *
     * @return Returns javax.activation.DataHandler
     */
    public Object getDataHandler() {
        /*
         * this should return a DataHandler containing the binary data
         * reperesented by the Base64 strings stored in OMText
         */

        if(isBinary){

        }

        if ((value != null || textNS != null) & isBinary) {
            String text = textNS == null ? value : getTextString();
            return org.apache.axiom.attachments.DataHandlerUtils.getDataHandlerFromText(text, mimeType);
        } else {

            if (dataHandlerObject == null) {
                if (contentID == null) {
                    throw new RuntimeException("ContentID is null");
                }
                dataHandlerObject = ((MTOMStAXSOAPModelBuilder) builder)
                        .getDataHandler(contentID);
            }
            return dataHandlerObject;
        }
    }

    private String getTextString() {
        if(textNS != null){
            String prefix = textNS.getPrefix();
            if (prefix == null || "".equals(prefix)) {
                return value;
            }else {
                return prefix + ":" + value;
            }
        }

        return null;
    }

    public String getLocalName() {
        return localName;
    }

    public java.io.InputStream getInputStream() throws OMException {
        if (isBinary) {
            if (dataHandlerObject == null) {
                getDataHandler();
            }
            InputStream inStream;
            javax.activation.DataHandler dataHandler = (javax.activation.DataHandler) dataHandlerObject;
            try {
                inStream = dataHandler.getDataSource().getInputStream();
            } catch (IOException e) {
                throw new OMException(
                        "Cannot get InputStream from DataHandler." + e);
            }
            return inStream;
        } else {
            throw new OMException("Unsupported Operation");
        }
    }

    public String getContentID() {
        if (contentID == null) {
            contentID = UUIDGenerator.getUUID()
                    + "@apache.org";
        }
        return this.contentID;
    }

    public boolean isComplete() {
        return true;
    }

    public void internalSerializeAndConsume(OMOutputImpl omOutput)
            throws XMLStreamException {
        internalSerializeLocal(omOutput);
    }

    private void internalSerializeLocal(OMOutputImpl omOutput) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = omOutput.getXmlStreamWriter();

        if (!this.isBinary) {
            writeOutput(xmlStreamWriter);
        } else {

            if (omOutput.isOptimized()) {
                if (contentID == null) {
                    contentID = omOutput.getNextContentId();
                }
                // send binary as MTOM optimised
                this.attribute = new OMAttributeImpl("href",
                        new OMNamespaceImpl("", "", this.factory), "cid:" + getContentID(),
                        this.factory);
                this.serializeStartpart(xmlStreamWriter);
                omOutput.writeOptimized(this);
                xmlStreamWriter.writeEndElement();
            } else {
                xmlStreamWriter.writeCharacters(this.getText());
            }
        }
    }

    /*
     * Methods to copy from OMSerialize utils
     */
    private void serializeStartpart(XMLStreamWriter writer)
            throws XMLStreamException {
        String nameSpaceName;
        String writer_prefix;
        String prefix;
        if (this.xopNS != null) {
            nameSpaceName = this.xopNS.getName();
            writer_prefix = writer.getPrefix(nameSpaceName);
            prefix = this.xopNS.getPrefix();
            if (nameSpaceName != null) {
                if (writer_prefix != null) {
                    writer
                            .writeStartElement(nameSpaceName, this
                                    .getLocalName());
                } else {
                    if (prefix != null) {
                        writer.writeStartElement(prefix, this.getLocalName(),
                                nameSpaceName);
                        //TODO FIX ME
                        //writer.writeNamespace(prefix, nameSpaceName);
                        writer.setPrefix(prefix, nameSpaceName);
                    } else {
                        writer.writeStartElement(nameSpaceName, this
                                .getLocalName());
                        writer.writeDefaultNamespace(nameSpaceName);
                        writer.setDefaultNamespace(nameSpaceName);
                    }
                }
            } else {
                writer.writeStartElement(this.getLocalName());
            }
        } else {
            writer.writeStartElement(this.getLocalName());
        }
        // add the elements attribute "href"
        serializeAttribute(this.attribute, writer);
        // add the namespace
        serializeNamespace(this.xopNS, writer);
    }

    /**
     * Method serializeAttribute.
     *
     * @param attr
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
            namespaceName = ns.getName();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName, attr
                        .getLocalName(), attr.getAttributeValue());
            } else {
                writer.writeAttribute(namespaceName, attr.getLocalName(), attr
                        .getAttributeValue());
            }
        } else {
            writer.writeAttribute(attr.getLocalName(), attr.getAttributeValue());
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
            String uri = namespace.getName();
            String ns_prefix = namespace.getPrefix();
            writer.writeNamespace(ns_prefix, namespace.getName());
            writer.setPrefix(ns_prefix, uri);
        }
    }

    /**
     * A slightly different implementation of the discard method.
     *
     * @throws OMException
     */
    public void discard() throws OMException {
        if (done) {
            this.detach();
        } else {
            builder.discard((OMElement) this.parent);
        }
    }

}
