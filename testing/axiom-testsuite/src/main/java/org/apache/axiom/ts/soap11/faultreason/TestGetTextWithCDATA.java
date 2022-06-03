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
package org.apache.axiom.ts.soap11.faultreason;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestGetTextWithCDATA extends SOAPTestCase {
    public TestGetTextWithCDATA(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSpec.SOAP11);
    }

    @Override
    protected void runTest() throws Throwable {
        String soap11Fault =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                        + "<SOAP-ENV:Body>"
                        + "<SOAP-ENV:Fault>"
                        + "<faultcode>SOAP-ENV:Server</faultcode>"
                        + "<faultstring xml:lang=\"en\"><![CDATA[handleMessage throws SOAPFaultException for ThrowsSOAPFaultToClientHandlersTest]]></faultstring>"
                        + "<detail>"
                        + "<somefaultentry/>"
                        + "</detail>"
                        + "<faultactor>faultActor</faultactor>"
                        + "</SOAP-ENV:Fault>"
                        + "</SOAP-ENV:Body>"
                        + "</SOAP-ENV:Envelope>";
        XMLStreamReader soap11Parser =
                StAXUtils.createXMLStreamReader(
                        TEST_PARSER_CONFIGURATION, new StringReader(soap11Fault));
        SOAPModelBuilder soap11Builder =
                OMXMLBuilderFactory.createStAXSOAPModelBuilder(metaFactory, soap11Parser);
        OMElement element = soap11Builder.getDocumentElement();
        element.build();
        assertTrue(element instanceof SOAPEnvelope);
        SOAPEnvelope se = (SOAPEnvelope) element;
        SOAPFault fault = se.getBody().getFault();
        SOAPFaultReason reason = fault.getReason();
        assertTrue(
                reason.getText()
                        .equals(
                                "handleMessage throws SOAPFaultException for ThrowsSOAPFaultToClientHandlersTest"));
        soap11Parser.close();
    }
}
