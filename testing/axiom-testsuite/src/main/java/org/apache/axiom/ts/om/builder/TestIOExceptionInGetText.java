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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.InvocationCounter;
import org.apache.axiom.testutils.io.ExceptionInputStream;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Test the behavior of the builder when an exception is thrown by {@link
 * XMLStreamReader#getText()}. The test is only effective if the StAX implementation lazily loads
 * the character data for a {@link javax.xml.stream.XMLStreamConstants#CHARACTERS} event. This is
 * the case for Woodstox. It checks that after the exception is thrown by the parser, the builder no
 * longer attempts to access the parser.
 */
public class TestIOExceptionInGetText extends AxiomTestCase {
    public TestIOExceptionInGetText(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        // Construct a stream that will throw an exception in the middle of a text node.
        // We need to create a very large document, because some parsers (such as some
        // versions of XLXP) have a large input buffer and would throw an exception already
        // when the XMLStreamReader is created.
        StringBuffer xml = new StringBuffer("<root>");
        for (int i = 0; i < 100000; i++) {
            xml.append('x');
        }
        InputStream in =
                new ExceptionInputStream(
                        new ByteArrayInputStream(xml.toString().getBytes("ASCII")));

        XMLStreamReader originalReader = StAXUtils.createXMLStreamReader(in);
        InvocationCounter invocationCounter = new InvocationCounter();
        XMLStreamReader reader = (XMLStreamReader) invocationCounter.createProxy(originalReader);

        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader)
                        .getDocumentElement();

        try {
            element.getNextOMSibling();
            fail("Expected exception");
        } catch (Exception ex) {
            // Expected
        }

        assertTrue(invocationCounter.getInvocationCount() > 0);
        invocationCounter.reset();

        try {
            element.getNextOMSibling();
            fail("Expected exception");
        } catch (Exception ex) {
            // Expected
        }

        assertEquals(0, invocationCounter.getInvocationCount());
    }
}
