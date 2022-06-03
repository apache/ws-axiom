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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Test that attempts to add an arbitrary element to the SOAP envelope (after the body). This is
 * allowed in SOAP 1.1, but not in SOAP 1.2.
 */
public class TestAddElementAfterBody extends SOAPTestCase {
    private final boolean header;

    public TestAddElementAfterBody(OMMetaFactory metaFactory, SOAPSpec spec, boolean header) {
        super(metaFactory, spec);
        this.header = header;
        addTestParameter("header", header);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope env = soapFactory.createSOAPEnvelope();
        if (header) {
            soapFactory.createSOAPHeader(env);
        }
        soapFactory.createSOAPBody(env);
        OMElement elem = env.getOMFactory().createOMElement(new QName("foo"));
        if (spec.isAllowsElementsAfterBody()) {
            env.addChild(elem);
        } else {
            try {
                env.addChild(elem);
                fail("Expected SOAPProcessingException");
            } catch (SOAPProcessingException ex) {
                // expected
            }
        }
    }
}
