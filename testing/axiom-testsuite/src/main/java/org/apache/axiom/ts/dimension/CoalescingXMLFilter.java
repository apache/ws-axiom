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
package org.apache.axiom.ts.dimension;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

final class CoalescingXMLFilter implements XMLFilter, ContentHandler, LexicalHandler {
    private static final String URI_LEXICAL_HANDLER =
            "http://xml.org/sax/properties/lexical-handler";

    private XMLReader parent;
    private ContentHandler contentHandler;
    private LexicalHandler lexicalHandler;
    private char[] buffer = new char[1024];
    private int bufferSize;

    CoalescingXMLFilter(XMLReader parent) {
        this.parent = parent;
    }

    @Override
    public void setParent(XMLReader parent) {
        this.parent = parent;
    }

    @Override
    public XMLReader getParent() {
        return parent;
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        parent.setEntityResolver(resolver);
    }

    @Override
    public EntityResolver getEntityResolver() {
        return parent.getEntityResolver();
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        parent.setDTDHandler(handler);
    }

    @Override
    public DTDHandler getDTDHandler() {
        return parent.getDTDHandler();
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        contentHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        parent.setErrorHandler(handler);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return parent.getErrorHandler();
    }

    @Override
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (URI_LEXICAL_HANDLER.equals(name)) {
            return lexicalHandler;
        } else {
            return parent.getProperty(name);
        }
    }

    @Override
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (URI_LEXICAL_HANDLER.equals(name)) {
            lexicalHandler = (LexicalHandler) value;
        } else {
            parent.setProperty(name, value);
        }
    }

    @Override
    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return parent.getFeature(name);
    }

    @Override
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        parent.setFeature(name, value);
    }

    private void setup() throws SAXException {
        parent.setContentHandler(this);
        parent.setProperty(URI_LEXICAL_HANDLER, this);
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        setup();
        parent.parse(input);
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        setup();
        parent.parse(systemId);
    }

    private void flushBuffer() throws SAXException {
        if (bufferSize > 0) {
            contentHandler.characters(buffer, 0, bufferSize);
        }
        bufferSize = 0;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        contentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        flushBuffer();
        contentHandler.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        flushBuffer();
        contentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        flushBuffer();
        contentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {
        flushBuffer();
        contentHandler.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        flushBuffer();
        contentHandler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        int bufferCapacity = buffer.length;
        while (bufferSize + length > bufferCapacity) {
            bufferCapacity *= 2;
        }
        if (bufferCapacity != buffer.length) {
            char[] newBuffer = new char[bufferCapacity];
            System.arraycopy(buffer, 0, newBuffer, 0, bufferSize);
            buffer = newBuffer;
        }
        System.arraycopy(ch, start, buffer, bufferSize, length);
        bufferSize += length;
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        flushBuffer();
        contentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        flushBuffer();
        contentHandler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        flushBuffer();
        contentHandler.skippedEntity(name);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        flushBuffer();
        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        flushBuffer();
        if (lexicalHandler != null) {
            lexicalHandler.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        flushBuffer();
        if (lexicalHandler != null) {
            lexicalHandler.endEntity(name);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        flushBuffer();
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        flushBuffer();
        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        flushBuffer();
        if (lexicalHandler != null) {
            lexicalHandler.comment(ch, start, length);
        }
    }
}
