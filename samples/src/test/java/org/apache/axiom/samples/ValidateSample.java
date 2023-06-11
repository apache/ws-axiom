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

import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.w3c.dom.Element;

public class ValidateSample extends TestCase {
    // START SNIPPET: sax
    public void validate(InputStream in, URL schemaUrl) throws Exception {
        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(in, "UTF-8");
        SOAPEnvelope envelope = builder.getSOAPEnvelope();
        OMElement bodyContent = envelope.getBody().getFirstElement();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        Validator validator = schema.newValidator();
        validator.validate(bodyContent.getSAXSource(true));
    }
    // END SNIPPET: sax

    public void testSAX() throws Exception {
        validate(
                getClass().getResourceAsStream("soap-request.xml"),
                getClass().getResource("schema.xsd"));
    }

    // START SNIPPET: dom
    public void validateUsingDOM(InputStream in, URL schemaUrl) throws Exception {
        OMMetaFactory mf = OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(mf, in, "UTF-8");
        SOAPEnvelope envelope = builder.getSOAPEnvelope();
        OMElement bodyContent = envelope.getBody().getFirstElement();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource((Element) bodyContent));
    }
    // END SNIPPET: dom

    public void testDOM() throws Exception {
        validate(
                getClass().getResourceAsStream("soap-request.xml"),
                getClass().getResource("schema.xsd"));
    }
}
