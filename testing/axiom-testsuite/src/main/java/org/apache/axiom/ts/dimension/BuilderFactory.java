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

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactorySPI;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.jaxp.sax.SAXImplementation;
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
        @Override
        public boolean isDeferredParsing() {
            return true;
        }

        @Override
        public void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator) {
        }

        @Override
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("source", "parser");
        }

        @Override
        public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource) throws Exception {
            return ((OMMetaFactorySPI)metaFactory).createOMBuilder(AxiomTestCase.TEST_PARSER_CONFIGURATION,
                    inputSource);
        }
    };

    BuilderFactory() {}
    
    @Instances
    private static BuilderFactory[] instances() {
        List<BuilderFactory> instances = new ArrayList<>();
        for (DOMImplementation implementation : getInstances(DOMImplementation.class)) {
            instances.add(new DOMBuilderFactory(implementation));
        }
        for (SAXImplementation implementation : getInstances(SAXImplementation.class)) {
            instances.add(new SAXBuilderFactory(implementation));
        }
        return instances.toArray(new BuilderFactory[instances.size()]);
    }
    
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
