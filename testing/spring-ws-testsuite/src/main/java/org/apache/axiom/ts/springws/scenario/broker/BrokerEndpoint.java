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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.Namespace;
import org.springframework.ws.server.endpoint.annotation.Namespaces;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.springframework.xml.transform.TransformerHelper;

@Endpoint
public class BrokerEndpoint {
    private final CustomerService customerService;
    private final TransformerHelper transformerHelper = new TransformerHelper();
    private final Deque<String> orderQueue = new LinkedList<String>();

    public BrokerEndpoint(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PayloadRoot(namespace = "urn:broker", localPart = "Order")
    @ResponsePayload
    @Namespaces(@Namespace(prefix = "p", uri = "urn:broker"))
    public OrderStatus order(
            @XPathParam("/p:Order/p:Customer") Integer customer,
            @RequestPayload Source payloadSource)
            throws UnknownCustomerException, TransformerException {
        customerService.validateCustomer(customer);
        StringWriter sw = new StringWriter();
        transformerHelper.transform(payloadSource, new StreamResult(sw));
        String payload = sw.toString();
        synchronized (orderQueue) {
            orderQueue.addLast(payload);
            orderQueue.notify();
        }
        OrderStatus status = new OrderStatus();
        status.setReceived(new Date());
        return status;
    }

    @PayloadRoot(namespace = "urn:broker", localPart = "RetrieveNextOrder")
    @ResponsePayload
    public Source retrieveNextOrder() throws InterruptedException {
        synchronized (orderQueue) {
            while (orderQueue.isEmpty()) {
                orderQueue.wait();
            }
            return new StreamSource(new StringReader(orderQueue.removeFirst()));
        }
    }
}
