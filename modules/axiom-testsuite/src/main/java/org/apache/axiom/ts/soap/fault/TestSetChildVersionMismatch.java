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
package org.apache.axiom.ts.soap.fault;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.ts.soap.SOAPFaultChild;
import org.apache.axiom.ts.soap.SOAPFaultChildAdapter;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestSetChildVersionMismatch extends SOAPTestCase {
    private final SOAPFaultChild type;

    public TestSetChildVersionMismatch(OMMetaFactory metaFactory, SOAPSpec spec, SOAPFaultChild type) {
        super(metaFactory, spec);
        this.type = type;
        type.getAdapter(SOAPFaultChildAdapter.class).addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPFaultChildAdapter adapter = type.getAdapter(SOAPFaultChildAdapter.class);
        SOAPFault fault = soapFactory.createSOAPFault();
        OMElement child = adapter.create(altSoapFactory);
        try {
            adapter.set(fault, child);
            fail("Expected SOAPProcessingException");
        } catch (SOAPProcessingException ex) {
            // Expected
        }
    }
}
