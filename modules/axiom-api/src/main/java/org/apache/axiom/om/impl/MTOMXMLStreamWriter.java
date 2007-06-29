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

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.LinkedList;


/**
 * For the moment this assumes that transport takes the decision of whether to optimize or not by
 * looking at whether the MTOM optimize is enabled & also looking at the OM tree whether it has any
 * optimizable content.
 */
public class MTOMXMLStreamWriter implements XMLStreamWriter {
    private XMLStreamWriter xmlWriter;
    private OutputStream outStream;
    private LinkedList binaryNodeList = new LinkedList();
    private StringWriter bufferedSOAPBody;
    private OMOutputFormat format = new OMOutputFormat();

    public MTOMXMLStreamWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
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
        this.format = format;
        this.outStream = outStream;

        if (format.getCharSetEncoding() == null) //Default encoding is UTF-8
            format.setCharSetEncoding(OMOutputFormat.DEFAULT_CHAR_SET_ENCODING);

        if (format.isOptimized()) {
            bufferedSOAPBody = new StringWriter();
            xmlWriter = StAXUtils.createXMLStreamWriter(bufferedSOAPBody);
        } else {
            xmlWriter = StAXUtils.createXMLStreamWriter(outStream,
                                                        format.getCharSetEncoding());
        }
    }

    public void writeStartElement(String string) throws XMLStreamException {
        xmlWriter.writeStartElement(string);
    }

    public void writeStartElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1);
    }

    public void writeStartElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1, string2);
    }

    public void writeEmptyElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1);
    }

    public void writeEmptyElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1, string2);
    }

    public void writeEmptyElement(String string) throws XMLStreamException {
        xmlWriter.writeStartElement(string);
    }

    public void writeEndElement() throws XMLStreamException {
        xmlWriter.writeEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        xmlWriter.writeEndDocument();
    }

    public void close() throws XMLStreamException {
        xmlWriter.close();
    }

    public void flush() throws XMLStreamException {
        xmlWriter.flush();
        String SOAPContentType;
        if (format.isOptimized()) {
            if (format.isSOAP11()) {
                SOAPContentType = SOAP11Constants.SOAP_11_CONTENT_TYPE;
            } else {
                SOAPContentType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
            }
            MIMEOutputUtils.complete(
                    outStream,
                    bufferedSOAPBody,
                    binaryNodeList,
                    format.getMimeBoundary(),
                    format.getRootContentId(),
                    format.getCharSetEncoding(), SOAPContentType);
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
        binaryNodeList.add(node);
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

    public void setOutputFormat(OMOutputFormat format) {
        this.format = format;
    }
}
