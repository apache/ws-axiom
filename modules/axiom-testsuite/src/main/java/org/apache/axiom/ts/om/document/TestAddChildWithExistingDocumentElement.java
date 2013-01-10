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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that an attempt to use {@link OMContainer#addChild(OMNode)} to add an {@link OMElement} to
 * an {@link OMDocument} that already has a document element results in an exception.
 */
public class TestAddChildWithExistingDocumentElement extends AxiomTestCase {
    public TestAddChildWithExistingDocumentElement(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDocument document = factory.createOMDocument();
        document.addChild(factory.createOMElement(new QName("root1")));
        try {
            document.addChild(factory.createOMElement(new QName("root2")));
            fail("Expected OMException");
        } catch (OMException ex) {
            // Expected
        }
    }
}
