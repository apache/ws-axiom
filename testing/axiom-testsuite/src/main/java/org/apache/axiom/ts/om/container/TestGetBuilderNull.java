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

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMContainer#getBuilder()} returns {@code null} on a programmatically created
 * node.
 */
public class TestGetBuilderNull extends AxiomTestCase {
    private final OMContainerFactory containerFactory;

    public TestGetBuilderNull(OMMetaFactory metaFactory, OMContainerFactory containerFactory) {
        super(metaFactory);
        this.containerFactory = containerFactory;
        containerFactory.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMContainer container = containerFactory.create(metaFactory.getOMFactory());
        assertThat(container.getBuilder()).isNull();
    }
}
