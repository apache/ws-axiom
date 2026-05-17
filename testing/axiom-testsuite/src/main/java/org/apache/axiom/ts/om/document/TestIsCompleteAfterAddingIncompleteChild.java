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
package org.apache.axiom.ts.om.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.StringReader;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests that the completeness status (as returned by {@link OMDocument#isComplete()}) is updated
 * correctly after an incomplete child is added to a programmatically created document. This is a
 * regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-315">AXIOM-315</a>.
 */
public class TestIsCompleteAfterAddingIncompleteChild implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMElement incompleteElement = OMXMLBuilderFactory.createOMBuilder(
                        factory, new StringReader("<elem>text</elem>"))
                .getDocumentElement(true);
        OMDocument document = factory.createOMDocument();
        assertThat(document.isComplete()).isTrue();
        document.addChild(incompleteElement);
        assertThat(document.isComplete()).isFalse();
    }
}
