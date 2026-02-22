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

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPElement;

import org.apache.axiom.ts.saaj.SAAJImplementation;
import org.apache.axiom.ts.saaj.SAAJTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.w3c.dom.NodeList;

public class TestSetParentElement extends SAAJTestCase {
    public TestSetParentElement(SAAJImplementation saajImplementation, SOAPSpec spec) {
        super(saajImplementation, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPElement parent = newSOAPFactory().createElement(new QName("parent"));
        SOAPElement child1 = parent.addChildElement(new QName("child1"));
        SOAPElement child2 =
                (SOAPElement) parent.getOwnerDocument().createElementNS(null, "child2");
        child2.setParentElement(parent);
        NodeList children = parent.getChildNodes();
        assertThat(children.getLength()).isEqualTo(2);
        assertThat(children.item(0)).isSameAs(child1);
        assertThat(children.item(1)).isSameAs(child2);
    }
}
