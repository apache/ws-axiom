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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.testutils.suite.MatrixTestCase;

public class TestSetException implements MatrixTestCase {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void runTest() throws Throwable {
        SOAPFault fault = soapFactory.createSOAPFault();
        Exception exception = new Exception("Test exception message");
        fault.setException(exception);
        SOAPFaultDetail detail = fault.getDetail();
        assertThat(detail).isNotNull();
        Iterator<OMElement> it = detail.getAllDetailEntries();
        assertThat(it.hasNext()).isTrue();
        OMElement entry = it.next();
        assertThat(entry).isNotNull();
        assertThat(entry.getLocalName()).isEqualTo(SOAPConstants.SOAP_FAULT_DETAIL_EXCEPTION_ENTRY);
        assertThat(entry.getNamespace()).isNull();
        String text = entry.getText();
        assertThat(text).startsWith(Exception.class.getName() + ": Test exception message");
        assertThat(text).contains("at " + TestSetException.class.getName());
        assertThat(it.hasNext()).isFalse();
    }
}
