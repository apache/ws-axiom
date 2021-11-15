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
package org.apache.axiom.ts.dimension;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Defines a way how an {@link OMElement} can be placed inside an object model. An implementation of
 * this interface wraps an {@link OMElement} in a container of a specific type ({@link OMDocument}
 * or {@link OMElement}) in a specific state.
 */
public abstract class ElementContext extends Multiton implements Dimension {
    public static final ElementContext ORPHAN = new ElementContext() {
        @Override
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("container", "none");
        }

        @Override
        public OMContainer wrap(OMElement element) {
            return null;
        }

        @Override
        public InputSource getControl(InputSource xml) {
            throw new UnsupportedOperationException();
        }
    };
    
    /**
     * The {@link OMElement} is a child of another (programmatically created) {@link OMElement}.
     */
    public static final ElementContext ELEMENT = new ElementContext() {
        @Override
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("container", "element");
            testCase.addTestParameter("complete", true);
        }

        @Override
        public OMContainer wrap(OMElement element) {
            OMElement parent = element.getOMFactory().createOMElement("parent", null);
            parent.addChild(element);
            return parent;
        }

        @Override
        public InputSource getControl(InputSource xml) throws Exception {
            Document document = DOMImplementation.XERCES.parse(xml);
            Element parent = document.createElementNS(null, "parent");
            parent.appendChild(document.getDocumentElement());
            StringWriter sw = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(parent), new StreamResult(sw));
            return new InputSource(new StringReader(sw.toString()));
        }
    };
    
    /**
     * The {@link OMElement} is a child of another {@link OMElement} created from a parser and that
     * is incomplete.
     */
    public static final ElementContext INCOMPLETE_ELEMENT = new ElementContext() {
        @Override
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("container", "element");
            testCase.addTestParameter("complete", "false");
        }
        
        @Override
        public OMContainer wrap(OMElement element) {
            OMElement parent = OMXMLBuilderFactory.createOMBuilder(element.getOMFactory(),
                    new StringReader("<parent><sibling/></parent>")).getDocumentElement();
            parent.getFirstOMChild().insertSiblingBefore(element);
            Assert.assertFalse(parent.isComplete());
            return parent;
        }
        
        @Override
        public InputSource getControl(InputSource xml) throws Exception {
            Document document = DOMImplementation.XERCES.parse(xml);
            Element parent = document.createElementNS(null, "parent");
            parent.appendChild(document.getDocumentElement());
            parent.appendChild(document.createElementNS(null, "sibling"));
            StringWriter sw = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(parent), new StreamResult(sw));
            return new InputSource(new StringReader(sw.toString()));
        }
    };
    
    private ElementContext() {}
    
    public abstract OMContainer wrap(OMElement element);
    
    public abstract InputSource getControl(InputSource xml) throws Exception;
}
