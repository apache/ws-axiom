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

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.ds.custombuilder.ByteArrayCustomBuilder;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.ts.soap.SOAPSampleAdapter;
import org.apache.axiom.ts.soap.SOAPSampleSet;

public class TestRegisterCustomBuilder extends SOAPTestCase {
    public TestRegisterCustomBuilder(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        StAXSOAPModelBuilder builder = (StAXSOAPModelBuilder)SOAPSampleSet.WSA.getMessage(spec).getAdapter(SOAPSampleAdapter.class).getBuilder(metaFactory);
        builder.registerCustomBuilder(new QName("http://www.w3.org/2005/08/addressing", "To"), 3,
                new ByteArrayCustomBuilder("utf-8"));
        SOAPHeader header = builder.getSOAPEnvelope().getHeader();
        ArrayList al = header.getHeaderBlocksWithNSURI("http://www.w3.org/2005/08/addressing");
        assertEquals(al.size(), 4);
        for (int i=0; i<al.size(); i++){
            SOAPHeaderBlock shb = (SOAPHeaderBlock)al.get(i);
            if ("To".equals(shb.getLocalName())) {
                assertNotNull(shb.getDataSource());
            }
        }
    }
}
