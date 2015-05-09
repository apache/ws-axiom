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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Defines a strategy to create an {@link OMXMLParserWrapper} from a given test file.
 */
public abstract class BuilderFactory extends Multiton implements Dimension {
    /**
     * Creates an {@link OMXMLParserWrapper} directly from the given {@link InputSource}, i.e. let
     * instantiate an appropriate parser.
     */
    public static final BuilderFactory PARSER = new BuilderFactory() {
        public boolean isDeferredParsing() {
            return true;
        }

        public void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator) {
        }

        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("source", "parser");
        }

        public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception {
            return metaFactory.createOMBuilder(metaFactory.getOMFactory(),
                    AxiomTestCase.TEST_PARSER_CONFIGURATION, inputSource);
        }
    };

    /**
     * Creates an {@link OMXMLParserWrapper} by parsing the input using DOM and passing it as a DOM
     * tree to Axiom.
     */
    public static final BuilderFactory DOM = new BuilderFactory() {
        public boolean isDeferredParsing() {
            return true;
        }

        public void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator) {
            // DOM gives access to the parsed replacement value (via the Entity interface), but Axiom
            // stores the unparsed replacement value. Therefore OMEntityReference#getReplacementText()
            // returns null for nodes created from a DOM tree.
            comparator.setCompareEntityReplacementValue(false);
            // DOM (or at least Xerces) sorts attributes
            comparator.setSortAttributes(true);
        }

        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("source", "dom");
        }

        public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setExpandEntityReferences(false);
            Document document = dbf.newDocumentBuilder().parse(inputSource);
            return OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), document, false);
        }
    };
    
    /**
     * Creates an {@link OMXMLParserWrapper} by passing a {@link SAXSource} to Axiom.
     */
    public static final BuilderFactory SAX = new BuilderFactory() {
        public boolean isDeferredParsing() {
            return false;
        }

        public void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator) {
            // SAX doesn't provide this information
            comparator.setCompareCharacterEncodingScheme(false);
            comparator.setCompareEncoding(false);
        }

        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("source", "sax");
        }

        public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            SAXParser parser = factory.newSAXParser();
            SAXSource source = new SAXSource(new CoalescingXMLFilter(parser.getXMLReader()), inputSource);
            return OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), source, false);
        }
    };
    
    private BuilderFactory() {}
    
    /**
     * Determines if the builder created by this strategy supports deferred parsing.
     * 
     * @return <code>true</code> if the builder supports deferred parsing, <code>false</code> if the
     *         builder doesn't support deferred parsing and will build the document all in once
     *         (this is the case for SAX only)
     */
    public abstract boolean isDeferredParsing();
    
    public abstract void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator);
    
    public abstract OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception;
}
