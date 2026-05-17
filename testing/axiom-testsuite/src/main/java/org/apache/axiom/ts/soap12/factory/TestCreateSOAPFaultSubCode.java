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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests {@link SOAPFactory#createSOAPFaultSubCode(SOAPFaultCode)} when used with a {@link
 * SOAPFaultCode} that has already a {@link SOAPFaultValue} child.
 */
public class TestCreateSOAPFaultSubCode implements MatrixTestCase {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.getDefaultFaultEnvelope();
        SOAPFault fault = envelope.getBody().getFault();
        SOAPFaultCode code = fault.getCode();
        SOAPFaultSubCode subCode = soapFactory.createSOAPFaultSubCode(code);
        assertThat(subCode.getNamespaceURI()).isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(subCode.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME);
        assertThat(subCode.getParent()).isSameAs(code);
        Iterator<OMNode> it = code.getChildren();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isInstanceOf(SOAPFaultValue.class);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(subCode);
        assertThat(it.hasNext()).isFalse();
    }
}
