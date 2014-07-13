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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.InvocationCounter;
import org.apache.axiom.ts.AxiomTestCase;

public class TestInvalidXML extends AxiomTestCase {
    public TestInvalidXML(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        XMLStreamReader originalReader = StAXUtils.createXMLStreamReader(AbstractTestCase.getTestResource("invalid_xml.xml"));
        InvocationCounter invocationCounter = new InvocationCounter();
        XMLStreamReader reader = (XMLStreamReader)invocationCounter.createProxy(originalReader);
        
        OMXMLParserWrapper stAXOMBuilder =
                OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(),
                                                        reader);
        
        Exception exception = null;
        while (exception == null || stAXOMBuilder.isCompleted()) {
            try {
                stAXOMBuilder.next();
            } catch (Exception e) {
                exception =e;
            }
        }
        
        assertTrue("Expected an exception because invalid_xml.xml is wrong", exception != null);
        
        assertTrue(invocationCounter.getInvocationCount() > 0);
        invocationCounter.reset();
        
        // Intentionally call builder again to make sure the same error is returned.
        Exception exception2 = null;
        try {
            stAXOMBuilder.next();
        } catch (Exception e) {
            exception2 = e;
        }
        
        assertEquals(0, invocationCounter.getInvocationCount());
        
        assertTrue("Expected a second exception because invalid_xml.xml is wrong", exception2 != null);
        assertTrue("Expected the same exception. first=" + exception + " second=" + exception2, 
                    exception.getMessage().equals(exception2.getMessage()));
    }
}
