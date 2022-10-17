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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class DOMImplementation extends Multiton {
    public static final DOMImplementation XERCES =
            new DOMImplementation("xerces", true, true) {
                @Override
                protected DocumentBuilderFactory newDocumentBuilderFactory() {
                    return new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
                }
            };

    public static final DOMImplementation CRIMSON =
            new DOMImplementation("crimson", false, false) {
                @Override
                protected DocumentBuilderFactory newDocumentBuilderFactory() {
                    return new org.apache.crimson.jaxp.DocumentBuilderFactoryImpl();
                }
            };

    private final String name;
    private final boolean dom3;
    private final boolean internalSubset;

    private DOMImplementation(String name, boolean dom3, boolean internalSubset) {
        this.name = name;
        this.dom3 = dom3;
        this.internalSubset = internalSubset;
    }

    public final String getName() {
        return name;
    }

    public final boolean isDOM3() {
        return dom3;
    }

    public final boolean supportsGetInternalSubset() {
        return internalSubset;
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
        if (!expandEntityReferences) {
            // Crimson creates EntityReference nodes for predefined entities (such as &lt;);
            // expand them.
            expandPredefinedEntityReferences(document.getDocumentElement());
        }
        return document;
    }

    private void expandPredefinedEntityReferences(Element element) {
        Node child = element.getFirstChild();
        while (child != null) {
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE:
                    expandPredefinedEntityReferences((Element) child);
                    break;
                case Node.ENTITY_REFERENCE_NODE:
                    if (child.getNodeName().equals("lt")) {
                        Text content = (Text) child.getFirstChild().cloneNode(false);
                        element.replaceChild(content, child);
                        child = content;
                    }
            }
            child = child.getNextSibling();
        }
    }

    public final Document parse(InputStream in) throws SAXException, IOException {
        return parse(new InputSource(in));
    }
}
