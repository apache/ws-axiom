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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.apache.axiom.om.impl.dom.intf.factory.DOOMNodeFactory;

/** Document builder factory that conforms to JAXP. */
public final class DOOMDocumentBuilderFactory extends DocumentBuilderFactory {
    private final DOOMNodeFactory factory;
    private Schema schema;

    public DOOMDocumentBuilderFactory(DOOMNodeFactory factory) {
        this.factory = factory;
    }

    @Override
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return new DOOMDocumentBuilder(
                factory,
                new DOMStAXParserConfiguration(isCoalescing(), isExpandEntityReferences()),
                schema);
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        // // TODO
        // throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void setFeature(String name, boolean value) throws ParserConfigurationException {
        // TODO TODO OS
    }

    @Override
    public boolean getFeature(String name) throws ParserConfigurationException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
