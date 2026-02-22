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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.apache.axiom.ts.jaxp.sax.SAXImplementation;
import org.xml.sax.InputSource;

/** Creates an {@link OMXMLParserWrapper} by passing a {@link SAXSource} to Axiom. */
final class SAXBuilderFactory extends BuilderFactory {
    private final SAXImplementation implementation;

    SAXBuilderFactory(SAXImplementation implementation) {
        this.implementation = implementation;
    }

    @Override
    public boolean isDeferredParsing() {
        return false;
    }

    @Override
    public void configureXMLStreamReaderComparator(XMLStreamReaderComparator comparator) {
        // SAX doesn't provide this information
        comparator.setCompareCharacterEncodingScheme(false);
        comparator.setCompareEncoding(false);
        // If the SAX implementation doesn't report the external subset boundaries correctly,
        // then the reported internal subset may be incorrect
        comparator.setCompareInternalSubset(implementation.reportsExternalSubsetEntity());
    }

    @Override
    public void addTestParameters(TestParameterTarget testCase) {
        testCase.addTestParameter("source", implementation.getName() + "-sax");
    }

    @Override
    public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory, InputSource inputSource)
            throws Exception {
        SAXParserFactory factory = implementation.newSAXParserFactory();
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        SAXParser parser = factory.newSAXParser();
        SAXSource source =
                new SAXSource(new CoalescingXMLFilter(parser.getXMLReader()), inputSource);
        return OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), source, false);
    }
}
