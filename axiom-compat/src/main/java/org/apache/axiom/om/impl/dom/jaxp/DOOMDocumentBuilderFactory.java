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
package org.apache.axiom.om.impl.dom.jaxp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.dom.DOMMetaFactory;

/**
 * @deprecated Use {@link DOMMetaFactory#newDocumentBuilderFactory()} to get an Axiom compatible
 *             {@link DocumentBuilderFactory}.
 */
public class DOOMDocumentBuilderFactory extends DocumentBuilderFactory {
    private final DocumentBuilderFactory target;

    public DOOMDocumentBuilderFactory() {
        target = ((DOMMetaFactory)OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM)).newDocumentBuilderFactory();
    }
    
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return target.newDocumentBuilder();
    }

    public void setNamespaceAware(boolean awareness) {
        target.setNamespaceAware(awareness);
    }

    public void setValidating(boolean validating) {
        target.setValidating(validating);
    }

    public void setIgnoringElementContentWhitespace(boolean whitespace) {
        target.setIgnoringElementContentWhitespace(whitespace);
    }

    public void setExpandEntityReferences(boolean expandEntityRef) {
        target.setExpandEntityReferences(expandEntityRef);
    }

    public void setIgnoringComments(boolean ignoreComments) {
        target.setIgnoringComments(ignoreComments);
    }

    public void setCoalescing(boolean coalescing) {
        target.setCoalescing(coalescing);
    }

    public boolean isNamespaceAware() {
        return target.isNamespaceAware();
    }

    public boolean isValidating() {
        return target.isValidating();
    }

    public boolean isIgnoringElementContentWhitespace() {
        return target.isIgnoringElementContentWhitespace();
    }

    public boolean isExpandEntityReferences() {
        return target.isExpandEntityReferences();
    }

    public boolean isIgnoringComments() {
        return target.isIgnoringComments();
    }

    public boolean isCoalescing() {
        return target.isCoalescing();
    }

    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        target.setAttribute(name, value);
    }

    public Object getAttribute(String name) throws IllegalArgumentException {
        return target.getAttribute(name);
    }

    public void setFeature(String name, boolean value) throws ParserConfigurationException {
        target.setFeature(name, value);
    }

    public boolean getFeature(String name) throws ParserConfigurationException {
        return target.getFeature(name);
    }

    public Schema getSchema() {
        return target.getSchema();
    }

    public void setSchema(Schema schema) {
        target.setSchema(schema);
    }

    public void setXIncludeAware(boolean state) {
        target.setXIncludeAware(state);
    }

    public boolean isXIncludeAware() {
        return target.isXIncludeAware();
    }
}
