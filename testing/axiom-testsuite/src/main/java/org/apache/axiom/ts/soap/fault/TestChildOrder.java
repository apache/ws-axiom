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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter;
import org.apache.axiom.ts.soap.SOAPFaultChild;
import org.apache.axiom.ts.soap.SOAPSpec;
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
public class TestChildOrder implements MatrixTestCase {
    @Inject
    private SOAPSpec spec;

    @Inject
    private SOAPFactory soapFactory;

    @Inject
    private @Named("inputOrder") SOAPFaultChild[] inputOrder;

    @Inject
    private SerializationStrategy serializationStrategy;

    @Override
    public void runTest() throws Throwable {
        SOAPFault fault = soapFactory.createSOAPFault();
        // Add the elements in the specified order.
        for (int i = 0; i < inputOrder.length; i++) {
            SOAPElementTypeAdapter adapter = inputOrder[i].getAdapter(SOAPElementTypeAdapter.class);
            adapter.getSetter().invoke(fault, adapter.create(soapFactory));
        }
        // Calculate the order in which we expect to see the children. Note that a given type
        // may be added multiple times. Therefore we need to use a Set.
        SortedSet<SOAPFaultChild> outputOrder = new TreeSet<>(Comparator.comparingInt(SOAPFaultChild::getOrder));
        outputOrder.addAll(Arrays.asList(inputOrder));
        // Check the result using the given serialization strategy
        Document document = DOMImplementation.XERCES.parse(
                serializationStrategy.serialize(fault).getInputSource());
        Element domFault = document.getDocumentElement();
        Node child = domFault.getFirstChild();
        for (SOAPFaultChild type : outputOrder) {
            assertThat(child).isNotNull();
            assertThat(child.getLocalName()).isEqualTo(type.getQName(spec).getLocalPart());
            child = child.getNextSibling();
        }
        assertThat(child).isNull();
    }
}
