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
package org.apache.axiom.ts.om.cross;

import static org.apache.axiom.testing.multiton.Multiton.getInstances;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.xml.XMLSample;

public class CrossOMTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final OMMetaFactory metaFactory;
    private final OMMetaFactory altMetaFactory;

    public CrossOMTestSuiteBuilder(OMMetaFactory metaFactory, OMMetaFactory altMetaFactory) {
        this.metaFactory = metaFactory;
        this.altMetaFactory = altMetaFactory;
    }

    @Override
    protected void addTests() {
        addTest(new TestAddChild(metaFactory, altMetaFactory));
        for (XMLSample file : getInstances(XMLSample.class)) {
            addTest(new TestImportInformationItem(metaFactory, altMetaFactory, file));
        }
        addTest(new TestInsertSibling(metaFactory, altMetaFactory, false));
        addTest(new TestInsertSibling(metaFactory, altMetaFactory, true));
    }
}
