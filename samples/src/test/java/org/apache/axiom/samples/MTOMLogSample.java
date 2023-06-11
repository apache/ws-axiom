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
package org.apache.axiom.samples;

import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.activation.DataHandler;

public class MTOMLogSample extends TestCase {
    private static final Log log = LogFactory.getLog(MTOMLogSample.class);

    // START SNIPPET: variant2
    private void logMessage(SOAPEnvelope env) throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer =
                new LogWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(sw));
        env.serialize(writer);
        writer.flush();
        log.info("Message: " + sw.toString());
    }
    // END SNIPPET: variant2

    public void test() throws XMLStreamException {
        SOAPFactory factory = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope env = factory.createDefaultSOAPMessage().getSOAPEnvelope();
        OMElement element =
                factory.createOMElement(
                        new QName("urn:testService", "invokeMtom", "ns"), env.getBody());
        element.addChild(
                factory.createOMText(
                        DataHandlerUtils.toBlob(new DataHandler("test", "text/xml")), true));
        logMessage(env);
    }
}
