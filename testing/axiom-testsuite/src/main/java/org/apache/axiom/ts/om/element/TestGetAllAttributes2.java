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
import java.util.Iterator;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Test {@link OMElement#getAllAttributes()} on a parsed document. Also check that the iterator
 * doesn't attempt to return namespace declarations.
 */
public class TestGetAllAttributes2 implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMElement element = AXIOMUtil.stringToOM(factory, "<e xmlns:p='urn:test' p:attr='test'/>");
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertThat(it.hasNext()).isTrue();
        OMAttribute attr = it.next();
        assertThat(attr.getNamespace().getNamespaceURI()).isEqualTo("urn:test");
        assertThat(attr.getLocalName()).isEqualTo("attr");
        assertThat(it.hasNext()).isFalse();
    }
}
