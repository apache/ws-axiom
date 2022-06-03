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
package org.apache.axiom.ts.soap12.builder;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.AxiomTestCase;

public class TestBuilder extends AxiomTestCase {
    public TestBuilder(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        String soap12Message =
                "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">\n"
                        + "   <env:Header>\n"
                        + "       <test:echoOk xmlns:test=\"http://example.org/ts-tests\"\n"
                        + "                    env:role=\"http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver\"\n"
                        + "                    env:mustUnderstand=\"true\">\n"
                        + "                       foo\n"
                        + "       </test:echoOk>\n"
                        + "   </env:Header>\n"
                        + "   <env:Body>\n"
                        + "       <env:Fault>\n"
                        + "           <env:Code>\n"
                        + "               <env:Value>env:Sender</env:Value>\n"
                        + "               <env:Subcode>\n"
                        + "                   <env:Value>m:MessageTimeout</env:Value>\n"
                        + "                   <env:Subcode>\n"
                        + "                       <env:Value>m:MessageTimeout</env:Value>\n"
                        + "                   </env:Subcode>\n"
                        + "               </env:Subcode>\n"
                        + "           </env:Code>\n"
                        + "           <env:Reason>\n"
                        + "               <env:Text>Sender Timeout</env:Text>\n"
                        + "           </env:Reason>\n"
                        + "           <env:Node>\n"
                        + "               http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver\n"
                        + "           </env:Node>\n"
                        + "           <env:Role>\n"
                        + "               ultimateReceiver\n"
                        + "           </env:Role>\n"
                        + "           <env:Detail xmlns:m=\"http:www.sample.org\">\n"
                        + "               Details of error\n"
                        + "               <m:MaxTime m:detail=\"This is only a test\">\n"
                        + "                   P5M\n"
                        + "               </m:MaxTime>\n"
                        + "               <m:AveTime>\n"
                        + "                   <m:Time>\n"
                        + "                       P3M\n"
                        + "                   </m:Time>\n"
                        + "               </m:AveTime>\n"
                        + "           </env:Detail>\n"
                        + "       </env:Fault>\n"
                        + "   </env:Body>\n"
                        + "</env:Envelope>";

        OMXMLParserWrapper soap12Builder =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                        metaFactory, new StringReader(soap12Message));
        SOAPEnvelope soap12Envelope = (SOAPEnvelope) soap12Builder.getDocumentElement();

