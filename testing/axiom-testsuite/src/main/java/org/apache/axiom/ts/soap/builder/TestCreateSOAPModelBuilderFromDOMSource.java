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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestCreateSOAPModelBuilderFromDOMSource extends SOAPTestCase {
    public TestCreateSOAPModelBuilderFromDOMSource(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = DOMImplementation.XERCES.parse(new InputSource(SOAPSampleSet.SIMPLE_FAULT.getMessage(spec).getUrl().toString()));
        SOAPMessage message = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, new DOMSource(document)).getSOAPMessage();
        assertAbout(xml())
                .that(xml(OMDocument.class, message))
                .ignoringWhitespaceInPrologAndEpilog()
                .hasSameContentAs(document);
        assertThat(message.getSOAPEnvelope().getBody().getFault()).isNotNull();
    }
}
