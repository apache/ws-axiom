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
import com.google.inject.name.Named;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.AXIOMUtil;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the behavior of {@link OMContainer#addChild(OMNode)} when used to add a node to an element
 * it is already a child of. In this case, the expected result is that the node is moved to the end
 * of the list of children.
 */
public class TestAddChildWithSameParent implements Executable {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("build")
    private boolean build;

    @Override
    public void execute() throws Throwable {
        OMElement parent = AXIOMUtil.stringToOM(factory, "<parent><a/><b/><c/></parent>");
        if (build) {
            parent.build();
        }
        OMElement b = (OMElement) parent.getFirstOMChild().getNextOMSibling();
        parent.addChild(b);
        OMElement child = (OMElement) parent.getFirstOMChild();
        assertThat(child.getLocalName()).isEqualTo("a");
        child = (OMElement) child.getNextOMSibling();
        assertThat(child.getLocalName()).isEqualTo("c");
        child = (OMElement) child.getNextOMSibling();
        assertThat(b).isSameAs(child);
        assertThat(child.getNextOMSibling()).isNull();
        assertThat(b.getParent()).isSameAs(parent);
    }
}
