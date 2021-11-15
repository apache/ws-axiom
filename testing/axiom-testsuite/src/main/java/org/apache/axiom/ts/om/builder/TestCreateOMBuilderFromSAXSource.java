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
package org.apache.axiom.ts.om.builder;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.jaxp.sax.SAXImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.xml.sax.InputSource;

public class TestCreateOMBuilderFromSAXSource extends ConformanceTestCase {
    private final SAXImplementation implementation;
    private final Boolean expandEntityReferences;
    
    public TestCreateOMBuilderFromSAXSource(OMMetaFactory metaFactory, XMLSample file,
            SAXImplementation implementation, Boolean expandEntityReferences) {
        super(metaFactory, file);
        this.implementation = implementation;
        addTestParameter("implementation", implementation.getName());
        this.expandEntityReferences = expandEntityReferences;
        if (expandEntityReferences != null) {
            addTestParameter("expandEntityReferences", expandEntityReferences.booleanValue());
        }
    }

    @Override
    protected void runTest() throws Throwable {
        SAXParserFactory factory = implementation.newSAXParserFactory();
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        SAXParser parser = factory.newSAXParser();
        SAXSource source = new SAXSource(parser.getXMLReader(), new InputSource(file.getUrl().toString()));
        OMXMLParserWrapper builder;
        if (expandEntityReferences == null) {
            builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), source);
        } else {
            builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), source,
                    expandEntityReferences.booleanValue());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        builder.getDocument().serialize(baos);
        InputSource actual = new InputSource();
        actual.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
        actual.setSystemId(file.getUrl().toString());
        assertAbout(xml())
                .that(actual)
                .ignoringWhitespaceInPrologAndEpilog()
                .expandingEntityReferences(expandEntityReferences == null ? false : expandEntityReferences.booleanValue())
                .hasSameContentAs(
                        DOMImplementation.XERCES.parse(
                                new InputSource(file.getUrl().toString()),
                                expandEntityReferences == null || expandEntityReferences));
    }
}
