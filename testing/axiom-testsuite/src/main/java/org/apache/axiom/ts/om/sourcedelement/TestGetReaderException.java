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
package org.apache.axiom.ts.om.sourcedelement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMSourcedElement} if {@link OMDataSource#getReader()} throws an
 * exception. In this case, the code must complete properly (and not end in an infinite loop) and
 * propagate the original exception (wrapped in an {@link OMException}).
 */
public class TestGetReaderException extends AxiomTestCase {
    public TestGetReaderException(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element =
                factory.createOMElement(
                        new AbstractPullOMDataSource() {
                            @Override
                            public XMLStreamReader getReader() throws XMLStreamException {
                                throw new XMLStreamException("Test exception");
                            }

                            @Override
                            public boolean isDestructiveRead() {
                                return true;
                            }
                        });
        try {
            element.getLocalName();
            fail("Expected OMException");
        } catch (OMException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof XMLStreamException);
            assertEquals("Test exception", cause.getMessage());
        }
    }
}
