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
package org.apache.axiom.ts.om.sourcedelement;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;

/**
 * Tests {@link OMContainer#removeChildren()} on an {@link OMSourcedElement} that is not expanded.
 * In this case the sourced element needs to be expanded to build any attributes present on the
 * element and to ensure that the information about the name of the element is complete.
 */
public class TestRemoveChildrenUnexpanded extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMSourcedElement element =
                factory.createOMElement(new StringOMDataSource("<element attr='value'><a/></element>"));
        element.removeChildren();
        // Check that the attribute has been added
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertThat(it.hasNext()).isTrue();
        OMAttribute attr = it.next();
        assertThat(attr.getLocalName()).isEqualTo("attr");
        assertThat(attr.getAttributeValue()).isEqualTo("value");
        assertThat(it.hasNext()).isFalse();
        // Check that the element is empty
        assertThat(element.getFirstOMChild()).isNull();
    }
}
