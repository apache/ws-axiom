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
package org.apache.axiom.core.stream.stax.pull.output;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.testing.multiton.Multiton.getInstances;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.core.stream.dom.input.DOMInput;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Document;

import junit.framework.TestSuite;

public class StAXPivotTransformerTest extends MatrixTestCase {
    private final XSLTImplementation xsltImplementation;
    private final XMLSample sample;

    public StAXPivotTransformerTest(XSLTImplementation xsltImplementation, XMLSample sample) {
        this.xsltImplementation = xsltImplementation;
        this.sample = sample;
        addTestParameter("xslt", xsltImplementation.getName());
        addTestParameter("sample", sample.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);
        factory.setCoalescing(true);
        factory.setIgnoringComments(true);
        Document document = factory.newDocumentBuilder().parse(sample.getUrl().toString());
        StAXPivot pivot = new StAXPivot(null);
        pivot.setReader(new DOMInput(document, false).createReader(pivot));
        StringWriter sw = new StringWriter();
        xsltImplementation
                .newTransformerFactory()
                .newTransformer()
                .transform(new StAXSource(pivot), new StreamResult(sw));
        assertAbout(xml()).that(sw.toString()).hasSameContentAs(document);
    }

    public static TestSuite suite() {
        return new MatrixTestSuiteBuilder() {
            @Override
            protected void addTests() {
                for (XSLTImplementation xsltImplementation :
                        getInstances(XSLTImplementation.class)) {
                    if (xsltImplementation.supportsStAXSource()) {
                        for (XMLSample sample : getInstances(XMLSample.class)) {
                            if (!sample.hasDTD()) {
                                addTest(new StAXPivotTransformerTest(xsltImplementation, sample));
                            }
                        }
                    }
                }
            }
        }.build();
    }
}
