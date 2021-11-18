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
package org.apache.axiom.core.stream.serializer;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.testing.multiton.Multiton.getInstances;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.core.stream.dom.input.DOMInput;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import junit.framework.TestSuite;

public class SerializerConformanceTest extends MatrixTestCase {
    private final XMLSample sample;

    public SerializerConformanceTest(XMLSample sample) {
        this.sample = sample;
        addTestParameter("sample", sample.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);
        Document document = factory.newDocumentBuilder().parse(sample.getUrl().toString());
        StringWriter sw = new StringWriter();
        XmlReader reader = new DOMInput(document, false).createReader(new Serializer(sw));
        while (!reader.proceed()) {
            // Just loop
        }
        InputSource is = new InputSource(new StringReader(sw.toString()));
        is.setSystemId(sample.getUrl().toString());
        assertAbout(xml())
            .that(is)
            .ignoringWhitespaceInPrologAndEpilog()
            .treatingElementContentWhitespaceAsText()  // TODO
            .hasSameContentAs(document);
    }

    public static TestSuite suite() {
        return new MatrixTestSuiteBuilder() {
            @Override
            protected void addTests() {
                for (XMLSample sample : getInstances(XMLSample.class)) {
                    addTest(new SerializerConformanceTest(sample));
                }
            }
        }.build();
    }
}
