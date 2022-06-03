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
package org.apache.axiom.ts.soap11.builder;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.AxiomTestCase;

public class TestBuilder extends AxiomTestCase {
    public TestBuilder(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        String soap11Message =
                "<?xml version='1.0' ?>"
                        + "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                        + "   <env:Header>\n"
                        + "       <test:echoOk xmlns:test=\"http://example.org/ts-tests\"\n"
                        + "                    env:actor=\"http://schemas.xmlsoap.org/soap/actor/next\"\n"
                        + "                    env:mustUnderstand=\"1\""
                        + "       >\n"
                        + "                       foo\n"
                        + "       </test:echoOk>\n"
                        + "   </env:Header>\n"
                        + "   <env:Body>\n"
                        + "       <env:Fault>\n"
                        + "           <faultcode>\n"
                        + "               env:Sender\n"
                        + "           </faultcode>\n"
                        + "           <faultstring>\n"
                        + "               Sender Timeout\n"
                        + "           </faultstring>\n"
                        + "           <faultactor>\n"
                        + "               http://schemas.xmlsoap.org/soap/envelope/actor/ultimateReceiver\n"
                        + "           </faultactor>\n"
                        + "           <detail xmlns:m=\"http:www.sample.org\">\n"
                        + "               Details of error\n"
                        + "               <m:MaxTime m:detail=\"This is only a test\">\n"
                        + "                   P5M\n"
                        + "               </m:MaxTime>\n"
                        + "               <m:AveTime>\n"
                        + "                   <m:Time>\n"
                        + "                       P3M\n"
                        + "                   </m:Time>\n"
                        + "               </m:AveTime>\n"
                        + "           </detail>\n"
                        + "           <n:Test xmlns:n=\"http:www.Test.org\">\n"
                        + "               <n:TestElement>\n"
                        + "                   This is only a test\n"
                        + "               </n:TestElement>\n"
                        + "           </n:Test>\n"
                        + "       </env:Fault>\n"
                        + "   </env:Body>\n"
                        + "</env:Envelope>";

        OMXMLParserWrapper soap11Builder =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                        metaFactory, new StringReader(soap11Message));
        SOAPEnvelope soap11Envelope = (SOAPEnvelope) soap11Builder.getDocumentElement();
        //            soap11Envelope.build();
        //            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(System.out);
        //            soap11Envelope.internalSerializeAndConsume(writer);
        //          writer.flush();

        assertTrue(
                "SOAP 1.1 :- envelope local name mismatch",
                soap11Envelope.getLocalName().equals(SOAPConstants.SOAPENVELOPE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.1 :- envelope namespace uri mismatch",
                soap11Envelope
                        .getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPHeader header = soap11Envelope.getHeader();
        assertTrue(
                "SOAP 1.1 :- Header local name mismatch",
                header.getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME));
        assertTrue(
                "SOAP 1.1 :- Header namespace uri mismatch",
                header.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) header.getFirstElement();
        assertTrue(
                "SOAP 1.1 :- Header block name mismatch",
                headerBlock.getLocalName().equals("echoOk"));
        assertTrue(
                "SOAP 1.1 :- Header block name space uri mismatch",
                headerBlock.getNamespace().getNamespaceURI().equals("http://example.org/ts-tests"));
        assertTrue(
                "SOAP 1.1 :- Headaer block text mismatch",
                headerBlock.getText().trim().equals("foo"));

        // Attribute iteration is not in any guaranteed order.
        // Use QNames to get the OMAttributes.
        QName actorQName =
                new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI, SOAP11Constants.ATTR_ACTOR);
        QName mustUnderstandQName =
                new QName(
                        SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                        SOAP11Constants.ATTR_MUSTUNDERSTAND);

        OMAttribute actorAttribute = headerBlock.getAttribute(actorQName);
        OMAttribute mustUnderstandAttribute = headerBlock.getAttribute(mustUnderstandQName);

