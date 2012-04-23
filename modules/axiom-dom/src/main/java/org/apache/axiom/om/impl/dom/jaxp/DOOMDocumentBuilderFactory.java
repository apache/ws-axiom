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

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;

/**
 * Document builder factory that conforms to JAXP.
 */
public class DOOMDocumentBuilderFactory extends DocumentBuilderFactory {
    private final OMFactory factory;
    private Schema schema;
    
    /**
     * @deprecated Application code should not instantiate this class directly, but use
     *             {@link DOMMetaFactory#newDocumentBuilderFactory()} to get an Axiom compatible
     *             {@link DocumentBuilderFactory}.
     */
    public DOOMDocumentBuilderFactory() {
        this(OMDOMMetaFactory.INSTANCE.getOMFactory());
    }
    
    public DOOMDocumentBuilderFactory(OMFactory factory) {
        this.factory = factory;
    }
    
    public DocumentBuilder newDocumentBuilder()
            throws ParserConfigurationException {
        return new DOOMDocumentBuilder(factory, schema);
    }

    public Object getAttribute(String name) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setAttribute(String name, Object value)
            throws IllegalArgumentException {
        // // TODO
        // throw new UnsupportedOperationException("TODO");
    }

    public void setFeature(String name, boolean value)
            throws ParserConfigurationException {
        // TODO TODO OS
    }

    public boolean getFeature(String name) throws ParserConfigurationException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
