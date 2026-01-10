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
package org.apache.axiom.systest.springws;

import junit.framework.TestCase;

import org.apache.axiom.soap.SOAPMessage;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

public class AxiomSoapMessageFactoryTest extends TestCase {
    /**
     * Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-444">AXIOM-444</a>.
     *
     * @throws Exception
     */
    public void testCreateWebServiceMessage() throws Exception {
        AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
        mf.afterPropertiesSet();
        AxiomSoapMessage swsMessage = mf.createWebServiceMessage();
        SOAPMessage message = swsMessage.getAxiomMessage();
        // Spring-WS uses SOAPFactory#createSOAPMessage(OMXMLParserWrapper) with a null argument.
        // We need to make sure that we nevertheless get a SOAPMessage that is in state complete.
        assertTrue(message.isComplete());
    }
}
