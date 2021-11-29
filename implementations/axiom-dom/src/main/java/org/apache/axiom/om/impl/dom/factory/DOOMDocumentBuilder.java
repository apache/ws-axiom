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

package org.apache.axiom.om.impl.dom.factory;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.dom.intf.factory.DOOMNodeFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

final class DOOMDocumentBuilder extends DocumentBuilder {
    private final DOOMNodeFactory factory;
    private final StAXParserConfiguration parserConfiguration;
    private final Schema schema;
    
    DOOMDocumentBuilder(DOOMNodeFactory factory, StAXParserConfiguration parserConfiguration, Schema schema) {
        this.factory = factory;
        this.parserConfiguration = parserConfiguration;
        this.schema = schema;
    }

    /**
     * Returns whether the parser is configured to understand namespaces or not. The StAX parser
     * used by this DOM impl is namespace aware therefore this will always return true.
     *
     * @see javax.xml.parsers.DocumentBuilder#isNamespaceAware()
     */
    @Override
    public boolean isNamespaceAware() {
        return true;
    }

    /**
     * The StAX builder used is the org.apache.axiom.om.impl.llom.StAXOMBuilder is a validating
     * builder.
     *
     * @see javax.xml.parsers.DocumentBuilder#isValidating()
     */
    @Override
    public boolean isValidating() {
        return true;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return factory;
    }

    /**
     * Returns a new document impl.
     *
     * @see javax.xml.parsers.DocumentBuilder#newDocument()
     */
    @Override
    public Document newDocument() {
        return factory.createDocument();
    }

    @Override
    public void setEntityResolver(EntityResolver er) {
        // TODO
    }

    @Override
    public void setErrorHandler(ErrorHandler eh) {
        // TODO 
    }

    @Override
    public Document parse(InputSource inputSource) throws SAXException, IOException {
        OMDocument document = factory.createOMBuilder(parserConfiguration,
                inputSource).getDocument();
        document.close(true);
        return (Document)document;
    }

    @Override
    public Document parse(InputStream is) throws SAXException, IOException {
        return parse(new InputSource(is));
    }

    /** @see javax.xml.parsers.DocumentBuilder#parse(java.io.File) */
    @Override
    public Document parse(File file) throws SAXException, IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            return parse(in);
        } finally {
            in.close();
        }
    }

    @Override
    public Document parse(InputStream is, String systemId) throws SAXException, IOException {
        InputSource inputSource = new InputSource(is);
        inputSource.setSystemId(systemId);
        return parse(inputSource);
    }

    @Override
    public Document parse(String uri) throws SAXException, IOException {
        return parse(new InputSource(uri));
    }
}
