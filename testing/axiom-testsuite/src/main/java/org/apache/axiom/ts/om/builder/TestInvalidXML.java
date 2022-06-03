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
package org.apache.axiom.ts.om.builder;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.InvocationCounter;
import org.apache.axiom.ts.AxiomTestCase;

public class TestInvalidXML extends AxiomTestCase {
    public TestInvalidXML(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        XMLStreamReader originalReader =
                StAXUtils.createXMLStreamReader(
                        TestInvalidXML.class.getResourceAsStream("invalid_xml.xml"));
        InvocationCounter invocationCounter = new InvocationCounter();
        XMLStreamReader reader = (XMLStreamReader) invocationCounter.createProxy(originalReader);

        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader)
                        .getDocumentElement();

        DeferredParsingException exception;
        try {
            element.getNextOMSibling();
            exception = null;
        } catch (DeferredParsingException ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull();

        assertTrue(invocationCounter.getInvocationCount() > 0);
        invocationCounter.reset();

        // Intentionally call builder again to make sure the same error is returned.
        DeferredParsingException exception2;
        try {
            element.getNextOMSibling();
            exception2 = null;
        } catch (DeferredParsingException ex) {
            exception2 = ex;
        }

        assertThat(invocationCounter.getInvocationCount()).isEqualTo(0);

        assertThat(exception2).isNotNull();
        assertThat(exception2.getMessage()).isEqualTo(exception.getMessage());
    }
}
