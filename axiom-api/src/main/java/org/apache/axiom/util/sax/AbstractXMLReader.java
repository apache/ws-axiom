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
package org.apache.axiom.util.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * Partial implementation of the {@link XMLReader} interface. It implements all the getters and
 * setters so that subclasses only need to implement {@link XMLReader#parse(InputSource)} and {@link
 * XMLReader#parse(String)}. Subclasses can access the various handlers and properties set on the
 * reader through protected attributes.
 */
public abstract class AbstractXMLReader implements XMLReader {
    private static final String URI_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String URI_NAMESPACE_PREFIXES =
            "http://xml.org/sax/features/namespace-prefixes";
    private static final String URI_EXTERNAL_GENERAL_ENTITIES =
            "http://xml.org/sax/features/external-general-entities";

    private static final String URI_LEXICAL_HANDLER =
            "http://xml.org/sax/properties/lexical-handler";

    protected boolean namespaces = true;
    protected boolean namespacePrefixes = false;
    protected boolean externalGeneralEntities = true;

    protected ContentHandler contentHandler;
    protected LexicalHandler lexicalHandler;
    protected DTDHandler dtdHandler;
    protected EntityResolver entityResolver;
    protected ErrorHandler errorHandler;

    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    @Override
    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (URI_NAMESPACES.equals(name)) {
            return namespaces;
        } else if (URI_NAMESPACE_PREFIXES.equals(name)) {
            return namespacePrefixes;
        } else if (URI_EXTERNAL_GENERAL_ENTITIES.equals(name)) {
            return externalGeneralEntities;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    @Override
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {

        if (URI_NAMESPACES.equals(name)) {
            namespaces = value;
        } else if (URI_NAMESPACE_PREFIXES.equals(name)) {
            namespacePrefixes = value;
        } else if (URI_EXTERNAL_GENERAL_ENTITIES.equals(name)) {
            externalGeneralEntities = value;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    @Override
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {

        if (URI_LEXICAL_HANDLER.equals(name)) {
            return lexicalHandler;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    @Override
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (URI_LEXICAL_HANDLER.equals(name)) {
            lexicalHandler = (LexicalHandler) value;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }
}
