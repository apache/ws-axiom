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
package org.apache.axiom.ts.dom;

import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.w3c.domts.DOMTestCase;

public final class W3CTestCase extends MatrixTestCase {
    private DOMTestCase test;

    public W3CTestCase(DOMTestCase test) {
        this.test = test;
        addTestParameter("id", test.getTargetURI());
    }

    @Override
    protected void runTest() throws Throwable {
        test.runTest();
        int mutationCount = test.getMutationCount();
        if (mutationCount != 0) {
            fail("Document loaded with willBeModified='false' was modified in course of test.");
        }
    }
}
