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

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;

public class TestInsertSibling extends CrossOMTestCase {
    private final boolean before;

    public TestInsertSibling(
            OMMetaFactory metaFactory, OMMetaFactory altMetaFactory, boolean before) {
        super(metaFactory, altMetaFactory);
        this.before = before;
        addTestParameter("before", before);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", null);
        OMText child = factory.createOMText(parent, "test");
        OMComment orgSibling = altMetaFactory.getOMFactory().createOMComment(null, "test");
        if (before) {
            child.insertSiblingBefore(orgSibling);
        } else {
            child.insertSiblingAfter(orgSibling);
        }
        OMComment sibling =
                (OMComment) (before ? child.getPreviousOMSibling() : child.getNextOMSibling());
        assertThat(sibling).isNotSameInstanceAs(orgSibling);
        assertThat(sibling.getValue()).isEqualTo("test");
    }
}
