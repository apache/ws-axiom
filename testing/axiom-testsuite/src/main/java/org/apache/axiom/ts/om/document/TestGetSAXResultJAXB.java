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

package org.apache.axiom.ts.om.document;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.document.jaxb.Order;
import org.apache.axiom.ts.om.document.jaxb.OrderItem;

public class TestGetSAXResultJAXB extends AxiomTestCase {
    public TestGetSAXResultJAXB(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        List<OrderItem> items = new ArrayList<OrderItem>(2);
        OrderItem item = new OrderItem();
        item.setPartId("P85-137-19");
        item.setQuantity(2);
        items.add(item);
        item = new OrderItem();
        item.setPartId("O85-554-66");
        item.setQuantity(1);
        items.add(item);
        Order order = new Order();
        order.setCustomerId("73107481");
        order.setItems(items);

        Marshaller marshaller = JAXBContext.newInstance(Order.class).createMarshaller();
        StringWriter out = new StringWriter();
        marshaller.marshal(order, out);

        OMDocument document = metaFactory.getOMFactory().createOMDocument();
        marshaller.marshal(order, document.getSAXResult().getHandler());

        assertAbout(xml()).that(xml(OMDocument.class, document)).hasSameContentAs(out.toString());
    }
}
