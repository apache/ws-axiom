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
package org.apache.axiom.ts.soap.envelope;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;

import com.google.inject.Inject;

import junit.framework.TestCase;

/**
 * Tests the behavior of {@link SOAPEnvelope#getSOAPBodyFirstElementLocalName()} and {@link
 * SOAPEnvelope#getSOAPBodyFirstElementNS()} for a {@link SOAPEnvelope} constructed from a parser.
 * In this case, the Axiom implementation may choose to use a special optimization to get the name
 * of the element without actually instantiating the corresponding {@link OMElement}.
 */
public class TestGetSOAPBodyFirstElementLocalNameAndNSWithParser extends TestCase {
    @Inject private OMMetaFactory metaFactory;
    @Inject private SOAPFactory soapFactory;
    @Inject private QName qname;

    @Override
    protected void runTest() throws Throwable {
        // Prepare the message. Note that we do this programmatically to make sure that the message
        // doesn't contain any unwanted whitespace.
        SOAPEnvelope orgEnvelope = soapFactory.createDefaultSOAPMessage().getSOAPEnvelope();
        orgEnvelope
                .getBody()
                .addChild(
                        soapFactory.createOMElement(
                                qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix()));
        String message = orgEnvelope.toString();

        SOAPEnvelope envelope =
                OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, new StringReader(message))
                        .getSOAPEnvelope();
        assertThat(envelope.getSOAPBodyFirstElementLocalName()).isEqualTo(qname.getLocalPart());
        OMNamespace ns = envelope.getSOAPBodyFirstElementNS();
        if (qname.getNamespaceURI().length() == 0) {
            assertThat(ns).isNull();
        } else {
            assertThat(ns.getNamespaceURI()).isEqualTo(qname.getNamespaceURI());
            assertThat(ns.getPrefix()).isEqualTo(qname.getPrefix());
        }

        // Also request an XMLStreamReader. The LLOM implementation triggers some special processing
        // in this case (because the getSOAPBodyFirstElementXXX calls put the builder in lookahead
        // mode). This is a regression test for r631687 (AXIOM-282).
        XMLStreamReader reader = envelope.getXMLStreamReader(false);
        assertThat(reader.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(reader.getLocalName()).isEqualTo("Envelope");
        assertThat(reader.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(reader.getLocalName()).isEqualTo("Body");
        assertThat(reader.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(reader.getLocalName()).isEqualTo(qname.getLocalPart());
        if (qname.getNamespaceURI().length() == 0) {
            assertThat(reader.getNamespaceURI()).isNull();
        } else {
            assertThat(reader.getNamespaceURI()).isEqualTo(qname.getNamespaceURI());
        }
        String readerPrefix = reader.getPrefix();
        if (qname.getPrefix().length() == 0) {
            assertThat(readerPrefix).isNullOrEmpty();
        } else {
            assertThat(readerPrefix).isEqualTo(qname.getPrefix());
        }
    }
}
