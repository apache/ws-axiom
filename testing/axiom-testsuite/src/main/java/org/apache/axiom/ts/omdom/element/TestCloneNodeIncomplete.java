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
package org.apache.axiom.ts.omdom.element;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Tests {@link Node#cloneNode(boolean)} on an element that is not completely built. */
public class TestCloneNodeIncomplete extends AxiomTestCase {
    public TestCloneNodeIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        Element element =
                (Element)
                        OMXMLBuilderFactory.createOMBuilder(
                                        metaFactory.getOMFactory(),
                                        new StringReader("<root><child1/><child2/></root>"))
                                .getDocumentElement();
        Element clone = (Element) element.cloneNode(true);
        assertAbout(xml())
                .that(xml(Element.class, clone))
                .hasSameContentAs(xml(Element.class, element));
    }
}
