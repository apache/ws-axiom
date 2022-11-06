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
package org.apache.axiom.om.impl.llom;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactoryLoader;
import org.apache.axiom.ts.om.OMTestSuiteBuilder;
import org.apache.axiom.ts.om.document.TestDigest;
import org.apache.axiom.ts.om.sourcedelement.TestGetSAXSourceWithPushOMDataSource;

public class OMImplementationTest extends TestCase {
    public static TestSuite suite() {
        OMTestSuiteBuilder builder =
                new OMTestSuiteBuilder(new OMLinkedListMetaFactoryLoader().load(null));

        // TODO: if there is a comment node surrounded by text, then these text nodes need to be
        // merged
        builder.exclude(TestDigest.class, "(|(file=digest3.xml)(file=digest4.xml))");

        // TODO: need to evaluate if the test case is correct
        builder.exclude(
                TestGetSAXSourceWithPushOMDataSource.class,
                "(&(scenario=getNamespaceContext)(serializeParent=false))");

        return builder.build();
    }
}
