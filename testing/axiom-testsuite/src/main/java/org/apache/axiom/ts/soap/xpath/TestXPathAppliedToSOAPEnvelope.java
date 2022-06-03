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
package org.apache.axiom.ts.soap.xpath;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-141">AXIOM-141</a>. */
public class TestXPathAppliedToSOAPEnvelope extends SOAPTestCase {
    private boolean createDocument;

    public TestXPathAppliedToSOAPEnvelope(
            OMMetaFactory metaFactory, SOAPSpec spec, boolean createDocument) {
        super(metaFactory, spec);
        this.createDocument = createDocument;
        addTestParameter("createDocument", createDocument);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement elem1 = soapFactory.createOMElement("elem1", null);
        OMElement elem2 = soapFactory.createOMElement("elem2", null);
        OMElement elem3 = soapFactory.createOMElement("elem3", null);
        elem2.addChild(elem3);
        elem1.addChild(elem2);
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        envelope.getBody().addChild(elem1);

        if (createDocument) {
            soapFactory.createOMDocument().addChild(envelope);
        }

        String XPathString = "//elem1";

        AXIOMXPath XPath = new AXIOMXPath(XPathString);
        OMNode node = (OMNode) XPath.selectSingleNode(envelope);

        assertNotNull(node);
    }
}
