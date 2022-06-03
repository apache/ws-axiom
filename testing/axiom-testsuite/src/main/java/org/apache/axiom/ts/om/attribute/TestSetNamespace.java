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
    private final boolean declare;
    private final boolean owner;

    /**
     * Constructor.
     *
     * @param metaFactory the meta factory
     * @param namespaceURI the namespace URI to set or <code>null</code> to set a <code>null</code>
     *     {@link OMNamespace}
     * @param prefix the prefix to set or <code>null</code> to generate a prefix
     * @param declare the value of the <code>declare</code> argument
     * @param owner flag indicating whether the attribute should have an owner element
     * @param prefixInScope the prefix of an existing namespace declaration in scope for the given
     *     namespace URI, or <code>null</code> if no matching namespace declaration is in scope
     * @param invalid flag indicating whether the namespace is invalid and {@link
     *     OMNamedInformationItem#setNamespace(OMNamespace, boolean)} is expected to throw an {@link
     *     IllegalArgumentException}
     * @param expectedPrefix the expected prefix of the attribute after the invocation of {@link
     *     OMNamedInformationItem#setNamespace(OMNamespace, boolean)}, or null if the method is
     *     expected to generate a prefix
     * @param expectNSDecl indicates whether {@link OMNamedInformationItem#setNamespace(OMNamespace,
     *     boolean)} is expected to generate a namespace declaration on the owner element
     */
    public TestSetNamespace(
            OMMetaFactory metaFactory,
            String namespaceURI,
            String prefix,
            boolean declare,
            boolean owner,
            String prefixInScope,
            boolean invalid,
            String expectedPrefix,
            boolean expectNSDecl) {
        super(
                metaFactory,
                namespaceURI,
                prefix,
                prefixInScope,
                invalid,
                expectedPrefix,
                expectNSDecl);
        this.declare = declare;
        this.owner = owner;
        addTestParameter("declare", declare);
        addTestParameter("owner", owner);
    }

    @Override
    protected boolean context() {
        return owner;
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
        node.setNamespace(ns, declare);
    }
}
