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

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.xml.sax.InputSource;

/**
 * Creates an {@link OMXMLParserWrapper} by parsing the input using DOM and passing it as a DOM tree
 * to Axiom.
 */
final class DOMBuilderFactory extends BuilderFactory {
    private final DOMImplementation implementation;

    DOMBuilderFactory(DOMImplementation implementation) {
        this.implementation = implementation;
    }

    @Override
    public boolean isDeferredParsing() {
        return true;
    }

    @Override
    public void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator) {
        // DOM gives access to the parsed replacement value (via the Entity interface), but Axiom
        // stores the unparsed replacement value. Therefore OMEntityReference#getReplacementText()
        // returns null for nodes created from a DOM tree.
        comparator.setCompareEntityReplacementValue(false);
        // DOM (or at least Xerces) sorts attributes
        comparator.setSortAttributes(true);
    }

    @Override
    public void addTestParameters(TestParameterTarget testCase) {
        testCase.addTestParameter("source", implementation.getName() + "-dom");
    }

    @Override
    public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource)
            throws Exception {
        return OMXMLBuilderFactory.createOMBuilder(
                metaFactory.getOMFactory(), implementation.parse(inputSource, false), false);
    }
}
