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
package org.apache.axiom.ts.soap.header;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

public class TestGetHeaderBlocksWithNSURIWithParser extends SampleBasedSOAPTestCase {
    public TestGetHeaderBlocksWithNSURIWithParser(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.HEADERS);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        ArrayList<SOAPHeaderBlock> headerBlocks =
                envelope.getHeader().getHeaderBlocksWithNSURI("urn:ns2");
        assertThat(headerBlocks).hasSize(2);
        assertThat(headerBlocks.get(0).getQName()).isEqualTo(new QName("urn:ns2", "h4"));
        assertThat(headerBlocks.get(1).getQName()).isEqualTo(new QName("urn:ns2", "h6"));
    }
}
