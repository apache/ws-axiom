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
package org.apache.axiom.ts.jaxp.dom;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.testing.multiton.Multiton;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class DOMImplementation extends Multiton {
    public static final DOMImplementation XERCES =
            new DOMImplementation("xerces") {
                @Override
                protected DocumentBuilderFactory newDocumentBuilderFactory() {
                    return new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
                }
            };

    private final String name;

    private DOMImplementation(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    protected abstract DocumentBuilderFactory newDocumentBuilderFactory();

    public final Document newDocument() {
        try {
            return newDocumentBuilderFactory().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new Error("Unexpected exception", ex);
        }
    }

    public final Document parse(InputSource is) throws SAXException, IOException {
        return parse(is, true);
    }

    public final Document parse(InputSource is, boolean expandEntityReferences)
            throws SAXException, IOException {
        DocumentBuilderFactory factory = newDocumentBuilderFactory();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(expandEntityReferences);
        Document document;
        try {
            document = factory.newDocumentBuilder().parse(is);
        } catch (ParserConfigurationException ex) {
            throw new Error("Unexpected exception", ex);
        }
        return document;
    }

    public final Document parse(InputStream in) throws SAXException, IOException {
        return parse(new InputSource(in));
    }
}
