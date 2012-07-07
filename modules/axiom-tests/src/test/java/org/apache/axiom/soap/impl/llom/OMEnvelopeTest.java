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

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMTestCase;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

public class OMEnvelopeTest extends OMTestCase {
    public void testGetHeader1() {
        SOAPHeader header = soapEnvelope.getHeader();
        assertTrue("Header information retrieved not correct",
                   (header != null &&
                           header.getLocalName().equalsIgnoreCase("Header")));
    }

    private SOAPEnvelope getSecondEnvelope() throws Exception {
        return OMXMLBuilderFactory.createSOAPModelBuilder(getTestResource(
                TestConstants.SAMPLE1), null).getSOAPEnvelope();
    }

    public void testGetHeader2() throws Exception {
        SOAPHeader header = getSecondEnvelope().getHeader();
        assertTrue("Header information retrieved not correct",
                   (header != null &&
                           header.getLocalName().equalsIgnoreCase("Header")));
        header.close(false);
    }
}