        assertTrue(
                "SOAP 1.1 :- Mustunderstand attribute not found", mustUnderstandAttribute != null);
        assertTrue(
                "SOAP 1.1 :- Mustunderstand value mismatch",
                mustUnderstandAttribute
                        .getAttributeValue()
                        .equals(SOAPConstants.ATTR_MUSTUNDERSTAND_1));
        assertTrue(
                "SOAP 1.1 :- Mustunderstand attribute namespace uri mismatch",
                mustUnderstandAttribute
                        .getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        assertTrue("SOAP 1.1 :- Actor attribute name not found", actorAttribute != null);
        assertTrue(
                "SOAP 1.1 :- Actor value mismatch",
                actorAttribute
                        .getAttributeValue()
                        .trim()
                        .equals(
                                "http://schemas.xmlsoap.org/soap/"
                                        + SOAP11Constants.ATTR_ACTOR
                                        + "/"
                                        + "next"));
        assertTrue(
                "SOAP 1.1 :- Actor attribute namespace uri mismatch",
                actorAttribute
                        .getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPBody body = soap11Envelope.getBody();
        assertTrue(
                "SOAP 1.1 :- Body local name mismatch",
                body.getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME));
        assertTrue(
                "SOAP 1.1 :- Body namespace uri mismatch",
                body.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPFault fault = body.getFault();
        assertTrue(
                "SOAP 1.1 :- Fault namespace uri mismatch",
                fault.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        Iterator<OMNode> iteratorInFault = fault.getChildren();

        iteratorInFault.next();
        SOAPFaultCode code = (SOAPFaultCode) iteratorInFault.next();
        assertEquals(
                "SOAP Fault code local name mismatch",
                code.getLocalName(),
                (SOAP11Constants.SOAP_FAULT_CODE_LOCAL_NAME));

        assertEquals("SOAP 1.1 :- Fault code value mismatch", code.getText().trim(), "env:Sender");

        iteratorInFault.next();
        SOAPFaultReason reason = (SOAPFaultReason) iteratorInFault.next();
        assertTrue(
                "SOAP 1.1 :- Fault string local name mismatch",
                reason.getLocalName().equals(SOAP11Constants.SOAP_FAULT_STRING_LOCAL_NAME));
        assertTrue(
                "SOAP 1.1 :- Fault string value mismatch",
                reason.getText().trim().equals("Sender Timeout"));

        iteratorInFault.next();
        SOAPFaultRole role = (SOAPFaultRole) iteratorInFault.next();
        assertTrue(
                "SOAP 1.1 :- Fault actor local name mismatch",
                role.getLocalName().equals(SOAP11Constants.SOAP_FAULT_ACTOR_LOCAL_NAME));
        assertTrue(
                "SOAP 1.1 :- Actor value mismatch",
                role.getText()
                        .trim()
                        .equals("http://schemas.xmlsoap.org/soap/envelope/actor/ultimateReceiver"));

        iteratorInFault.next();
        SOAPFaultDetail detail = (SOAPFaultDetail) iteratorInFault.next();
        assertTrue(
                "SOAP 1.1 :- Fault detail local name mismatch",
                detail.getLocalName().equals(SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Text in detail mismatch",
                detail.getText().trim().equals("Details of error"));

        Iterator<OMNode> iteratorInDetail = detail.getChildren();

        iteratorInDetail.next();
        OMElement element1 = (OMElement) iteratorInDetail.next();
        assertTrue(
                "SOAP 1.1 :- MaxTime element mismatch", element1.getLocalName().equals("MaxTime"));
        assertTrue(
                "SOAP 1.1 :- MaxTime element namespace mismatch",
                element1.getNamespace().getNamespaceURI().equals("http:www.sample.org"));
        assertTrue(
                "SOAP 1.1 :- Text value in MaxTime element mismatch",
                element1.getText().trim().equals("P5M"));

        Iterator<OMAttribute> attributeIterator = element1.getAllAttributes();
        OMAttribute attributeInMaxTime = attributeIterator.next();
        assertTrue(
                "SOAP 1.1 :- Attribute local name mismatch",
                attributeInMaxTime.getLocalName().equals("detail"));
        assertTrue(
                "SOAP 1.1 :- Attribute namespace mismatch",
                attributeInMaxTime.getNamespace().getNamespaceURI().equals("http:www.sample.org"));
        assertTrue(
                "SOAP 1.1 :- Attribute value mismatch",
                attributeInMaxTime.getAttributeValue().equals("This is only a test"));

        iteratorInDetail.next();
        OMElement element2 = (OMElement) iteratorInDetail.next();
        assertTrue(
                "SOAP 1.1 :- AveTime element mismatch", element2.getLocalName().equals("AveTime"));
        assertTrue(
                "SOAP 1.1 :- AveTime element namespace mismatch",
                element2.getNamespace().getNamespaceURI().equals("http:www.sample.org"));

        Iterator<OMNode> iteratorInAveTimeElement = element2.getChildren();

        iteratorInAveTimeElement.next();
        OMElement element21 = (OMElement) iteratorInAveTimeElement.next();
        assertTrue("SOAP 1.1 :- Time element mismatch", element21.getLocalName().equals("Time"));
        assertTrue(
                "SOAP 1.1 :- Time element namespace mismatch",
                element21.getNamespace().getNamespaceURI().equals("http:www.sample.org"));
        assertTrue(
                "SOAP 1.1 :- Text value in Time element mismatch",
                element21.getText().trim().equals("P3M"));

        iteratorInFault.next();
        OMElement testElement = (OMElement) iteratorInFault.next();
        assertTrue("SOAP 1.1 :- Test element mismatch", testElement.getLocalName().equals("Test"));
        assertTrue(
                "SOAP 1.1 :- Test element namespace mismatch",
                testElement.getNamespace().getNamespaceURI().equals("http:www.Test.org"));

        OMElement childOfTestElement = testElement.getFirstElement();
        assertTrue(
                "SOAP 1.1 :- Test element child local name mismatch",
                childOfTestElement.getLocalName().equals("TestElement"));
        assertTrue(
                "SOAP 1.1 :- Test element child namespace mismatch",
                childOfTestElement.getNamespace().getNamespaceURI().equals("http:www.Test.org"));
        assertTrue(
                "SOAP 1.1 :- Test element child value mismatch",
                childOfTestElement.getText().trim().equals("This is only a test"));

        soap11Builder.close();
    }
}
