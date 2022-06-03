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
package org.apache.axiom.ts.om.container;

import static com.google.common.truth.Truth.assertThat;

import java.util.Iterator;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetDescendants extends AxiomTestCase {
    private final OMContainerFactory containerFactory;
    private final boolean includeSelf;

    public TestGetDescendants(
            OMMetaFactory metaFactory, OMContainerFactory containerFactory, boolean includeSelf) {
        super(metaFactory);
        this.containerFactory = containerFactory;
        containerFactory.addTestParameters(this);
        this.includeSelf = includeSelf;
        addTestParameter("includeSelf", includeSelf);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMContainer root = containerFactory.create(factory);
        OMElement child1 = factory.createOMElement("child", null, root);
        OMProcessingInstruction child2 =
                factory.createOMProcessingInstruction(root, "test", "data");
        OMText grandchild1 = factory.createOMText(child1, "text");
        OMComment grandchild2 = factory.createOMComment(child1, "text");
        Iterator<? extends OMSerializable> it = root.getDescendants(includeSelf);
        if (includeSelf) {
            assertThat(it.hasNext()).isTrue();
            assertThat(it.next()).isEqualTo(root);
        }
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(child1);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(grandchild1);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(grandchild2);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(child2);
        assertThat(it.hasNext()).isFalse();
    }
}
