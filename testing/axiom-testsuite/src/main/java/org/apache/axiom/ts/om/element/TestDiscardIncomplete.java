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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPSampleSet;

/** Test the discard method */
public class TestDiscardIncomplete extends AxiomTestCase {
    public TestDiscardIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement documentElement = null;

        // first build the OM tree without caching and see whether we can discard
        // an element from it
        // TODO: we shouldn't use a SOAP message here
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(
                        metaFactory.getOMFactory(),
                        SOAPSampleSet.WSA.getMessage(SOAPSpec.SOAP11).getInputStream());
        documentElement = builder.getDocumentElement();

        documentElement.getFirstElement().discard();

        String envelopeString = documentElement.toStringWithConsume();

        documentElement.close(false);
    }
}
