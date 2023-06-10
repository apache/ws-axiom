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
package org.apache.axiom.ts.saaj.element;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPPart;

import org.apache.axiom.ts.saaj.SAAJImplementation;
import org.apache.axiom.ts.saaj.SAAJTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Tests the behavior of the {@link Node#getOwnerDocument()} method when invoked on a {@link
 * SOAPElement} as well as the properties of the returned document.
 */
public class TestGetOwnerDocument extends SAAJTestCase {
    public TestGetOwnerDocument(SAAJImplementation saajImplementation, SOAPSpec spec) {
        super(saajImplementation, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc = newSOAPFactory().createElement(new QName("test")).getOwnerDocument();
        assertThat(doc).isNotInstanceOf(SOAPPart.class);
        assertThat(doc.createElementNS(null, "test")).isInstanceOf(SOAPElement.class);
    }
}
