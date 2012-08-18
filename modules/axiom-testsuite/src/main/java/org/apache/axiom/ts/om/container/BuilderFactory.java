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
package org.apache.axiom.ts.om.container;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Defines a strategy to create an {@link OMXMLParserWrapper} from a given test file.
 */
public interface BuilderFactory {
    /**
     * Creates an {@link OMXMLParserWrapper} directly from the given {@link InputSource}, i.e. let
     * instantiate an appropriate parser.
     */
    BuilderFactory PARSER = new BuilderFactory() {
        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("source", "parser");
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
    BuilderFactory DOM = new BuilderFactory() {
        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("source", "dom");
        }

        public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setExpandEntityReferences(false);
            Document document = dbf.newDocumentBuilder().parse(inputSource);
            return OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), document, false);
        }
    };
    
    void addTestProperties(AxiomTestCase testCase);
    
    OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception;
}
