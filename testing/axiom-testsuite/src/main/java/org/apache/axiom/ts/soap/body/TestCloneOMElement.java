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
package org.apache.axiom.ts.soap.body;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.ts.soap.SOAPSampleAdapter;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.custommonkey.xmlunit.XMLAssert;

public class TestCloneOMElement extends SOAPTestCase {
    public TestCloneOMElement(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    protected void runTest() throws Throwable {
        SOAPEnvelope soapEnvelope = SOAPSampleSet.WSA.getMessage(spec).getAdapter(SOAPSampleAdapter.class).getSOAPEnvelope(metaFactory);
        SOAPBody body = soapEnvelope.getBody();

        OMElement firstClonedBodyElement = body.cloneOMElement();
        OMElement secondClonedBodyElement = body.cloneOMElement();

        // cloneOMElement creates plain OMElements
        assertFalse(firstClonedBodyElement instanceof SOAPBody);
        assertFalse(secondClonedBodyElement instanceof SOAPBody);
        
        // first check whether both have the same information
        XMLAssert.assertXMLEqual(body.toString(),
                                 firstClonedBodyElement.toString());
        XMLAssert.assertXMLEqual(body.toString(),
                                 secondClonedBodyElement.toString());
        XMLAssert.assertXMLEqual(firstClonedBodyElement.toString(),
                                 secondClonedBodyElement.toString());

        // The clone is expected to be orphaned
        assertNull(firstClonedBodyElement.getParent());
        assertNull(secondClonedBodyElement.getParent());

        soapEnvelope.close(false);
    }
}
