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
package org.apache.axiom.ts.soapdom.message;

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestLazySOAPFactorySelection extends SOAPTestCase {
    public TestLazySOAPFactorySelection(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        // Create a SOAP model builder without specifying the SOAP version.
        SOAPMessage message = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory,
                SOAPSampleSet.NO_HEADER.getMessage(spec).getInputStream(), null).getSOAPMessage();
        
        // At this stage, the SOAPFactory instance has not yet been determined.
        // However, if we cast the SOAPMessage to a Document and use it to create e new Element,
        // then that element must have the right factory.
        Element element = ((Document)message).createElementNS("urn:test", "p:test");
        
        SOAPFactory soapFactoryFromNewElement = (SOAPFactory)((OMElement)element).getOMFactory();
        assertThat(soapFactoryFromNewElement).isSameAs(soapFactory);
    }
}
