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
package org.apache.axiom.ts.jaxp.sax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;

import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("deprecation")
final class CrimsonSAXParserWrapper extends SAXParser {
    private final SAXParser parent;

    public CrimsonSAXParserWrapper(SAXParser parent) {
        this.parent = parent;
    }

    @Override
    public void reset() {
        parent.reset();
    }

    @Override
    public void parse(InputStream is, HandlerBase hb) throws SAXException, IOException {
        parent.parse(is, hb);
    }

    @Override
    public void parse(InputStream is, HandlerBase hb, String systemId)
            throws SAXException, IOException {
        parent.parse(is, hb, systemId);
    }

    @Override
    public void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException {
        parent.parse(is, dh);
    }

    @Override
    public void parse(InputStream is, DefaultHandler dh, String systemId)
            throws SAXException, IOException {
        parent.parse(is, dh, systemId);
    }

    @Override
    public void parse(String uri, HandlerBase hb) throws SAXException, IOException {
        parent.parse(uri, hb);
    }

    @Override
    public void parse(String uri, DefaultHandler dh) throws SAXException, IOException {
        parent.parse(uri, dh);
    }

    @Override
    public void parse(File f, HandlerBase hb) throws SAXException, IOException {
        parent.parse(f, hb);
    }

    @Override
    public void parse(File f, DefaultHandler dh) throws SAXException, IOException {
        parent.parse(f, dh);
    }

    @Override
    public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException {
        parent.parse(is, hb);
    }

    @Override
    public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
        parent.parse(is, dh);
    }

    @Override
    public Parser getParser() throws SAXException {
        return parent.getParser();
    }

    @Override
    public XMLReader getXMLReader() throws SAXException {
        return new CrimsonXMLReaderWrapper(parent.getXMLReader());
    }

    @Override
    public boolean isNamespaceAware() {
        return parent.isNamespaceAware();
    }

    @Override
    public boolean isValidating() {
        return parent.isValidating();
    }

    @Override
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        parent.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        return parent.getProperty(name);
    }

    @Override
    public Schema getSchema() {
        return parent.getSchema();
    }

    @Override
    public boolean isXIncludeAware() {
        return parent.isXIncludeAware();
    }
}
