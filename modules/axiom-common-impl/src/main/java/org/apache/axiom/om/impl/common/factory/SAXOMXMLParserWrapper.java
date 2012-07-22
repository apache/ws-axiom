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
package org.apache.axiom.om.impl.common.factory;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.SAXOMBuilder;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * {@link OMXMLParserWrapper} implementation for SAX parsers. By "SAX parser" we mean anything that
 * is able to write a sequence of events to a SAX {@link ContentHandler}.
 * <p>
 * <b>Note:</b> This is a very naive implementation. Other implementations of the Axiom API may
 * provide smarter implementations. For example, in theory it should be possible to defer the
 * invocation of the parser until the {@link OMElement} object returned by
 * {@link #getDocumentElement()} is accessed.
 */
public class SAXOMXMLParserWrapper implements OMXMLParserWrapper {
    private final OMFactory factory;
    private final SAXSource source;
    private OMDocument document;

    public SAXOMXMLParserWrapper(OMFactory factory, SAXSource source) {
        this.factory = factory;
        this.source = source;
    }

    public OMDocument getDocument() {
        if (document == null) {
            SAXOMBuilder builder = new SAXOMBuilder(factory);
            XMLReader reader = source.getXMLReader();
            reader.setContentHandler(builder);
            reader.setDTDHandler(builder);
            try {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.setProperty("http://xml.org/sax/properties/declaration-handler", builder);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.parse(source.getInputSource());
            } catch (IOException ex) {
                throw new OMException(ex);
            } catch (SAXException ex) {
                throw new OMException(ex);
            }
            document = builder.getDocument();
        }
        return document;
    }

    public OMElement getDocumentElement() {
        return getDocument().getOMDocumentElement();
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        return getDocumentElement();
    }

    public void close() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void discard(OMElement el) throws OMException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public short getBuilderType() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncoding() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object getParser() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object getRegisteredContentHandler() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isCache() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isCompleted() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int next() throws OMException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void registerExternalContentHandler(Object obj) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void setCache(boolean b) throws OMException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
