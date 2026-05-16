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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMElement#setText(QName)} when invoked on an element that has
 * children.
 */
public class TestSetTextQNameWithExistingChildren extends AxiomTestCase {
    @Inject
    private OMMetaFactory metaFactory;

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement("TestElement", null);

        // Add some children of various types
        factory.createOMText(element, "some text");
        factory.createOMText(element, "cdata section", OMNode.CDATA_SECTION_NODE);
        factory.createOMComment(element, "comment");
        factory.createOMProcessingInstruction(element, "piTarget", "piData");
        factory.createOMElement("child", null, element);

        QName qname = new QName("urn:ns1", "test", "ns");
        element.setText(qname);

        assertThat(element.getText()).isEqualTo("ns:test");

        // Check that OMElement#setText() has created the expected nodes
        OMNode child = element.getFirstOMChild();
        assertThat(child).isInstanceOf(OMText.class);
        assertThat(child.getParent()).isSameAs(element);
        assertThat(((OMText) child).getText()).isEqualTo("ns:test");
        assertThat(child.getNextOMSibling()).isNull();
    }
}
