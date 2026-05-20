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
package org.apache.axiom.ts.soap12.fault;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.ts.soap.AltSOAPFactory;
import org.junit.jupiter.api.function.Executable;

public class TestSetNode implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Inject
    @AltSOAPFactory
    private SOAPFactory altSoapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        soapFault.setNode(soapFactory.createSOAPFaultNode(soapFault));
        assertThat(soapFault.getNode()).isNotNull();
        assertThat(soapFault.getNode().getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME);
        assertThatThrownBy(() -> soapFault.setNode(altSoapFactory.createSOAPFaultNode()))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
