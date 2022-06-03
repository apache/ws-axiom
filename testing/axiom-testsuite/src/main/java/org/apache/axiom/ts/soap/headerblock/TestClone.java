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

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestClone extends SOAPTestCase {
    private final Boolean processed;

    public TestClone(OMMetaFactory metaFactory, SOAPSpec spec, Boolean processed) {
        super(metaFactory, spec);
        this.processed = processed;
        addTestParameter("processed", String.valueOf(processed));
    }

    private void checkProcessed(SOAPHeader clonedHeader, SOAPHeaderBlock orgHeaderBlock) {
        SOAPHeaderBlock clonedHeaderBlock =
                (SOAPHeaderBlock) clonedHeader.getFirstChildWithName(orgHeaderBlock.getQName());
        assertThat(clonedHeaderBlock.isProcessed())
                .isEqualTo(processed == null ? orgHeaderBlock.isProcessed() : processed);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeader header = soapFactory.createSOAPHeader();
        OMNamespace ns = soapFactory.createOMNamespace("urn:test", "p");
        SOAPHeaderBlock unprocessedHeaderBlock = header.addHeaderBlock("unprocessed", ns);
        SOAPHeaderBlock processedHeaderBlock = header.addHeaderBlock("processed", ns);
        processedHeaderBlock.setProcessed();
        SOAPCloneOptions options = new SOAPCloneOptions();
        options.setPreserveModel(true);
        options.setProcessedFlag(processed);
        SOAPHeader clonedHeader = (SOAPHeader) header.clone(options);
        checkProcessed(clonedHeader, unprocessedHeaderBlock);
        checkProcessed(clonedHeader, processedHeaderBlock);
    }
}
