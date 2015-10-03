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
package org.apache.axiom.ts.fom;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;

public class FOMTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final Abdera abdera;

    public FOMTestSuiteBuilder(Abdera abdera) {
        this.abdera = abdera;
    }

    @Override
    protected void addTests() {
        addTest(new org.apache.axiom.ts.fom.attribute.TestGetFactory(abdera));
        addTest(new org.apache.axiom.ts.fom.attribute.TestSetAttributeValueQNameNew(abdera, new QName("attr")));
        addTest(new org.apache.axiom.ts.fom.attribute.TestSetAttributeValueQNameNew(abdera, new QName("urn:test", "attr")));
        addTest(new org.apache.axiom.ts.fom.attribute.TestSetAttributeValueQNameNew(abdera, new QName("urn:test", "attr", "p")));
        addTest(new org.apache.axiom.ts.fom.attribute.TestSetAttributeValueQNameRemove(abdera));
        addTest(new org.apache.axiom.ts.fom.collection.TestSetAccept(abdera));
        addTest(new org.apache.axiom.ts.fom.collection.TestSetAcceptRemove(abdera));
        addTest(new org.apache.axiom.ts.fom.control.TestIsDraft(abdera));
        addTest(new org.apache.axiom.ts.fom.control.TestSetUnsetDraft(abdera));
        addTest(new org.apache.axiom.ts.fom.entry.TestAddCategoryFromCategories(abdera));
        addTest(new org.apache.axiom.ts.fom.entry.TestGetCategoriesByScheme(abdera));
        addTest(new org.apache.axiom.ts.fom.feed.TestAddAuthorWithExistingEntry1(abdera));
        addTest(new org.apache.axiom.ts.fom.feed.TestAddAuthorWithExistingEntry2(abdera));
        addTest(new org.apache.axiom.ts.fom.person.TestSetEmail(abdera));
    }
}
