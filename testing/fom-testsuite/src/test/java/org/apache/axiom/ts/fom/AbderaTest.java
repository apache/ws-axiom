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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.abdera.Abdera;
import org.apache.axiom.ts.fom.collection.TestSetAcceptRemove;
import org.apache.axiom.ts.fom.control.TestSetUnsetDraft;
import org.apache.axiom.ts.fom.feed.TestAddAuthorWithExistingEntry2;

public class AbderaTest extends TestCase {
    public static TestSuite suite() {
        FOMTestSuiteBuilder builder = new FOMTestSuiteBuilder(new Abdera());
        
        // Doesn't work because _setElementValue creates an OMElementImpl instead of a FOMElement
        builder.exclude(TestSetUnsetDraft.class);
        // Fails with ConcurrentModificationException
        builder.exclude(TestSetAcceptRemove.class);
        // Broken in Abdera 1.1.3
        builder.exclude(TestAddAuthorWithExistingEntry2.class);
        
        return builder.build();
    }
}
