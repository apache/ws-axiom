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

package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.custommonkey.xmlunit.XMLTestCase;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

public class StAXSOAPModelBuilderTest extends XMLTestCase {
    /**
     * @throws Exception
     */
    public void testOptimizedFault() throws Exception {
        String soap11Fault = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<SOAP-ENV:Body>" +
                    "<SOAP-ENV:Fault>" +
                        "<faultcode>SOAP-ENV:Server</faultcode>" +
                        "<faultstring xml:lang=\"en\">handleMessage throws SOAPFaultException for ThrowsSOAPFaultToClientHandlersTest</faultstring>" +
                        "<detail>" +
                            "<somefaultentry/>" +
                        "</detail>" +
                        "<faultactor>faultActor</faultactor>" +
                        "</SOAP-ENV:Fault>" +
                    "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";
        
        // Use the test parser that is aware of the first qname in the body.
        // This simulates the use of the parser that has this information built into its
        // implementation.
        
        XMLStreamReader soap11Parser = StAXUtils.createXMLStreamReader(
                new StringReader(soap11Fault));
        QName qname = new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI, SOAP11Constants.BODY_FAULT_LOCAL_NAME, "SOAP-ENV");
        XMLStreamReaderWithQName parser = new XMLStreamReaderWithQName(soap11Parser, qname);
        SOAPModelBuilder soap11Builder = OMXMLBuilderFactory.createStAXSOAPModelBuilder(parser);
        SOAPEnvelope env = soap11Builder.getSOAPEnvelope();
        boolean isFault = env.hasFault();
        assertTrue(isFault);
        assertTrue(!parser.isReadBody());
        
        // Get the name of the first element in the body
        String localName = env.getSOAPBodyFirstElementLocalName();
        assertTrue(localName.equals("Fault"));
        assertTrue(!parser.isReadBody());
        parser.close();
    }
}
