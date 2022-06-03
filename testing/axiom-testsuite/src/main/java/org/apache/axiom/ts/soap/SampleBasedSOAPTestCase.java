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
package org.apache.axiom.ts.soap;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;

public abstract class SampleBasedSOAPTestCase extends SOAPTestCase {
    private final SOAPSample sample;

    public SampleBasedSOAPTestCase(
            OMMetaFactory metaFactory, SOAPSpec spec, SOAPSampleSet sampleSet) {
        super(metaFactory, spec);
        sample = sampleSet.getMessage(spec);
    }

    public SampleBasedSOAPTestCase(OMMetaFactory metaFactory, SOAPSample sample) {
        super(metaFactory, sample.getSOAPSpec());
        this.sample = sample;
    }

    @Override
    protected final void runTest() throws Throwable {
        runTest(sample.getAdapter(SOAPSampleAdapter.class).getSOAPEnvelope(metaFactory));
    }

    protected abstract void runTest(SOAPEnvelope envelope) throws Throwable;
}
