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
import static org.assertj.core.api.Assertions.fail;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.testutils.suite.MatrixTestCase;

public class TestGetNamespacesInScope implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMElement element = AXIOMUtil.stringToOM(factory, "<a xmlns:ns1='urn:ns1'><b xmlns:ns2='urn:ns2'/></a>");
        boolean ns1seen = false;
        boolean ns2seen = false;
        Iterator<OMNamespace> it = element.getFirstElement().getNamespacesInScope();
        int count = 0;
        while (it.hasNext()) {
            OMNamespace ns = it.next();
            count++;
            if (ns.getPrefix().equals("ns1")) {
                ns1seen = true;
                assertThat(ns.getNamespaceURI()).isEqualTo("urn:ns1");
            } else if (ns.getPrefix().equals("ns2")) {
                ns2seen = true;
                assertThat(ns.getNamespaceURI()).isEqualTo("urn:ns2");
            } else {
                fail("Unexpected prefix: " + ns.getPrefix());
            }
        }
        assertThat(count).isEqualTo(2);
        assertThat(ns1seen).isTrue();
        assertThat(ns2seen).isTrue();
    }
}
