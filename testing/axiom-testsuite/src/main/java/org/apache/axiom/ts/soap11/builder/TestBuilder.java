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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
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
import org.junit.jupiter.api.function.Executable;

public class TestBuilder implements Executable {
    @Inject
    private OMMetaFactory metaFactory;

    @Override
    public void execute() throws Throwable {
        String soap11Message = "<?xml version='1.0' ?>"
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
                OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, new StringReader(soap11Message));
        SOAPEnvelope soap11Envelope = (SOAPEnvelope) soap11Builder.getDocumentElement();
        //            soap11Envelope.build();
        //            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(System.out);
        //            soap11Envelope.internalSerializeAndConsume(writer);
        //          writer.flush();

        assertThat(soap11Envelope.getLocalName()).isEqualTo(SOAPConstants.SOAPENVELOPE_LOCAL_NAME);
        assertThat(soap11Envelope.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPHeader header = soap11Envelope.getHeader();
        assertThat(header.getLocalName()).isEqualTo(SOAPConstants.HEADER_LOCAL_NAME);
        assertThat(header.getNamespace().getNamespaceURI()).isEqualTo(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) header.getFirstElement();
        assertThat(headerBlock.getLocalName()).isEqualTo("echoOk");
        assertThat(headerBlock.getNamespace().getNamespaceURI()).isEqualTo("http://example.org/ts-tests");
        assertThat(headerBlock.getText().trim()).isEqualTo("foo");

        // Attribute iteration is not in any guaranteed order.
        // Use QNames to get the OMAttributes.
        QName actorQName = new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI, SOAP11Constants.ATTR_ACTOR);
        QName mustUnderstandQName =
                new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI, SOAP11Constants.ATTR_MUSTUNDERSTAND);

        OMAttribute actorAttribute = headerBlock.getAttribute(actorQName);
        OMAttribute mustUnderstandAttribute = headerBlock.getAttribute(mustUnderstandQName);

        assertThat(mustUnderstandAttribute).isNotNull();
        assertThat(mustUnderstandAttribute.getAttributeValue()).isEqualTo(SOAPConstants.ATTR_MUSTUNDERSTAND_1);
        assertThat(mustUnderstandAttribute.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        assertThat(actorAttribute).isNotNull();
        assertThat(actorAttribute.getAttributeValue().trim())
                .isEqualTo("http://schemas.xmlsoap.org/soap/" + SOAP11Constants.ATTR_ACTOR + "/" + "next");
        assertThat(actorAttribute.getNamespace().getNamespaceURI())
                .isEqualTo(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPBody body = soap11Envelope.getBody();
        assertThat(body.getLocalName()).isEqualTo(SOAPConstants.BODY_LOCAL_NAME);
        assertThat(body.getNamespace().getNamespaceURI()).isEqualTo(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        SOAPFault fault = body.getFault();
        assertThat(fault.getNamespace().getNamespaceURI()).isEqualTo(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

        Iterator<OMNode> iteratorInFault = fault.getChildren();

        iteratorInFault.next();
        SOAPFaultCode code = (SOAPFaultCode) iteratorInFault.next();
        assertThat(code.getLocalName()).isEqualTo(SOAP11Constants.SOAP_FAULT_CODE_LOCAL_NAME);

        assertThat(code.getText().trim()).isEqualTo("env:Sender");

        iteratorInFault.next();
        SOAPFaultReason reason = (SOAPFaultReason) iteratorInFault.next();
        assertThat(reason.getLocalName()).isEqualTo(SOAP11Constants.SOAP_FAULT_STRING_LOCAL_NAME);
        assertThat(reason.getText().trim()).isEqualTo("Sender Timeout");

        iteratorInFault.next();
        SOAPFaultRole role = (SOAPFaultRole) iteratorInFault.next();
        assertThat(role.getLocalName()).isEqualTo(SOAP11Constants.SOAP_FAULT_ACTOR_LOCAL_NAME);
        assertThat(role.getText().trim()).isEqualTo("http://schemas.xmlsoap.org/soap/envelope/actor/ultimateReceiver");

        iteratorInFault.next();
        SOAPFaultDetail detail = (SOAPFaultDetail) iteratorInFault.next();
        assertThat(detail.getLocalName()).isEqualTo(SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME);
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
        assertThat(attributeInMaxTime.getNamespace().getNamespaceURI()).isEqualTo("http:www.sample.org");
        assertThat(attributeInMaxTime.getAttributeValue()).isEqualTo("This is only a test");

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

        iteratorInFault.next();
        OMElement testElement = (OMElement) iteratorInFault.next();
        assertThat(testElement.getLocalName()).isEqualTo("Test");
        assertThat(testElement.getNamespace().getNamespaceURI()).isEqualTo("http:www.Test.org");

        OMElement childOfTestElement = testElement.getFirstElement();
        assertThat(childOfTestElement.getLocalName()).isEqualTo("TestElement");
        assertThat(childOfTestElement.getNamespace().getNamespaceURI()).isEqualTo("http:www.Test.org");
        assertThat(childOfTestElement.getText().trim()).isEqualTo("This is only a test");

        soap11Builder.close();
    }
}
