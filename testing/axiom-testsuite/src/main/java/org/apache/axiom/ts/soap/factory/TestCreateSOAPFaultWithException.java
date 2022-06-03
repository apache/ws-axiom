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
package org.apache.axiom.ts.soap.factory;

import static com.google.common.truth.Truth.assertThat;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestCreateSOAPFaultWithException extends SOAPTestCase {
    private final boolean withParent;

    public TestCreateSOAPFaultWithException(
            OMMetaFactory metaFactory, SOAPSpec spec, boolean withParent) {
        super(metaFactory, spec);
        this.withParent = withParent;
        addTestParameter("withParent", withParent);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPBody body = withParent ? soapFactory.getDefaultEnvelope().getBody() : null;
        SOAPFault fault = soapFactory.createSOAPFault(body, new Exception("Testing soap fault"));
        if (body != null) {
            assertThat(body.hasFault()).isTrue();
            assertThat(body.getFault()).isSameInstanceAs(fault);
        }
        assertThat(fault.isComplete()).isTrue();
        SOAPFaultDetail detail = fault.getDetail();
        assertThat(detail).isNotNull();
        Iterator<OMElement> it = detail.getAllDetailEntries();
        assertThat(it.hasNext()).isTrue();
        OMElement entry = it.next();
        assertThat(entry.getQName())
                .isEqualTo(new QName(SOAPConstants.SOAP_FAULT_DETAIL_EXCEPTION_ENTRY));
        assertThat(entry.getText()).contains("Testing soap fault");
    }
}
