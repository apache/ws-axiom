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

package org.apache.axiom.om;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSpec;

import junit.framework.TestCase;

import org.apache.axiom.ts.soap.SOAPSampleSet;

public abstract class OMTestCase extends TestCase {
    protected SOAPModelBuilder builder;
    protected OMFactory ombuilderFactory;
    protected SOAPFactory soapFactory;

    protected SOAPEnvelope soapEnvelope;

    public OMTestCase() {
        ombuilderFactory = OMAbstractFactory.getOMFactory();
        soapFactory = OMAbstractFactory.getSOAP11Factory();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        builder = OMXMLBuilderFactory.createSOAPModelBuilder(
                SOAPSampleSet.WSA.getMessage(SOAPSpec.SOAP11).getInputStream(), null);
        soapEnvelope = (SOAPEnvelope) builder.getDocumentElement();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        soapEnvelope.close(false);
    }
}
