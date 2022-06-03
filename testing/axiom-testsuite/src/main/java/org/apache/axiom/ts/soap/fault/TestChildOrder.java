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
package org.apache.axiom.ts.soap.fault;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter;
import org.apache.axiom.ts.soap.SOAPFaultChild;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests that the children added using methods such as {@link SOAPFault#setCode(SOAPFaultCode)} and
 * {@link SOAPFault#setReason(SOAPFaultReason)} appear in the order required by the SOAP specs when
 * the {@link SOAPFault} is serialized.
 *
 * <p>Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-392">AXIOM-392</a>.
 */
public class TestChildOrder extends SOAPTestCase {
    private final SOAPFaultChild[] inputOrder;
    private final SerializationStrategy serializationStrategy;

    public TestChildOrder(
            OMMetaFactory metaFactory,
            SOAPSpec spec,
            SOAPFaultChild[] inputOrder,
            SerializationStrategy serializationStrategy) {
        super(metaFactory, spec);
        this.inputOrder = inputOrder;
        this.serializationStrategy = serializationStrategy;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < inputOrder.length; i++) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(
                    inputOrder[i]
                            .getAdapter(SOAPElementTypeAdapter.class)
                            .getType()
                            .getSimpleName());
        }
        addTestParameter("inputOrder", buffer.toString());
        serializationStrategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPFault fault = soapFactory.createSOAPFault();
        // Add the elements in the specified order.
        for (int i = 0; i < inputOrder.length; i++) {
            SOAPElementTypeAdapter adapter = inputOrder[i].getAdapter(SOAPElementTypeAdapter.class);
            adapter.getSetter().invoke(fault, adapter.create(soapFactory));
        }
        // Calculate the order in which we expect to see the children. Note that a given type
        // may be added multiple times. Therefore we need to use a Set.
        SortedSet<SOAPFaultChild> outputOrder =
                new TreeSet<>(
                        new Comparator<SOAPFaultChild>() {
                            @Override
                            public int compare(SOAPFaultChild o1, SOAPFaultChild o2) {
                                return o1.getOrder() - o2.getOrder();
                            }
                        });
        outputOrder.addAll(Arrays.asList(inputOrder));
        // Check the result using the given serialization strategy
        Document document =
                DOMImplementation.XERCES.parse(
                        serializationStrategy.serialize(fault).getInputSource());
        Element domFault = document.getDocumentElement();
        Node child = domFault.getFirstChild();
        for (SOAPFaultChild type : outputOrder) {
            assertNotNull(child);
            assertEquals(type.getQName(spec).getLocalPart(), child.getLocalName());
            child = child.getNextSibling();
        }
        assertNull(child);
    }
}
