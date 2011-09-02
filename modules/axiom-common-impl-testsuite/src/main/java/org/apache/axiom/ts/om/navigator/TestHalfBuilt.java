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
package org.apache.axiom.ts.om.navigator;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.OMNavigator;

public class TestHalfBuilt extends OMNavigatorTestCase {
    public TestHalfBuilt(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        assertNotNull(envelope);
        //now the OM is not fully created. Try to navigate it
        OMNavigator navigator = new OMNavigator(envelope);
        OMSerializable node = null;
        while (navigator.isNavigable()) {
            node = navigator.next();
            assertNotNull(node);
        }
    }
}
