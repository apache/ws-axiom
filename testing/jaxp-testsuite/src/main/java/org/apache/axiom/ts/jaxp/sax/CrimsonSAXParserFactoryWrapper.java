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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

final class CrimsonSAXParserFactoryWrapper extends SAXParserFactory {
    private final SAXParserFactory parent;

    CrimsonSAXParserFactoryWrapper(SAXParserFactory parent) {
        this.parent = parent;
    }

    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        return new CrimsonSAXParserWrapper(parent.newSAXParser());
    }

    @Override
    public void setNamespaceAware(boolean awareness) {
        parent.setNamespaceAware(awareness);
    }

    @Override
    public void setValidating(boolean validating) {
        parent.setValidating(validating);
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
    public void setFeature(String name, boolean value) throws ParserConfigurationException,
            SAXNotRecognizedException, SAXNotSupportedException {
        parent.setFeature(name, value);
    }

    @Override
    public boolean getFeature(String name) throws ParserConfigurationException,
            SAXNotRecognizedException, SAXNotSupportedException {
        return parent.getFeature(name);
    }

    @Override
    public Schema getSchema() {
        return parent.getSchema();
    }

    @Override
    public void setSchema(Schema schema) {
        parent.setSchema(schema);
    }

    @Override
    public void setXIncludeAware(boolean state) {
        parent.setXIncludeAware(state);
    }

    @Override
    public boolean isXIncludeAware() {
        return parent.isXIncludeAware();
    }
}
