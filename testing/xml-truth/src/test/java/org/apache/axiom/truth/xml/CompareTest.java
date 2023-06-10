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

import junit.framework.TestSuite;

import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.xml.XMLSample;

public class CompareTest extends MatrixTestCase {
    private XMLSample sample;
    private final XMLObjectFactory left;
    private final XMLObjectFactory right;
    private boolean expandEntityReferences;

    public CompareTest(
            XMLSample sample,
            XMLObjectFactory left,
            XMLObjectFactory right,
            boolean expandEntityReferences) {
        this.sample = sample;
        this.left = left;
        this.right = right;
        this.expandEntityReferences = expandEntityReferences;
        addTestParameter("sample", sample.getName());
        addTestParameter("left", left.getName());
        addTestParameter("right", right.getName());
        addTestParameter("expandEntityReferences", expandEntityReferences);
    }

    @Override
    protected void runTest() throws Throwable {
        assertAbout(xml())
                .that(left.toXMLObject(sample))
                .ignoringWhitespaceInPrologAndEpilog()
                .expandingEntityReferences(expandEntityReferences)
                .hasSameContentAs(right.toXMLObject(sample));
    }

    public static TestSuite suite() {
        return new MatrixTestSuiteBuilder() {
            @Override
            protected void addTests() {
                for (XMLSample sample : getInstances(XMLSample.class)) {
                    for (XMLObjectFactory left : getInstances(XMLObjectFactory.class)) {
                        for (XMLObjectFactory right : getInstances(XMLObjectFactory.class)) {
                            addTest(new CompareTest(sample, left, right, true));
                            addTest(new CompareTest(sample, left, right, false));
                        }
                    }
                }
            }
        }.build();
    }
}
