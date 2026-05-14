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
package org.apache.axiom.ts.soap.envelope;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

import com.google.inject.Inject;

/**
 * Test the behavior when adding a header to an envelope that has not yet been built completely.
 * This is a regression test for AXIOM-127.
 */
public class TestAddHeaderToIncompleteEnvelope extends SampleBasedSOAPTestCase {
    @Inject private SOAPFactory soapFactory;

    @Inject
    public TestAddHeaderToIncompleteEnvelope(SOAPSpec spec) {
        super(spec, SOAPSampleSet.NO_HEADER);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        assertThat(envelope.getHeader()).isNull();
        SOAPHeader header = soapFactory.createSOAPHeader(envelope);
        assertThat(envelope.getHeader()).isSameAs(header);
    }
}
