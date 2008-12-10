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

package org.apache.axiom.om.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.util.CommonUtils;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * MTOMXMLStreamWriter is an XML + Attachments stream writer.
 * 
 * For the moment this assumes that transport takes the decision of whether to optimize or not by
 * looking at whether the MTOM optimize is enabled & also looking at the OM tree whether it has any
 * optimizable content.
 */
public class MTOMXMLStreamWriter implements XMLStreamWriter {
    private static Log log = LogFactory.getLog(MTOMXMLStreamWriter.class);
    private static boolean isDebugEnabled = log.isDebugEnabled();
    private static boolean isTraceEnabled = log.isTraceEnabled();
    private final static int UNSUPPORTED = -1;
    private final static int EXCEED_LIMIT = 1;
    private XMLStreamWriter xmlWriter;
    private OutputStream outStream;
    private LinkedList binaryNodeList = new LinkedList();
    private ByteArrayOutputStream bufferedXML;  // XML for the SOAPPart
    private OMOutputFormat format = new OMOutputFormat();
    
    // State variables
    private boolean isEndDocument = false; // has endElement been called
    private boolean isComplete = false;    // have the attachments been written
    private int depth = 0;                 // current eleement depth

    public MTOMXMLStreamWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
        if (isTraceEnabled) {
            log.trace("Call Stack =" + CommonUtils.callStackToString());
        }
    }

    /**
     * Creates a new MTOMXMLStreamWriter with specified encoding.
     *
     * @param outStream
     * @param format
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     * @see OMOutputFormat#DEFAULT_CHAR_SET_ENCODING
     */
    public MTOMXMLStreamWriter(OutputStream outStream, OMOutputFormat format)
            throws XMLStreamException, FactoryConfigurationError {
        if (isDebugEnabled) {
            log.debug("OutputStream =" + outStream.getClass());
            log.debug("OMFormat = " + format.toString());
        }
        if (isTraceEnabled) {
            log.trace("Call Stack =" + CommonUtils.callStackToString());
        }
        this.format = format;
        this.outStream = outStream;

        if (format.getCharSetEncoding() == null) //Default encoding is UTF-8
            format.setCharSetEncoding(OMOutputFormat.DEFAULT_CHAR_SET_ENCODING);

        if (format.isOptimized()) {
            // REVIEW If the buffered XML gets too big, should it be written out to a file 
            bufferedXML = new ByteArrayOutputStream();
            xmlWriter = StAXUtils.createXMLStreamWriter(bufferedXML,format.getCharSetEncoding());
        } else {
            xmlWriter = StAXUtils.createXMLStreamWriter(outStream,
                                                        format.getCharSetEncoding());
        }
    }

    public void writeStartElement(String string) throws XMLStreamException {
        xmlWriter.writeStartElement(string);
        depth++;
    }

    public void writeStartElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1);
        depth++;
    }

    public void writeStartElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1, string2);
        depth++;
    }

    public void writeEmptyElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeEmptyElement(string, string1);
    }

    public void writeEmptyElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeEmptyElement(string, string1, string2);
    }

    public void writeEmptyElement(String string) throws XMLStreamException {
        xmlWriter.writeEmptyElement(string);
    }

    public void writeEndElement() throws XMLStreamException {
        xmlWriter.writeEndElement();
        depth--;
    }

    public void writeEndDocument() throws XMLStreamException {
        if (isDebugEnabled) {
            log.debug("writeEndDocument");
        }
        xmlWriter.writeEndDocument();
        isEndDocument = true; 
    }

    public void close() throws XMLStreamException {
        if (isDebugEnabled) {
            log.debug("close");
        }
        xmlWriter.close();
    }

    /**
     * Flush is overridden to trigger the attachment serialization
     */
    public void flush() throws XMLStreamException {
        if (isDebugEnabled) {
            log.debug("Calling MTOMXMLStreamWriter.flush");
        }
        xmlWriter.flush();
        String SOAPContentType;
        // flush() triggers the optimized attachment writing.
        // If the optimized attachments are specified, and the xml
        // document is completed, then write out the attachments.
        if (format.isOptimized() && !isComplete & (isEndDocument || depth == 0)) {
            if (isDebugEnabled) {
                log.debug("The XML writing is completed.  Now the attachments are written");
            }
            isComplete = true;
            if (format.isSOAP11()) {
                SOAPContentType = SOAP11Constants.SOAP_11_CONTENT_TYPE;
            } else {
                SOAPContentType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
            }
            try {
                MIMEOutputUtils.complete(outStream,
                                         bufferedXML.toByteArray(),
                                         binaryNodeList,
                                         format.getMimeBoundary(),
                                         format.getRootContentId(),
                                         format.getCharSetEncoding(),
                                         SOAPContentType, 
                                         format);
                bufferedXML.close();
                bufferedXML = null;
            } catch (UnsupportedEncodingException e) {
                throw new OMException(e);
            } catch (IOException e) {
                throw new OMException(e);
            }
        }
    }
    

    public void writeAttribute(String string, String string1) throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1);
    }

    public void writeAttribute(String string, String string1, String string2, String string3)
            throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1, string2, string3);
    }

    public void writeAttribute(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1, string2);
    }

    public void writeNamespace(String string, String string1) throws XMLStreamException {
        xmlWriter.writeNamespace(string, string1);
    }

    public void writeDefaultNamespace(String string) throws XMLStreamException {
        xmlWriter.writeDefaultNamespace(string);
    }

    public void writeComment(String string) throws XMLStreamException {
        xmlWriter.writeComment(string);
    }

    public void writeProcessingInstruction(String string) throws XMLStreamException {
        xmlWriter.writeProcessingInstruction(string);
    }

    public void writeProcessingInstruction(String string, String string1)
            throws XMLStreamException {
        xmlWriter.writeProcessingInstruction(string, string1);
    }

    public void writeCData(String string) throws XMLStreamException {
        xmlWriter.writeCData(string);
    }

    public void writeDTD(String string) throws XMLStreamException {
        xmlWriter.writeDTD(string);
    }

    public void writeEntityRef(String string) throws XMLStreamException {
        xmlWriter.writeEntityRef(string);
    }

    public void writeStartDocument() throws XMLStreamException {
        xmlWriter.writeStartDocument();
    }

    public void writeStartDocument(String string) throws XMLStreamException {
        xmlWriter.writeStartDocument(string);
    }

    public void writeStartDocument(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartDocument(string, string1);
    }

    public void writeCharacters(String string) throws XMLStreamException {
        xmlWriter.writeCharacters(string);
    }

    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        xmlWriter.writeCharacters(chars, i, i1);
    }

    public String getPrefix(String string) throws XMLStreamException {
        return xmlWriter.getPrefix(string);
    }

    public void setPrefix(String string, String string1) throws XMLStreamException {
        xmlWriter.setPrefix(string, string1);
    }

    public void setDefaultNamespace(String string) throws XMLStreamException {
        xmlWriter.setDefaultNamespace(string);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        xmlWriter.setNamespaceContext(namespaceContext);
    }

    public NamespaceContext getNamespaceContext() {
        return xmlWriter.getNamespaceContext();
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return xmlWriter.getProperty(string);
    }

    public boolean isOptimized() {
        return format.isOptimized();
    }

    public String getContentType() {
        return format.getContentType();
    }

    public void writeOptimized(OMText node) {
        if(isDebugEnabled){
            log.debug("Start MTOMXMLStreamWriter.writeOptimized()");
        }
        binaryNodeList.add(node);    
        if(isDebugEnabled){
            log.debug("Exit MTOMXMLStreamWriter.writeOptimized()");
        }
    }
    /*
     * This method check if size of dataHandler exceeds the optimization Threshold
     * set on OMOutputFormat. 
     * return true is size exceeds the threshold limit.
     * return false otherwise.
     */
    public boolean isOptimizedThreshold(OMText node){
    	if(isDebugEnabled){
            log.debug("Start MTOMXMLStreamWriter.isOptimizedThreshold()");
        }
        DataHandler dh = (DataHandler)node.getDataHandler();
        int optimized = UNSUPPORTED;
        if(dh!=null){
            if(isDebugEnabled){
                log.debug("DataHandler fetched, starting optimized Threshold processing");
            }
            optimized= BufferUtils.doesDataHandlerExceedLimit(dh, format.getOptimizedThreshold());
        }
        if(optimized == UNSUPPORTED || optimized == EXCEED_LIMIT){
            if(log.isDebugEnabled()){
                log.debug("node should be added to binart NodeList for optimization");
            }
            return true;
        }
        return false;
    }
    
    public void setXmlStreamWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
    }

    public XMLStreamWriter getXmlStreamWriter() {
        return xmlWriter;
    }

    public String getMimeBoundary() {
        return format.getMimeBoundary();
    }

    public String getRootContentId() {
        return format.getRootContentId();
    }

    public String getNextContentId() {
        return format.getNextContentId();
    }

    /**
     * Returns the character set encoding scheme. If the value of the charSetEncoding is not set
     * then the default will be returned.
     *
     * @return Returns encoding.
     */
    public String getCharSetEncoding() {
        return format.getCharSetEncoding();
    }

    public void setCharSetEncoding(String charSetEncoding) {
        format.setCharSetEncoding(charSetEncoding);
    }

    public String getXmlVersion() {
        return format.getXmlVersion();
    }

    public void setXmlVersion(String xmlVersion) {
        format.setXmlVersion(xmlVersion);
    }

    public void setSoap11(boolean b) {
        format.setSOAP11(b);
    }

    public boolean isIgnoreXMLDeclaration() {
        return format.isIgnoreXMLDeclaration();
    }

    public void setIgnoreXMLDeclaration(boolean ignoreXMLDeclaration) {
        format.setIgnoreXMLDeclaration(ignoreXMLDeclaration);
    }

    public void setDoOptimize(boolean b) {
        format.setDoOptimize(b);
    }

    /**
     * Get the output format used by this writer.
     * <p>
     * The caller should use the returned instance in a read-only way, i.e.
     * he should not modify the settings of the output format. Any attempt
     * to do so will lead to unpredictable results.
     * 
     * @return the output format used by this writer
     */
    public OMOutputFormat getOutputFormat() {
        return format;
    }

    public void setOutputFormat(OMOutputFormat format) {
        this.format = format;
    }
    
    /**
     * If this XMLStreamWriter is connected to an OutputStream
     * then the OutputStream is returned.  This allows a node
     * (perhaps an OMSourcedElement) to write its content
     * directly to the OutputStream.
     * @return OutputStream or null
     */
    public OutputStream getOutputStream() throws XMLStreamException {  
        OutputStream os = null;
        if (bufferedXML != null) {
            os = bufferedXML;
        } else {
            os = outStream;
        }
        
        if (isDebugEnabled) {
            if (os == null) {
                log.debug("Direct access to the output stream is not available.");
            } else if (bufferedXML != null) {
                log.debug("Returning access to the buffered xml stream: " + bufferedXML);
            } else {
                log.debug("Returning access to the original output stream: " + os);
            }
        }
       
        if (os != null) {
            // Flush the state of the writer..Many times the 
            // write defers the writing of tag characters (>)
            // until the next write.  Flush out this character
            this.writeCharacters(""); 
            this.flush();
        }
        return os;
    }
    
    /**
     * Writes the relevant output.
     *
     * @param writer
     * @throws XMLStreamException
     */
    private void writeOutput(OMText textNode) throws XMLStreamException {
        int type = textNode.getType();
        if (type == OMNode.TEXT_NODE || type == OMNode.SPACE_NODE) {
            writeCharacters(textNode.getText());
        } else if (type == OMNode.CDATA_SECTION_NODE) {
            writeCData(textNode.getText());
        } else if (type == OMNode.ENTITY_REFERENCE_NODE) {
            writeEntityRef(textNode.getText());
        }
    }
}
