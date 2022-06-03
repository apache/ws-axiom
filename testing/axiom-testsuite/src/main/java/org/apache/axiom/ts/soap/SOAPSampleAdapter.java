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
import org.apache.axiom.om.OMMetaFactorySPI;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.testing.multiton.AdapterType;
import org.junit.Assert;
import org.xml.sax.InputSource;

/**
 * {@link SOAPSample} adapter that adds some Axiom specific methods to retrieve the content of the
 * test message.
 */
@AdapterType
public final class SOAPSampleAdapter {
    private final SOAPSample sample;

    SOAPSampleAdapter(SOAPSample sample) {
        this.sample = sample;
    }

    public SOAPModelBuilder getBuilder(OMMetaFactory metaFactory) {
        return ((OMMetaFactorySPI) metaFactory)
                .createSOAPModelBuilder(new InputSource(sample.getInputStream()));
    }

    public SOAPMessage getSOAPMessage(OMMetaFactory metaFactory) {
        return getBuilder(metaFactory).getSOAPMessage();
    }

    public SOAPEnvelope getSOAPEnvelope(OMMetaFactory metaFactory) {
        SOAPEnvelope envelope = getBuilder(metaFactory).getSOAPEnvelope();
        // TODO: this is not the right place to assert this
        Assert.assertSame(
                sample.getSOAPSpec().getEnvelopeNamespaceURI(),
                ((SOAPFactory) envelope.getOMFactory()).getSoapVersionURI());
        return envelope;
    }
}
