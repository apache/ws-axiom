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
package org.apache.axiom.ts.soap;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.AxiomTestCase;

public abstract class SOAPTestCase extends AxiomTestCase {
    protected final SOAPSpec spec;
    protected SOAPFactory soapFactory;
    protected SOAPFactory altSoapFactory;

    public SOAPTestCase(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory);
        this.spec = spec;
        addTestParameter("spec", spec.getName());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        soapFactory = spec.getAdapter(FactorySelector.class).getFactory(metaFactory);
        altSoapFactory =
                spec.getAltSpec().getAdapter(FactorySelector.class).getFactory(metaFactory);
    }

    protected SOAPHeaderBlock createSOAPHeaderBlock() {
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        ;
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        return soapFactory.createSOAPHeaderBlock("testHeaderBlock", namespace, soapHeader);
    }
}
