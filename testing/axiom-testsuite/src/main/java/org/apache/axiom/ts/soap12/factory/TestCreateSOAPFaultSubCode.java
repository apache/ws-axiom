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
package org.apache.axiom.ts.soap12.factory;

import java.util.Iterator;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests {@link SOAPFactory#createSOAPFaultSubCode(SOAPFaultCode)} when used with a {@link
 * SOAPFaultCode} that has already a {@link SOAPFaultValue} child.
 */
public class TestCreateSOAPFaultSubCode extends SOAPTestCase {
    public TestCreateSOAPFaultSubCode(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSpec.SOAP12);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.getDefaultFaultEnvelope();
        SOAPFault fault = envelope.getBody().getFault();
        SOAPFaultCode code = fault.getCode();
        SOAPFaultSubCode subCode = soapFactory.createSOAPFaultSubCode(code);
        assertEquals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, subCode.getNamespaceURI());
        assertEquals(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME, subCode.getLocalName());
        assertSame(code, subCode.getParent());
        Iterator<OMNode> it = code.getChildren();
        assertTrue(it.hasNext());
        assertTrue(it.next() instanceof SOAPFaultValue);
        assertTrue(it.hasNext());
        assertSame(subCode, it.next());
        assertFalse(it.hasNext());
    }
}
