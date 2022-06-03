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
package org.apache.axiom.ts.soap.headerblock;

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Test cloning a {@link SOAPHeaderBlock} flagged as processed, but without preserving the model. In
 * this case the result is a plain {@link OMElement} instance and the processed flag is ignored.
 *
 * <p>This is a regression test for an issue in older Axiom versions.
 */
public class TestCloneProcessedWithoutPreservingModel extends SOAPTestCase {
    public TestCloneProcessedWithoutPreservingModel(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeaderBlock headerBlock =
                soapFactory.createSOAPHeaderBlock(
                        "test", soapFactory.createOMNamespace("urn:test", "p"));
        headerBlock.setProcessed();
        OMElement clone = (OMElement) headerBlock.clone(new SOAPCloneOptions());
        assertThat(clone).isNotInstanceOf(SOAPHeaderBlock.class);
    }
}
