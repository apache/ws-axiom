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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that {@link OMNode#insertSiblingBefore(OMNode)} fails if the node doesn't have a parent.
 */
public class TestInsertSiblingAfterOnOrphan implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {
        OMText text1 = factory.createOMText("text1");
        OMText text2 = factory.createOMText("text2");
        assertThatThrownBy(() -> text1.insertSiblingBefore(text2)).isInstanceOf(OMException.class);
    }
}
