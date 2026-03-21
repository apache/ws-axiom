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
package org.apache.axiom.ts.om.attribute;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.om.SetNamespaceTestCase;

/**
 * Test the behavior of {@link OMNamedInformationItem#setNamespace(OMNamespace, boolean)} on an
 * {@link OMAttribute}.
 */
public class TestSetNamespace extends SetNamespaceTestCase {
    /**
     * Test parameters for {@link TestSetNamespace}.
     *
     * @param declare the value of the {@code declare} argument
     * @param owner flag indicating whether the attribute should have an owner element
     */
    public record Params(
            String namespaceURI,
            String prefix,
            boolean declare,
            boolean owner,
            String prefixInScope,
            boolean invalid,
            String expectedPrefix,
            boolean expectNSDecl)
            implements SetNamespaceTestCase.Params {}

    private final Params params;

    public TestSetNamespace(OMMetaFactory metaFactory, Params params) {
        super(metaFactory, params);
        this.params = params;
        addTestParameter("declare", params.declare());
        addTestParameter("owner", params.owner());
    }

    @Override
    protected boolean context() {
        return params.owner();
    }

    @Override
    protected OMNamedInformationItem node(OMFactory factory, OMElement context) {
        if (context != null) {
            return context.addAttribute("attr", "value", null);
        } else {
            return factory.createOMAttribute("attr", null, "value");
        }
    }

    @Override
    protected void setNamespace(OMNamedInformationItem node, OMNamespace ns) {
        node.setNamespace(ns, params.declare());
    }
}
