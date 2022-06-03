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
package org.apache.axiom.ts.om.cross;

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;

public class TestAddChild extends CrossOMTestCase {
    public TestAddChild(OMMetaFactory metaFactory, OMMetaFactory altMetaFactory) {
        super(metaFactory, altMetaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement parent = metaFactory.getOMFactory().createOMElement("parent", null);
        OMElement orgChild = altMetaFactory.getOMFactory().createOMElement("child", null);
        parent.addChild(orgChild);
        OMElement child = (OMElement) parent.getFirstOMChild();
        assertThat(child).isNotSameInstanceAs(orgChild);
        assertThat(child.getLocalName()).isEqualTo("child");
    }
}
