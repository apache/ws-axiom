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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.StringReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests {@link OMContainer#removeChildren()} on an {@link OMElement}. */
public class TestRemoveChildren extends AxiomTestCase {
    @Inject
    private OMFactory factory;

    private final boolean complete;

    @Inject
    public TestRemoveChildren(@Named("complete") boolean complete) {
        this.complete = complete;
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element = OMXMLBuilderFactory.createOMBuilder(
                        factory, new StringReader("<root><a>A</a><b>B</b></root>"))
                .getDocumentElement();
        if (complete) {
            element.build();
        }
        OMElement firstChild = (OMElement) element.getFirstOMChild();
        assertThat(element.isComplete()).isEqualTo(complete);
        assertThat(firstChild.isComplete()).isEqualTo(complete);
        element.removeChildren();
        // We still need to be able to get the content of the child we retrieved before
        // calling removeChildren.
        assertThat(firstChild.getText()).isEqualTo("A");
        // Test that the child has been detached correctly.
        assertThat(firstChild.getParent()).isNull();
        assertThat(firstChild.getPreviousOMSibling()).isNull();
        assertThat(firstChild.getNextOMSibling()).isNull();
        // Test that the element is now empty.
        assertThat(element.getFirstOMChild()).isNull();
        // Check that the element is in a clean state and that we are able to add
        // new children.
        element.addChild(factory.createOMElement("c", null));
        assertAbout(xml()).that(xml(OMElement.class, element)).hasSameContentAs("<root><c/></root>");
    }
}
