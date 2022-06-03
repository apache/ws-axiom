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

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMDocument#getOMDocumentElement()} returns <code>null</code> after the existing
 * document element has been removed using {@link OMNode#detach()}. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-361">AXIOM-361</a>.
 */
public class TestGetOMDocumentElementAfterDetach extends AxiomTestCase {
    public TestGetOMDocumentElementAfterDetach(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDocument document = factory.createOMDocument();
        OMElement documentElement = factory.createOMElement("root", null, document);
        assertSame(documentElement, document.getOMDocumentElement());
        documentElement.detach();
        assertNull(document.getOMDocumentElement());
    }
}
