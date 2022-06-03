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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the result of {@link OMSerializable#isComplete()} on an {@link OMElement} when deferred
 * parsing is in effect.
 */
public class TestIsCompleteWithParser extends AxiomTestCase {
    public TestIsCompleteWithParser(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(), new StringReader("<a><b/></a>"))
                        .getDocumentElement();
        assertFalse(element.isComplete());
        element.getFirstElement().getNextOMSibling();
        assertTrue(element.isComplete());
        element.getNextOMSibling();
        assertTrue(element.isComplete());
    }
}
