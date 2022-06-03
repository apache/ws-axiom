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
package org.apache.axiom.ts.soap.builder;

import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.custombuilder.BlobOMDataSourceCustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

/**
 * Tests that a custom builder registered with {@link
 * CustomBuilderSupport#registerCustomBuilderForPayload(CustomBuilder)} is still taken into account
 * after using {@link SOAPBody#hasFault()}. This assumes that the Axiom implementation supports the
 * optimization described by <a
 * href="https://issues.apache.org/jira/browse/AXIOM-282">AXIOM-282</a>.
 */
public class TestRegisterCustomBuilderForPayloadAfterSOAPFaultCheck
        extends SampleBasedSOAPTestCase {
    public TestRegisterCustomBuilderForPayloadAfterSOAPFaultCheck(
            OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.WSA);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPModelBuilder builder = (SOAPModelBuilder) envelope.getBuilder();

        // Do a fault check.  This is normally done in the engine (Axiom) and should
        // not cause inteference with the custom builder processing
        envelope.getBody().hasFault();

        // Do the registration here...this simulates when it could occure in the engine
        // (After the fault check and during phase processing...probably dispatch phase)
        ((CustomBuilderSupport) builder)
                .registerCustomBuilder(
                        CustomBuilder.Selector.PAYLOAD,
                        new BlobOMDataSourceCustomBuilder(MemoryBlob.FACTORY, "utf-8"));

        OMElement bodyElement = envelope.getBody().getFirstElement();
        assertTrue(bodyElement instanceof OMSourcedElement);
    }
}
