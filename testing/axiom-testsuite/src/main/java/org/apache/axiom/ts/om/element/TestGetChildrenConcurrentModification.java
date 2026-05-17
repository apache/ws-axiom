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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;

/**
 * Tests that the iterator returned by {@link OMContainer#getChildren()} throws a {@link
 * ConcurrentModificationException} if the current node is removed using a method other than {@link
 * Iterator#remove()}.
 */
public class TestGetChildrenConcurrentModification extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMElement parent = factory.createOMElement("parent", null);
        factory.createOMElement("child1", null, parent);
        factory.createOMElement("child2", null, parent);
        factory.createOMElement("child3", null, parent);
        Iterator<OMNode> it = parent.getChildren();
        it.next();
        OMElement child2 = (OMElement) it.next();
        child2.detach();
        assertThatThrownBy(it::next).isInstanceOf(ConcurrentModificationException.class);
    }
}
