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

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests that {@link SOAPBody#getFirstElementLocalName()} returns the expected result if the parser
 * has already progressed past the start of the payload and the optimization described in <a
 * href="https://issues.apache.org/jira/browse/AXIOM-282">AXIOM-282</a> is no longer applicable.
 */
public class TestGetFirstElementLocalNameWithParserNoLookahead extends SOAPTestCase {
    public TestGetFirstElementLocalNameWithParserNoLookahead(
            OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope orgEnvelope = soapFactory.getDefaultEnvelope();
        OMElement payload =
                soapFactory.createOMElement(
                        "payload",
                        soapFactory.createOMNamespace("urn:test", "p"),
                        orgEnvelope.getBody());
        OMElement child = soapFactory.createOMElement("child", null, payload);
        soapFactory.createOMElement("grandchild", null, child);
        SOAPModelBuilder builder =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                        metaFactory, new StringReader(orgEnvelope.toString()));
        SOAPBody body = builder.getSOAPEnvelope().getBody();
        body.getFirstElement().getFirstElement();
        assertThat(body.getFirstElementLocalName()).isEqualTo("payload");
    }
}