        assertTrue(
                "SOAP 1.2 :- envelope local name mismatch",
                soap12Envelope.getLocalName().equals(SOAPConstants.SOAPENVELOPE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- envelope namespace uri mismatch",
                soap12Envelope
                        .getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPHeader header = soap12Envelope.getHeader();
        assertTrue(
                "SOAP 1.2 :- Header local name mismatch",
                header.getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Header namespace uri mismatch",
                header.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) header.getFirstElement();
        assertTrue(
                "SOAP 1.2 :- Header block name mismatch",
                headerBlock.getLocalName().equals("echoOk"));
        assertTrue(
                "SOAP 1.2 :- Header block name space uri mismatch",
                headerBlock.getNamespace().getNamespaceURI().equals("http://example.org/ts-tests"));
        assertEquals("SOAP 1.2 :- Header block text mismatch", headerBlock.getText().trim(), "foo");

        // Attribute iteration is not in any guaranteed order.
        // Use QNames to get the OMAttributes.
        QName roleQName =
                new QName(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, SOAP12Constants.SOAP_ROLE);
        QName mustUnderstandQName =
                new QName(
                        SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                        SOAP12Constants.ATTR_MUSTUNDERSTAND);

        OMAttribute roleAttribute = headerBlock.getAttribute(roleQName);
        OMAttribute mustUnderstandAttribute = headerBlock.getAttribute(mustUnderstandQName);

        assertTrue("SOAP 1.2 :- Role attribute name not found", roleAttribute != null);

        assertTrue(
                "SOAP 1.2 :- Role value mismatch",
                roleAttribute
                        .getAttributeValue()
                        .trim()
                        .equals(
                                SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI
                                        + "/"
                                        + SOAP12Constants.SOAP_ROLE
                                        + "/"
                                        + "ultimateReceiver"));

        assertTrue(
                "SOAP 1.2 :- Mustunderstand attribute not found", mustUnderstandAttribute != null);

        assertTrue(
                "SOAP 1.2 :- Mustunderstand value mismatch",
                mustUnderstandAttribute
                        .getAttributeValue()
                        .equals(SOAPConstants.ATTR_MUSTUNDERSTAND_TRUE));

        SOAPBody body = soap12Envelope.getBody();
        assertTrue(
                "SOAP 1.2 :- Body local name mismatch",
                body.getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Body namespace uri mismatch",
                body.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        SOAPFault fault = body.getFault();
        assertTrue(
                "SOAP 1.2 :- Fault local name mismatch",
                fault.getLocalName().equals(SOAPConstants.SOAPFAULT_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault namespace uri mismatch",
                fault.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        Iterator<OMNode> iteratorInFault = fault.getChildren();

        iteratorInFault.next();
        SOAPFaultCode code = (SOAPFaultCode) iteratorInFault.next();
        assertTrue(
                "SOAP 1.2 :- Fault code local name mismatch",
                code.getLocalName().equals(SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault code namespace uri mismatch",
                code.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        Iterator<OMNode> iteratorInCode = code.getChildren();

        iteratorInCode.next();
        SOAPFaultValue value1 = (SOAPFaultValue) iteratorInCode.next();
        assertTrue(
                "SOAP 1.2 :- Fault code value local name mismatch",
                value1.getLocalName().equals(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault code namespace uri mismatch",
                value1.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
        assertTrue("SOAP 1.2 :- Value1 text mismatch", value1.getText().equals("env:Sender"));

        QName valueQName = value1.getTextAsQName();
        assertTrue(
                "SOAP 1.2 :- Fault code value's qname local name mismatch",
                valueQName.getLocalPart().equals("Sender"));

        assertTrue(
                "SOAP 1.2 :- Fault code value's qname namespace uri mismatch",
                valueQName.getNamespaceURI().equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        iteratorInCode.next();
        SOAPFaultSubCode subCode1 = (SOAPFaultSubCode) iteratorInCode.next();
        assertTrue(
                "SOAP 1.2 :- Fault sub code local name mismatch",
                subCode1.getLocalName().equals(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault subcode namespace uri mismatch",
                subCode1.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        Iterator<OMNode> iteratorInSubCode1 = subCode1.getChildren();

        iteratorInSubCode1.next();
        SOAPFaultValue value2 = (SOAPFaultValue) iteratorInSubCode1.next();
        assertTrue(
                "SOAP 1.2 :- Fault code value local name mismatch",
                value2.getLocalName().equals(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault code namespace uri mismatch",
                value2.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
        assertTrue("SOAP 1.2 :- Value2 text mismatch", value2.getText().equals("m:MessageTimeout"));

        iteratorInSubCode1.next();
        SOAPFaultSubCode subCode2 = (SOAPFaultSubCode) iteratorInSubCode1.next();
        assertTrue(
                "SOAP 1.2 :- Fault sub code local name mismatch",
                subCode2.getLocalName().equals(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault subcode namespace uri mismatch",
                subCode2.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        Iterator<OMNode> iteratorInSubCode2 = subCode2.getChildren();

        iteratorInSubCode2.next();
        SOAPFaultValue value3 = (SOAPFaultValue) iteratorInSubCode2.next();
        assertTrue(
                "SOAP 1.2 :- Fault code value local name mismatch",
                value3.getLocalName().equals(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault code namespace uri mismatch",
                value3.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
        assertTrue("SOAP 1.2 :- Value2 text mismatch", value3.getText().equals("m:MessageTimeout"));

        iteratorInFault.next();
        SOAPFaultReason reason = (SOAPFaultReason) iteratorInFault.next();
        assertTrue(
                "SOAP 1.2 :- Fault reason local name mismatch",
                reason.getLocalName().equals(SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault reason namespace uri mismatch",
                reason.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        Iterator<OMNode> iteratorInReason = reason.getChildren();

        iteratorInReason.next();
        SOAPFaultText text = (SOAPFaultText) iteratorInReason.next();
        assertTrue(
                "SOAP 1.2 :- Fault text local name mismatch",
                text.getLocalName().equals(SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Text namespace uri mismatch",
                text.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
        assertTrue("SOAP 1.2 :- Text value mismatch", text.getText().equals("Sender Timeout"));

        iteratorInFault.next();
        SOAPFaultNode node = (SOAPFaultNode) iteratorInFault.next();
        assertTrue(
                "SOAP 1.2 :- Fault node local name mismatch",
                node.getLocalName().equals(SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault node namespace uri mismatch",
                node.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
        assertTrue(
                "SOAP 1.2 :- Node value mismatch",
                node.getText()
                        .trim()
                        .equals("http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver"));

        iteratorInFault.next();
        SOAPFaultRole role = (SOAPFaultRole) iteratorInFault.next();
        assertTrue(
                "SOAP 1.2 :- Fault role local name mismatch",
                role.getLocalName().equals(SOAP12Constants.SOAP_FAULT_ROLE_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault role namespace uri mismatch",
                role.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
        assertTrue(
                "SOAP 1.2 :- Role value mismatch",
                role.getText().trim().equals("ultimateReceiver"));

        iteratorInFault.next();
        SOAPFaultDetail detail = (SOAPFaultDetail) iteratorInFault.next();
        assertTrue(
                "SOAP 1.2 :- Fault detail local name mismatch",
                detail.getLocalName().equals(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME));
        assertTrue(
                "SOAP 1.2 :- Fault detail namespace uri mismatch",
                detail.getNamespace()
                        .getNamespaceURI()
                        .equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));

        assertTrue(
                "SOAP 1.2 :- Text in detail mismatch",
                detail.getText().trim().equals("Details of error"));

        Iterator<OMNode> iteratorInDetail = detail.getChildren();

        iteratorInDetail.next();
        OMElement element1 = (OMElement) iteratorInDetail.next();
        assertTrue(
                "SOAP 1.2 :- MaxTime element mismatch", element1.getLocalName().equals("MaxTime"));
        assertTrue(
                "SOAP 1.2 :- MaxTime element namespace mismatch",
                element1.getNamespace().getNamespaceURI().equals("http:www.sample.org"));
        assertTrue(
                "SOAP 1.2 :- Text value in MaxTime element mismatch",
                element1.getText().trim().equals("P5M"));

        Iterator<OMAttribute> attributeIterator = element1.getAllAttributes();
        OMAttribute attributeInMaxTime = attributeIterator.next();
        assertTrue(
                "SOAP 1.2 :- Attribute local name mismatch",
                attributeInMaxTime.getLocalName().equals("detail"));
        assertTrue(
                "SOAP 1.2 :- Attribute namespace mismatch",
                attributeInMaxTime.getNamespace().getNamespaceURI().equals("http:www.sample.org"));
        assertTrue(
                "SOAP 1.2 :- Attribute value mismatch",
                attributeInMaxTime.getAttributeValue().trim().equals("This is only a test"));

        iteratorInDetail.next();
        OMElement element2 = (OMElement) iteratorInDetail.next();
        assertTrue(
                "SOAP 1.2 :- AveTime element mismatch", element2.getLocalName().equals("AveTime"));
        assertTrue(
                "SOAP 1.2 :- AveTime element namespace mismatch",
                element2.getNamespace().getNamespaceURI().equals("http:www.sample.org"));

        Iterator<OMNode> iteratorInAveTimeElement = element2.getChildren();

        iteratorInAveTimeElement.next();
        OMElement element21 = (OMElement) iteratorInAveTimeElement.next();
        assertTrue("SOAP 1.2 :- Time element mismatch", element21.getLocalName().equals("Time"));
        assertTrue(
                "SOAP 1.2 :- Time element namespace mismatch",
                element21.getNamespace().getNamespaceURI().equals("http:www.sample.org"));
        assertTrue(
                "SOAP 1.2 :- Text value in Time element mismatch",
                element21.getText().trim().equals("P3M"));

        soap12Builder.close();
    }
}
