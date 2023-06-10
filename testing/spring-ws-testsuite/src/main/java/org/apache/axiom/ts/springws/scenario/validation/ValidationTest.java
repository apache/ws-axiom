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
package org.apache.axiom.ts.springws.scenario.validation;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.scenario.ScenarioConfig;
import org.apache.axiom.ts.springws.scenario.ScenarioTestCase;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class ValidationTest extends ScenarioTestCase {
    public ValidationTest(ScenarioConfig config, SOAPSpec spec) {
        super(config, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        StockQuoteClient client = context.getBean(StockQuoteClient.class);

        assertEquals(105.37, client.getQuote("GOOG"), 0.001);

        try {
            client.getQuote("TOOLONG");
            fail("Expected SoapFaultClientException");
        } catch (SoapFaultClientException ex) {
            assertEquals(spec.getSenderFaultCode(), ex.getFaultCode());
            Iterator<SoapFaultDetailElement> it =
                    ex.getSoapFault().getFaultDetail().getDetailEntries();
            assertTrue(it.hasNext());
            assertEquals(
                    new QName("http://springframework.org/spring-ws", "ValidationError"),
                    it.next().getName());
        }
    }
}
