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

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMTestUtils;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.AxiomTestCase;

public class BadInputTest extends AxiomTestCase {
    private final String file;

    public BadInputTest(OMMetaFactory metaFactory, String file) {
        super(metaFactory);
        this.file = file;
        addTestProperty("file", file);
    }

    protected void runTest() throws Throwable {
        try {
            SOAPEnvelope soapEnvelope =
                    OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory,
                            AbstractTestCase.getTestResource("badsoap/" + file), null)
                            .getSOAPEnvelope();
            OMTestUtils.walkThrough(soapEnvelope);
            fail("this must failed gracefully with OMException");
        } catch (OMException e) {
            return;
        }
    }

    //done
    //    public void testNotnamespaceQualified() throws Exception {
    //        try {
    //            SOAPEnvelope soapEnvelope =
    //                    (SOAPEnvelope) OMTestUtils.getOMBuilder(new File(dir, "notnamespaceQualified.xml")).getDocumentElement();
    //            OMTestUtils.walkThrough(soapEnvelope);
    //            fail("this must failed gracefully with OMException or AxisFault");
    //        } catch (OMException e) {
    //            return;
    //        } catch (AxisFault e) {
    //            return;
    //        }
    //
    //    }
    //done
    //    public void testBodyNotQualified() throws Exception {
    //        try {
    //            SOAPEnvelope soapEnvelope =
    //                    (SOAPEnvelope) OMTestUtils.getOMBuilder(new File(dir, "bodyNotQualified.xml")).getDocumentElement();
    //            OMTestUtils.walkThrough(soapEnvelope);
    //            fail("this must failed gracefully with OMException or AxisFault");
    //        } catch (OMException e) {
    //            //we are OK!
    //            return;
    //        } catch (AxisFault e) {
    //            //we are OK here too!
    //            return;
    //        }
    //
    //    }
}
