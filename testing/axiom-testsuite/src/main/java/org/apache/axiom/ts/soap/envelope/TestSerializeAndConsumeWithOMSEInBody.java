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

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Regression test for WSCOMMONS-226. */
public class TestSerializeAndConsumeWithOMSEInBody extends SOAPTestCase {
    public TestSerializeAndConsumeWithOMSEInBody(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPBody body = soapFactory.createSOAPBody();
        envelope.addChild(body);
        OMNamespace ns = soapFactory.createOMNamespace("http://ns1", "d");
        OMElement payload = soapFactory.createOMElement(new DummySource(), "dummy", ns);
        payload.setNamespace(ns); // This line will cause NoSuchElementException
        body.addChild(payload);
        StringWriter writer = new StringWriter();
        envelope.serializeAndConsume(writer);
        //        System.out.println(writer);
    }

    private static class DummySource implements OMDataSource {
        private XMLStreamReader reader;
        // private String xml = "<?xml version='1.0'?><d:dummy name='1' xmlns:d='http://ns1'/>";
        private String xml =
                "<?xml version='1.0'?><d:dummy name='1' xmlns:d='http://ns1'>hello<mixed/>world</d:dummy>";

        /**
         * @see org.apache.axiom.om.OMDataSource#getReader()
         */
        @Override
        public XMLStreamReader getReader() throws XMLStreamException {
            // TODO Auto-generated method stub
            return StAXUtils.createXMLStreamReader(new StringReader(xml));
        }

        /**
         * @see org.apache.axiom.om.OMDataSource#serialize(java.io.OutputStream,
         *     org.apache.axiom.om.OMOutputFormat)
         */
        @Override
        public void serialize(OutputStream output, OMOutputFormat format)
                throws XMLStreamException {
            // TODO Auto-generated method stub

        }

        /**
         * @see org.apache.axiom.om.OMDataSource#serialize(java.io.Writer,
         *     org.apache.axiom.om.OMOutputFormat)
         */
        @Override
        public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
            // TODO Auto-generated method stub

        }

        /**
         * @see org.apache.axiom.om.OMDataSource#serialize(javax.xml.stream.XMLStreamWriter)
         */
        @Override
        public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
            // TODO Auto-generated method stub

        }
    }
}
