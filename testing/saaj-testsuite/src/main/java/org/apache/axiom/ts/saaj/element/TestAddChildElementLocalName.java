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

import jakarta.xml.soap.SOAPElement;

import org.apache.axiom.ts.saaj.SAAJImplementation;
import org.apache.axiom.ts.saaj.SAAJTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;

/** Tests the behavior of {@link SOAPElement#addChildElement(String)}. */
public class TestAddChildElementLocalName extends SAAJTestCase {
    public TestAddChildElementLocalName(SAAJImplementation saajImplementation, SOAPSpec spec) {
        super(saajImplementation, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPElement root = newSOAPFactory().createElement("root", "p", "urn:test");
        SOAPElement element = root.addChildElement("child");
        assertThat(element.getLocalName()).isEqualTo("child");
        assertThat(element.getNamespaceURI()).isNull();
        assertThat(element.getPrefix()).isNull();
        assertThat(element.getParentNode()).isSameInstanceAs(root);
        assertThat(element.getAttributes().getLength()).isEqualTo(0);
    }
}
