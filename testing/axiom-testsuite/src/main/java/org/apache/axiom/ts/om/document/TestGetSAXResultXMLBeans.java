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

import com.google.inject.Inject;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.ts.om.document.xmlbeans.OrderDocument;
import org.apache.axiom.ts.om.document.xmlbeans.OrderDocument.Order;
import org.apache.axiom.ts.om.document.xmlbeans.OrderDocument.Order.Item;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class TestGetSAXResultXMLBeans extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OrderDocument document = OrderDocument.Factory.newInstance();
        Order order = document.addNewOrder();
        order.setCustomerId("73107481");
        Item item = order.addNewItem();
        item.setPartId("P85-137-19");
        item.setQuantity(2);
        item = order.addNewItem();
        item.setPartId("O85-554-66");
        item.setQuantity(1);

        StringWriter out = new StringWriter();
        document.save(out);
        OMDocument omDocument = factory.createOMDocument();
        ContentHandler handler = omDocument.getSAXResult().getHandler();
        document.save(handler, (LexicalHandler) handler);

        assertAbout(xml()).that(xml(OMDocument.class, omDocument)).hasSameContentAs(out.toString());
    }
}
