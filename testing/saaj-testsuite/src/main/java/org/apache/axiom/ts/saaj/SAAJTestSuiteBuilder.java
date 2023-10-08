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
package org.apache.axiom.ts.saaj;

import jakarta.xml.soap.SAAJMetaFactory;

import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.saaj.body.TestAddChildElementReification;
import org.apache.axiom.ts.saaj.element.TestAddChildElementLocalName;
import org.apache.axiom.ts.saaj.element.TestAddChildElementLocalNamePrefixAndURI;
import org.apache.axiom.ts.saaj.element.TestGetOwnerDocument;
import org.apache.axiom.ts.saaj.element.TestSetParentElement;
import org.apache.axiom.ts.saaj.header.TestExamineMustUnderstandHeaderElements;
import org.apache.axiom.ts.soap.SOAPSpec;

public class SAAJTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final SAAJImplementation saajImplementation;

    public SAAJTestSuiteBuilder(SAAJMetaFactory metaFactory) {
        saajImplementation = new SAAJImplementation(metaFactory);
    }

    @Override
    protected void addTests() {
        addTests(SOAPSpec.SOAP11);
        addTests(SOAPSpec.SOAP12);
    }

    private void addTests(SOAPSpec spec) {
        addTest(new TestExamineMustUnderstandHeaderElements(saajImplementation, spec));
        addTest(new TestAddChildElementLocalName(saajImplementation, spec));
        addTest(new TestAddChildElementLocalNamePrefixAndURI(saajImplementation, spec));
        addTest(new TestSetParentElement(saajImplementation, spec));
        addTest(new TestGetOwnerDocument(saajImplementation, spec));
        addTest(new TestAddChildElementReification(saajImplementation, spec));
    }
}
