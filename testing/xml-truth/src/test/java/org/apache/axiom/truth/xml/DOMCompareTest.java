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
package org.apache.axiom.truth.xml;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.testing.multiton.Multiton.getInstances;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestSuite;

import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.xml.XMLSample;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

public class DOMCompareTest extends MatrixTestCase {
    private XMLSample sample;
    private boolean expandEntityReferences;

    public DOMCompareTest(XMLSample sample, boolean expandEntityReferences) {
        this.sample = sample;
        this.expandEntityReferences = expandEntityReferences;
        addTestParameter("sample", sample.getName());
        addTestParameter("expandEntityReferences", expandEntityReferences);
    }
    
    @Override
    protected void runTest() throws Throwable {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        // If necessary, let DOMTraverser expand entity references
        factory.setExpandEntityReferences(false);
        assertAbout(xml())
                .that(xml(factory.newDocumentBuilder().parse(sample.getUrl().toString())))
                .ignoringWhitespaceInPrologAndEpilog()
                .expandingEntityReferences(expandEntityReferences)
                .hasSameContentAs(xml(sample.getUrl()));
    }

    public static TestSuite suite() {
        return new MatrixTestSuiteBuilder() {
            @Override
            protected void addTests() {
                for (XMLSample sample : getInstances(XMLSample.class)) {
                    addTest(new DOMCompareTest(sample, true));
                    addTest(new DOMCompareTest(sample, false));
                }
            }
        }.build();
    }
}
