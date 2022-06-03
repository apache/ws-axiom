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

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests the behavior of {@link SOAPEnvelope#getSOAPBodyFirstElementLocalName()} and {@link
 * SOAPEnvelope#getSOAPBodyFirstElementNS()} for a {@link SOAPEnvelope} constructed from a parser.
 * In this case, the Axiom implementation may choose to use a special optimization to get the name
 * of the element without actually instantiating the corresponding {@link OMElement}.
 */
public class TestGetSOAPBodyFirstElementLocalNameAndNSWithParser extends SOAPTestCase {
    private final QName qname;

    public TestGetSOAPBodyFirstElementLocalNameAndNSWithParser(
            OMMetaFactory metaFactory, SOAPSpec spec, QName qname) {
        super(metaFactory, spec);
        this.qname = qname;
        addTestParameter("prefix", qname.getPrefix());
        addTestParameter("uri", qname.getNamespaceURI());
    }

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
        assertEquals(qname.getLocalPart(), envelope.getSOAPBodyFirstElementLocalName());
        OMNamespace ns = envelope.getSOAPBodyFirstElementNS();
        if (qname.getNamespaceURI().length() == 0) {
            assertNull(ns);
        } else {
            assertEquals(qname.getNamespaceURI(), ns.getNamespaceURI());
            assertEquals(qname.getPrefix(), ns.getPrefix());
        }

        // Also request an XMLStreamReader. The LLOM implementation triggers some special processing
        // in this case (because the getSOAPBodyFirstElementXXX calls put the builder in lookahead
        // mode). This is a regression test for r631687 (AXIOM-282).
        XMLStreamReader reader = envelope.getXMLStreamReader(false);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals("Body", reader.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(qname.getLocalPart(), reader.getLocalName());
        if (qname.getNamespaceURI().length() == 0) {
            assertNull(reader.getNamespaceURI());
        } else {
            assertEquals(qname.getNamespaceURI(), reader.getNamespaceURI());
        }
        String readerPrefix = reader.getPrefix();
        if (qname.getPrefix().length() == 0) {
            assertTrue(readerPrefix == null || readerPrefix.isEmpty());
        } else {
            assertEquals(qname.getPrefix(), readerPrefix);
        }
    }
}
