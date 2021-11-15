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

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.jaxp.sax.SAXImplementation;
import org.apache.axiom.ts.soap.SOAPSample;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class TestCreateSOAPModelBuilderFromSAXSource extends SOAPTestCase {
    public TestCreateSOAPModelBuilderFromSAXSource(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SAXParserFactory parserFactory = SAXImplementation.XERCES.newSAXParserFactory();
        parserFactory.setNamespaceAware(true);
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SOAPSample sample = SOAPSampleSet.SIMPLE_FAULT.getMessage(spec);
        InputStream in = sample.getInputStream();
        InputSource is = new InputSource(in);
        is.setEncoding(sample.getEncoding());
        SOAPMessage message = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, new SAXSource(reader, is)).getSOAPMessage();
        assertAbout(xml())
                .that(xml(OMDocument.class, message))
                .ignoringWhitespaceInPrologAndEpilog()
                .hasSameContentAs(sample.getEnvelope());
        assertThat(message.getSOAPEnvelope().getBody().getFault()).isNotNull();
    }
}
