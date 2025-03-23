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
package org.apache.axiom.ts.springws.scenario.broker;

import static org.junit.Assert.assertThrows;

import java.util.Locale;

import javax.xml.transform.Source;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.scenario.ScenarioConfig;
import org.apache.axiom.ts.springws.scenario.ScenarioTestCase;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Broker scenario test.
 *
 * <p>The test uses a method endpoint and covers the following aspects:
 *
 * <ul>
 *   <li>{@link XPathParam} parameters.
 *   <li>{@link Source} parameters and return values.
 *   <li>Endpoint methods with multiple parameters that require procession the payload.
 *   <li>SOAP faults.
 * </ul>
 */
public class BrokerScenarioTest extends ScenarioTestCase {
    public BrokerScenarioTest(ScenarioConfig config, SOAPSpec spec) {
        super(config, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        BrokerClient client = context.getBean(BrokerClient.class);

        Order order = new Order();
        order.setCustomer(47892);
        Order.Item item1 = new Order.Item();
        item1.setStock("GOOG");
        item1.setCount(100);
        Order.Item item2 = new Order.Item();
        item2.setStock("MSFT");
        item2.setCount(10);
        order.setItems(new Order.Item[] {item1, item2});
        OrderStatus status = client.order(order);
        assertNotNull(status.getReceived());

        Order receivedOrder = client.receiveNextOrder();
        assertNotNull(receivedOrder);
        assertEquals(order.getCustomer(), receivedOrder.getCustomer());
        assertEquals(order.getItems().length, receivedOrder.getItems().length);
        assertEquals(order.getItems()[0].getStock(), receivedOrder.getItems()[0].getStock());
        assertEquals(order.getItems()[0].getCount(), receivedOrder.getItems()[0].getCount());

        order.setCustomer(23629);
        // SOAP 1.2 fault processing is locale dependent
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        try {
            SoapFaultClientException ex =
                    assertThrows(SoapFaultClientException.class, () -> client.order(order));
            assertEquals(spec.getSenderFaultCode(), ex.getFaultCode());
            assertEquals("Customer 23629 unknown", ex.getMessage());
        } finally {
            Locale.setDefault(oldLocale);
        }
    }
}
