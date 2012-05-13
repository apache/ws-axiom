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
package org.apache.axiom.ts.om.element;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.AxiomTestCase;
import org.custommonkey.xmlunit.XMLAssert;

public class TestCloneOMElement extends AxiomTestCase {
    public TestCloneOMElement(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        SOAPEnvelope soapEnvelope = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory,
                AbstractTestCase.getTestResource(TestConstants.SOAP_SOAPMESSAGE), null).getSOAPEnvelope();
        SOAPBody body = soapEnvelope.getBody();

        OMElement firstClonedBodyElement = body.cloneOMElement();
        OMElement secondClonedBodyElement = body.cloneOMElement();

        // first check whether both have the same information
        XMLAssert.assertXMLEqual(body.toString(),
                                 firstClonedBodyElement.toString());
        XMLAssert.assertXMLEqual(body.toString(),
                                 secondClonedBodyElement.toString());
        XMLAssert.assertXMLEqual(firstClonedBodyElement.toString(),
                                 secondClonedBodyElement.toString());

        // lets check some links. They must not be equal
        assertNotSame(body.getParent(), firstClonedBodyElement.getParent());
        assertNotSame(body.getParent(), secondClonedBodyElement.getParent());
        assertNotSame(firstClonedBodyElement.getParent(), secondClonedBodyElement.getParent());

        soapEnvelope.close(false);
    }
}
