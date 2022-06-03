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
package org.apache.axiom.ts.om.element;

import java.io.StringReader;

import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMSerializable#build()} on an {@link OMElement} that has been
 * discarded. In this case the method is expected to throw a {@link NodeUnavailableException}.
 */
public class TestBuildDiscarded extends AxiomTestCase {
    public TestBuildDiscarded(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent =
                OMXMLBuilderFactory.createOMBuilder(
                                factory, new StringReader("<root><a/><b/></root>"))
                        .getDocumentElement();
        // Partially build the parent
        parent.getFirstOMChild();
        parent.discard();
        try {
            parent.build();
            fail("Expected NodeUnavailableException");
        } catch (NodeUnavailableException ex) {
            // Expected
        }
    }
}
