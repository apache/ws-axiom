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
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetDescendants extends AxiomTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("includeSelf")
    private boolean includeSelf;

    @Override
    protected void runTest() throws Throwable {
        OMElement element = AXIOMUtil.stringToOM(factory, "<root><a><b><c><d/><e/></c></b><f/></a><g/></root>");
        // We intentionally get the descendants of <a> so that we can test containment
        // (the iterator must never return <g>, which is a sibling of <a>).
        Iterator<OMNode> it = element.getFirstElement().getDescendants(includeSelf);
        if (includeSelf) {
            assertThat(((OMElement) it.next()).getLocalName()).isEqualTo("a");
        }
        assertThat(((OMElement) it.next()).getLocalName()).isEqualTo("b");
        assertThat(((OMElement) it.next()).getLocalName()).isEqualTo("c");
        assertThat(((OMElement) it.next()).getLocalName()).isEqualTo("d");
        assertThat(((OMElement) it.next()).getLocalName()).isEqualTo("e");
        assertThat(((OMElement) it.next()).getLocalName()).isEqualTo("f");
        assertThat(it.hasNext()).isFalse();
    }
}
