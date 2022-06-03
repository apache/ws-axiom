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
import static org.apache.axiom.truth.AxiomTruth.assertThat;

import java.io.StringReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMContainer#addChild(OMNode)} works properly on a container that has been
 * created programmatically, but that has a child that is itself incomplete.
 *
 * <p>This is a regression test for an issue in older Axiom versions (where the {@link
 * OMContainer#build()}) method would fail on {@link OMDocument} instances.
 */
public class TestAddChildWithIncompleteSibling extends AxiomTestCase {
    private final OMContainerFactory containerFactory;

    public TestAddChildWithIncompleteSibling(
            OMMetaFactory metaFactory, OMContainerFactory containerFactory) {
        super(metaFactory);
        this.containerFactory = containerFactory;
        containerFactory.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMContainer container = containerFactory.create(factory);
        container.addChild(
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<a>test</a>"))
                        .getDocumentElement(true));
        assertThat(container.isComplete()).isFalse();
        container.addChild(factory.createOMText("test"));
        assertThat(container).hasNumberOfChildren(2);
    }
}
