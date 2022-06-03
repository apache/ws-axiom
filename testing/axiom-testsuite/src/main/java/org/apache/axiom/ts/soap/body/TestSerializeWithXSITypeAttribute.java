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
package org.apache.axiom.ts.soap.body;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactorySPI;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.dimension.serialization.XML;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

/** Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-107">AXIOM-107</a>. */
public class TestSerializeWithXSITypeAttribute extends SampleBasedSOAPTestCase {
    private final SerializationStrategy serializationStrategy;

    public TestSerializeWithXSITypeAttribute(
            OMMetaFactory metaFactory, SOAPSpec spec, SerializationStrategy serializationStrategy) {
        super(metaFactory, spec, SOAPSampleSet.XSI_TYPE);
        this.serializationStrategy = serializationStrategy;
        serializationStrategy.addTestParameters(this);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        // Serialize the SOAP body; this should generate an additional namespace declaration
        // to for the namespace used in the value of the xsi:type attribute.
        XML xml = serializationStrategy.serialize(envelope.getBody());
        // No deserialize the result and test that the attribute value can be resolved.
        OMElement element =
                ((OMMetaFactorySPI) metaFactory)
                        .createOMBuilder(StAXParserConfiguration.DEFAULT, xml.getInputSource())
                        .getDocumentElement()
                        .getFirstElement()
                        .getFirstElement();
        assertThat(
                        element.resolveQName(
                                element.getAttributeValue(
                                        new QName(
                                                "http://www.w3.org/2001/XMLSchema-instance",
                                                "type"))))
                .isEqualTo(new QName("http://ws.apache.org/axis2/user", "myData"));
    }
}
