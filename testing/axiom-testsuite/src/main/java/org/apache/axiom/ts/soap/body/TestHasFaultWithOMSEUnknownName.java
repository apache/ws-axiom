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

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests that a call to {@link SOAPBody#hasFault()} doesn't cause expansion of an {@link
 * OMSourcedElement} with an unknown name that is the first child of the SOAP body.
 */
public class TestHasFaultWithOMSEUnknownName extends SOAPTestCase {
    public TestHasFaultWithOMSEUnknownName(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        SOAPBody body = envelope.getBody();
        OMSourcedElement element =
                soapFactory.createOMElement(new StringOMDataSource("<ns:root xmlns:ns='urn:ns'/>"));
        body.addChild(element);
        assertFalse(body.hasFault());
        assertFalse(element.isExpanded());
    }
}
