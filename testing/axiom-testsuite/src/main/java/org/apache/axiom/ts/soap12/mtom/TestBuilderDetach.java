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
package org.apache.axiom.ts.soap12.mtom;

import static org.apache.axiom.truth.AxiomTestVerb.ASSERT;

import java.util.Iterator;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.testutils.io.InstrumentedInputStream;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.soap.MTOMSample;

public class TestBuilderDetach extends AxiomTestCase {
    public TestBuilderDetach(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        MTOMSample sample = MTOMSample.SAMPLE1;
        InstrumentedInputStream in = new InstrumentedInputStream(sample.getInputStream());
        Attachments attachments = new Attachments(in, sample.getContentType());
        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, attachments);
        SOAPEnvelope envelope = builder.getSOAPEnvelope();
        long countBeforeDetach = in.getCount();
        builder.detach();
        ASSERT.that(in.getCount()).isGreaterThan(countBeforeDetach);
        ASSERT.that(in.isClosed()).isFalse();
        int binaryCount = 0;
        for (Iterator it = envelope.getDescendants(false); it.hasNext(); ) {
            OMNode node = (OMNode)it.next();
            if (node instanceof OMText) {
                OMText text = (OMText)node;
                if (text.isBinary()) {
                    IOTestUtils.compareStreams(
                            sample.getPart(text.getContentID()),
                            ((DataHandler)text.getDataHandler()).getInputStream());
                    binaryCount++;
                }
            }
        }
        ASSERT.that(binaryCount).isGreaterThan(0);
        in.close();
    }
}
