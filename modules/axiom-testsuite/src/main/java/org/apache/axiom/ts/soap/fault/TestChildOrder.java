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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.ts.strategy.serialization.SerializationStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tests that the children added using methods such as {@link SOAPFault#setCode(SOAPFaultCode)} and
 * {@link SOAPFault#setReason(SOAPFaultReason)} appear in the order required by the SOAP specs when
 * the {@link SOAPFault} is serialized.
 * <p>
 * Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-392">AXIOM-392</a>.
 */
public class TestChildOrder extends SOAPTestCase {
    private final SerializationStrategy serializationStrategy;

    public TestChildOrder(OMMetaFactory metaFactory, SOAPSpec spec, SerializationStrategy serializationStrategy) {
        super(metaFactory, spec);
        this.serializationStrategy = serializationStrategy;
        serializationStrategy.addTestProperties(this);
    }

    protected void runTest() throws Throwable {
        SOAPFault fault = soapFactory.createSOAPFault();
        // Add fault code and reason in the "wrong" order
        SOAPFaultReason reason = soapFactory.createSOAPFaultReason(); 
        reason.setText("Invalid credentials"); 
        fault.setReason(reason); 
        SOAPFaultCode code = soapFactory.createSOAPFaultCode(); 
        code.setText(new QName(soapFactory.getNamespace().getNamespaceURI(), "Client")); 
        fault.setCode(code);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        // Check the result using the given serialization strategy
        Document document = dbf.newDocumentBuilder().parse(serializationStrategy.serialize(fault).getInputSource());
        Element domFault = document.getDocumentElement();
        NodeList children = domFault.getChildNodes();
        assertEquals(2, children.getLength());
        assertEquals(spec.getFaultCodeQName().getLocalPart(), children.item(0).getLocalName());
        assertEquals(spec.getFaultReasonQName().getLocalPart(), children.item(1).getLocalName());
    }
}
