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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

/**
 * Tests the behavior of {@link OMContainer#getXMLStreamReaderWithoutCaching()} on a {@link
 * SOAPEnvelope} with a partially built {@link SOAPHeaderBlock}. A {@link SOAPHeaderBlock} is an
 * {@link OMSourcedElement}, but if it is not linked to a {@link OMDataSource} then it should behave
 * like a plain {@link OMElement}. For {@link OMContainer#getXMLStreamReaderWithoutCaching()} this
 * means that consuming the reader should not build the {@link SOAPHeaderBlock}.
 */
public class TestGetXMLStreamReaderWithoutCachingWithPartiallyBuiltHeaderBlock
        extends SampleBasedSOAPTestCase {
    public TestGetXMLStreamReaderWithoutCachingWithPartiallyBuiltHeaderBlock(
            OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.WSA);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPHeaderBlock headerBlock =
                (SOAPHeaderBlock)
                        envelope.getHeader()
                                .getFirstChildWithName(
                                        new QName(
                                                "http://www.w3.org/2005/08/addressing", "ReplyTo"));
        headerBlock.getFirstElement().getFirstOMChild();
        assertFalse(headerBlock.isComplete());
        XMLStreamReader reader = envelope.getXMLStreamReaderWithoutCaching();
        while (reader.hasNext()) {
            reader.next();
        }
        assertFalse(headerBlock.isComplete());
    }
}
