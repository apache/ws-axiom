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
package org.apache.axiom.ts.soap.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Tests {@link OMDocument#getCharsetEncoding()} on a {@link SOAPMessage} created by a builder. */
public class TestGetCharsetEncodingWithParser extends SOAPTestCase {
    public TestGetCharsetEncodingWithParser(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        String encoding = "iso-8859-15";
        SOAPEnvelope orgEnvelope = soapFactory.getDefaultEnvelope();
        soapFactory
                .createOMElement("echo", soapFactory.createOMNamespace("urn:test", null))
                .setText("test");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        orgEnvelope.serialize(baos, format);
        SOAPMessage message =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                                metaFactory, new ByteArrayInputStream(baos.toByteArray()), encoding)
                        .getSOAPMessage();
        assertEquals(encoding, message.getCharsetEncoding());
    }
}
