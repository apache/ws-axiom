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
package org.apache.axiom.ts.om.node;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests the behavior of {@link OMNode#insertSiblingAfter(OMNode)} if the node is already a sibling.
 */
public class TestInsertSiblingAfterSameParent implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMElement parent = factory.createOMElement("test", null);
        OMText text1 = factory.createOMText("text1");
        OMText text2 = factory.createOMText("text2");
        OMText text3 = factory.createOMText("text3");
        parent.addChild(text1);
        parent.addChild(text2);
        parent.addChild(text3);
        text1.insertSiblingAfter(text3);
        assertThat(text3.getParent()).isSameAs(parent);
        Iterator<OMNode> it = parent.getChildren();
        assertThat(it.next()).isSameAs(text1);
        assertThat(it.next()).isSameAs(text3);
        assertThat(it.next()).isSameAs(text2);
        assertThat(it.hasNext()).isFalse();
    }
}
