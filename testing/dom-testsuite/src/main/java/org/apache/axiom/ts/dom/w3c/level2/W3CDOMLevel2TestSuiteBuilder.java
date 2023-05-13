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
package org.apache.axiom.ts.dom.w3c.level2;

import org.apache.axiom.ts.dom.DocumentBuilderFactoryFactory;
import org.apache.axiom.ts.dom.w3c.DOMFeature;
import org.apache.axiom.ts.dom.w3c.W3CDOMTestSuiteBuilder;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.DOMTestSuite;
import org.w3c.domts.level2.core.alltests;

public final class W3CDOMLevel2TestSuiteBuilder extends W3CDOMTestSuiteBuilder {
    public W3CDOMLevel2TestSuiteBuilder(
            DocumentBuilderFactoryFactory dbff, DOMFeature... unsupportedFeatures) {
        super(dbff, unsupportedFeatures);
    }

    @Override
    protected DOMTestSuite createDOMTestSuite(DOMTestDocumentBuilderFactory factory)
            throws Exception {
        return new alltests(factory);
    }
}
