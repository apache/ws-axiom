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
package org.apache.axiom.om.impl.jaxp;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestSuite;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.xml.sax.InputSource;

@SuppressWarnings({"rawtypes", "deprecation"})
public class StreamSourceToOMResultTest extends MatrixTestCase {
    private static final String[] axiomImplementations = {"default", "dom"};

    private final OMMetaFactory omMetaFactory;
    private final XMLSample file;

    private StreamSourceToOMResultTest(String axiomImplementation, XMLSample file) {
        omMetaFactory = OMAbstractFactory.getMetaFactory(axiomImplementation);
        this.file = file;
        addTestParameter("axiomImplementation", axiomImplementation);
        addTestParameter("file", file.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        StreamSource source = new StreamSource(file.getUrl().toString());
        OMResult result = new OMResult(omMetaFactory.getOMFactory());
        XSLTImplementation.XALAN.newTransformerFactory().newTransformer().transform(source, result);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        result.getDocument().serialize(out);
        InputSource actual = new InputSource();
        actual.setByteStream(new ByteArrayInputStream(out.toByteArray()));
        actual.setSystemId(file.getUrl().toString());
        assertAbout(xml())
                .that(actual)
                .ignoringWhitespaceInPrologAndEpilog()
                .expandingEntityReferences()
                .hasSameContentAs(file.getUrl());
    }

    public static TestSuite suite() {
        MatrixTestSuiteBuilder builder =
                new MatrixTestSuiteBuilder() {
                    @Override
                    protected void addTests() {
                        for (int i = 0; i < axiomImplementations.length; i++) {
                            for (Iterator it = Multiton.getInstances(XMLSample.class).iterator();
                                    it.hasNext(); ) {
                                addTest(
                                        new StreamSourceToOMResultTest(
                                                axiomImplementations[i], (XMLSample) it.next()));
                            }
                        }
                    }
                };
        builder.exclude("(|(file=sax-attribute-namespace-bug.xml)(file=large.xml))");
        return builder.build();
    }
}
