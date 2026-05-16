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
import com.google.inject.name.Named;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests the behavior of {@link OMNode#detach()}. */
public class TestDetachFirstChild extends AxiomTestCase {
    @Inject
    private OMMetaFactory metaFactory;

    private final boolean build;

    @Inject
    public TestDetachFirstChild(@Named("build") boolean build) {
        this.build = build;
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement root = AXIOMUtil.stringToOM(metaFactory.getOMFactory(), "<root><a/><b/></root>");
        if (build) {
            root.build();
        } else {
            assertThat(root.isComplete()).isFalse();
        }
        OMNode oldFirstChild = root.getFirstOMChild();
        assertThat(oldFirstChild).isNotNull();
        oldFirstChild.detach();
        OMNode newFirstChild = root.getFirstOMChild();
        assertThat(newFirstChild).isNotNull();
        assertThat(newFirstChild).isNotSameAs(oldFirstChild);
    }
}
