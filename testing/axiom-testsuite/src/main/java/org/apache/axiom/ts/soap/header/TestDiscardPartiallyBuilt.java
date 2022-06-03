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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

/**
 * Tests the behavior of {@link OMNode#discard()} on a {@link SOAPHeader} that is partially built,
 * more precisely in a situation where the builder is building a descendant that is not an immediate
 * child of the header.
 */
public class TestDiscardPartiallyBuilt extends SampleBasedSOAPTestCase {
    public TestDiscardPartiallyBuilt(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.WSA);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPHeader header = envelope.getHeader();
        OMElement from =
                header.getFirstChildWithName(
                        new QName("http://www.w3.org/2005/08/addressing", "ReplyTo"));
        from.getFirstElement().getFirstOMChild();
        // Just in case getFirstChildWithName or getFirstElement did stupid things
        assertFalse(from.isComplete());

        header.discard();
        OMElement body = envelope.getFirstElement();
        assertTrue(body instanceof SOAPBody);
    }
}
