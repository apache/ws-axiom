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

import static org.assertj.core.api.Assertions.assertThat;

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

import com.google.inject.Inject;

public class TestBuilder extends AxiomTestCase {
    @Inject
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

        assertThat(soap12Envelope.getLocalName()).isEqualTo(SOAPConstants.SOAPENVELOPE_LOCAL_NAME);
        assertThat(soap12Envelope.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPHeader header = soap12Envelope.getHeader();
        assertThat(header.getLocalName()).isEqualTo(SOAPConstants.HEADER_LOCAL_NAME);
        assertThat(header.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) header.getFirstElement();
        assertThat(headerBlock.getLocalName()).isEqualTo("echoOk");
        assertThat(headerBlock.getNamespace().getNamespaceURI())
                .isEqualTo("http://example.org/ts-tests");
        assertThat(headerBlock.getText().trim()).isEqualTo("foo");

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

        assertThat(roleAttribute).isNotNull();

        assertThat(roleAttribute.getAttributeValue().trim())
                .isEqualTo(
                        SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI
                                + "/"
                                + SOAP12Constants.SOAP_ROLE
                                + "/"
                                + "ultimateReceiver");

        assertThat(mustUnderstandAttribute).isNotNull();

        assertThat(mustUnderstandAttribute.getAttributeValue())
                .isEqualTo(SOAPConstants.ATTR_MUSTUNDERSTAND_TRUE);

        SOAPBody body = soap12Envelope.getBody();
        assertThat(body.getLocalName()).isEqualTo(SOAPConstants.BODY_LOCAL_NAME);
        assertThat(body.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPFault fault = body.getFault();
        assertThat(fault.getLocalName()).isEqualTo(SOAPConstants.SOAPFAULT_LOCAL_NAME);
        assertThat(fault.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        Iterator<OMNode> iteratorInFault = fault.getChildren();

        iteratorInFault.next();
        SOAPFaultCode code = (SOAPFaultCode) iteratorInFault.next();
        assertThat(code.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME);
        assertThat(code.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        Iterator<OMNode> iteratorInCode = code.getChildren();

        iteratorInCode.next();
        SOAPFaultValue value1 = (SOAPFaultValue) iteratorInCode.next();
        assertThat(value1.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME);
        assertThat(value1.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(value1.getText()).isEqualTo("env:Sender");

        QName valueQName = value1.getTextAsQName();
        assertThat(valueQName.getLocalPart()).isEqualTo("Sender");

        assertThat(valueQName.getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        iteratorInCode.next();
        SOAPFaultSubCode subCode1 = (SOAPFaultSubCode) iteratorInCode.next();
        assertThat(subCode1.getLocalName())
                .isEqualTo(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME);
        assertThat(subCode1.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        Iterator<OMNode> iteratorInSubCode1 = subCode1.getChildren();

        iteratorInSubCode1.next();
        SOAPFaultValue value2 = (SOAPFaultValue) iteratorInSubCode1.next();
        assertThat(value2.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME);
        assertThat(value2.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(value2.getText()).isEqualTo("m:MessageTimeout");

        iteratorInSubCode1.next();
        SOAPFaultSubCode subCode2 = (SOAPFaultSubCode) iteratorInSubCode1.next();
        assertThat(subCode2.getLocalName())
                .isEqualTo(SOAP12Constants.SOAP_FAULT_SUB_CODE_LOCAL_NAME);
        assertThat(subCode2.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        Iterator<OMNode> iteratorInSubCode2 = subCode2.getChildren();

        iteratorInSubCode2.next();
        SOAPFaultValue value3 = (SOAPFaultValue) iteratorInSubCode2.next();
        assertThat(value3.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME);
        assertThat(value3.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(value3.getText()).isEqualTo("m:MessageTimeout");

        iteratorInFault.next();
        SOAPFaultReason reason = (SOAPFaultReason) iteratorInFault.next();
        assertThat(reason.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME);
        assertThat(reason.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        Iterator<OMNode> iteratorInReason = reason.getChildren();

        iteratorInReason.next();
        SOAPFaultText text = (SOAPFaultText) iteratorInReason.next();
        assertThat(text.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME);
        assertThat(text.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(text.getText()).isEqualTo("Sender Timeout");

        iteratorInFault.next();
        SOAPFaultNode node = (SOAPFaultNode) iteratorInFault.next();
        assertThat(node.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME);
        assertThat(node.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(node.getText().trim())
                .isEqualTo("http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver");

        iteratorInFault.next();
        SOAPFaultRole role = (SOAPFaultRole) iteratorInFault.next();
        assertThat(role.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_ROLE_LOCAL_NAME);
        assertThat(role.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        assertThat(role.getText().trim()).isEqualTo("ultimateReceiver");

        iteratorInFault.next();
        SOAPFaultDetail detail = (SOAPFaultDetail) iteratorInFault.next();
        assertThat(detail.getLocalName()).isEqualTo(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME);
        assertThat(detail.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        assertThat(detail.getText().trim()).isEqualTo("Details of error");

        Iterator<OMNode> iteratorInDetail = detail.getChildren();

        iteratorInDetail.next();
        OMElement element1 = (OMElement) iteratorInDetail.next();
        assertThat(element1.getLocalName()).isEqualTo("MaxTime");
        assertThat(element1.getNamespace().getNamespaceURI()).isEqualTo("http:www.sample.org");
        assertThat(element1.getText().trim()).isEqualTo("P5M");

        Iterator<OMAttribute> attributeIterator = element1.getAllAttributes();
        OMAttribute attributeInMaxTime = attributeIterator.next();
        assertThat(attributeInMaxTime.getLocalName()).isEqualTo("detail");
        assertThat(attributeInMaxTime.getNamespace().getNamespaceURI())
                .isEqualTo("http:www.sample.org");
        assertThat(attributeInMaxTime.getAttributeValue().trim()).isEqualTo("This is only a test");

        iteratorInDetail.next();
        OMElement element2 = (OMElement) iteratorInDetail.next();
        assertThat(element2.getLocalName()).isEqualTo("AveTime");
        assertThat(element2.getNamespace().getNamespaceURI()).isEqualTo("http:www.sample.org");

        Iterator<OMNode> iteratorInAveTimeElement = element2.getChildren();

        iteratorInAveTimeElement.next();
        OMElement element21 = (OMElement) iteratorInAveTimeElement.next();
        assertThat(element21.getLocalName()).isEqualTo("Time");
        assertThat(element21.getNamespace().getNamespaceURI()).isEqualTo("http:www.sample.org");
        assertThat(element21.getText().trim()).isEqualTo("P3M");

        soap12Builder.close();
    }
}
