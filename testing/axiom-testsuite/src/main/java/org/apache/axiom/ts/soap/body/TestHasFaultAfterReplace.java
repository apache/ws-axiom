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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests that {@link SOAPBody#hasFault()} returns the correct value after a {@link SOAPFault} child
 * has been replaced by an {@link OMElement} that is not a {@link SOAPFault}. Earlier versions of
 * Axiom attempted to cache the result of {@link SOAPBody#hasFault()}, but this cached value was not
 * updated correctly in all situations. This is a regression test for this issue.
 */
public class TestHasFaultAfterReplace extends SOAPTestCase {
    public TestHasFaultAfterReplace(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    protected void runTest() throws Throwable {
        SOAPBody body = soapFactory.getDefaultFaultEnvelope().getBody();
        assertTrue(body.hasFault());
        body.getFault().detach();
        soapFactory.createOMElement("echo", soapFactory.createOMNamespace("urn:test", "echo"));
        assertFalse(body.hasFault());
    }
}
