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
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

public class TestDeclareDefaultNamespace1 extends AxiomTestCase {
    public TestDeclareDefaultNamespace1(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        /**
         * <RootElement xmlns="http://one.org"> <ns2:ChildElementOne
         * xmlns:ns2="http://ws.apache.org/axis2" xmlns="http://two.org"> <ChildElementTwo
         * xmlns="http://one.org" /> </ns2:ChildElementOne> </RootElement>
         */
        OMFactory omFac = metaFactory.getOMFactory();

        OMElement documentElement =
                omFac.createOMElement("RootElement", omFac.createOMNamespace("http://one.org", ""));

        OMNamespace ns = omFac.createOMNamespace("http://ws.apache.org/axis2", "ns2");
        OMElement childOne = omFac.createOMElement("ChildElementOne", ns, documentElement);
        childOne.declareDefaultNamespace("http://two.org");

        OMElement childTwo =
                omFac.createOMElement(
                        "ChildElementTwo", omFac.createOMNamespace("http://one.org", ""), childOne);

        assertEquals(
                2,
                getNumberOfOccurrences(
                        documentElement.toStringWithConsume(), "xmlns=\"http://one.org\""));
    }
}
