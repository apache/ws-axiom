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
package org.apache.axiom.ts.soap.builder;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.AxiomTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

public class MessageTest extends AxiomTestCase {
    private final String file;

    public MessageTest(OMMetaFactory metaFactory, String file) {
        super(metaFactory);
        this.file = file;
        addTestProperty("file", file);
    }

    protected void runTest() throws Throwable {
        SOAPEnvelope soapEnvelope = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory,
                AbstractTestCase.getTestResource(file), null).getSOAPEnvelope();
        OMTestUtils.walkThrough(soapEnvelope);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document expected;
        InputStream in = AbstractTestCase.getTestResource(file);
        try {
            expected = db.parse(in);
        } finally {
            in.close();
        }
        Document actual = db.newDocument();
        // TODO: need to use getSAXSource (instead of toString) because of AXIOM-430
        TransformerFactory.newInstance().newTransformer().transform(soapEnvelope.getSAXSource(true), new DOMResult(actual));
        XMLAssert.assertXMLIdentical(XMLUnit.compareXML(expected, actual), true);
        soapEnvelope.close(false);
    }
}
