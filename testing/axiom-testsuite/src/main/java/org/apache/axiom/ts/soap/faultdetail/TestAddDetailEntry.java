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
package org.apache.axiom.ts.soap.faultdetail;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultDetail;

// SOAP Fault Detail Test (Programaticaly Created)
public class TestAddDetailEntry extends TestCase {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPBody body = soapFactory.createSOAPBody(envelope);
        SOAPFault fault = soapFactory.createSOAPFault(body);
        SOAPFaultDetail soapFaultDetail = soapFactory.createSOAPFaultDetail(fault);
        OMNamespace omNamespace = soapFactory.createOMNamespace("http://www.test.org", "test");
        soapFaultDetail.addDetailEntry(soapFactory.createOMElement("DetailEntry1", omNamespace));
        soapFaultDetail.addDetailEntry(soapFactory.createOMElement("DetailEntry2", omNamespace));
        Iterator<OMElement> iterator = soapFaultDetail.getAllDetailEntries();
        OMElement detailEntry1 = iterator.next();
        assertThat(detailEntry1).isNotNull();
        assertThat(detailEntry1.getLocalName()).isEqualTo("DetailEntry1");
        assertThat(detailEntry1.getNamespace().getNamespaceURI()).isEqualTo("http://www.test.org");
        OMElement detailEntry2 = iterator.next();
        assertThat(detailEntry2).isNotNull();
        assertThat(detailEntry2.getLocalName()).isEqualTo("DetailEntry2");
        assertThat(detailEntry2.getNamespace().getNamespaceURI()).isEqualTo("http://www.test.org");
        assertThat(iterator.hasNext()).isFalse();
    }
}
