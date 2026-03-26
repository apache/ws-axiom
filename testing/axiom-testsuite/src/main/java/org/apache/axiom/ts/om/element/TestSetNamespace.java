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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.om.SetNamespaceTestCase;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

/**
 * Tests the behavior of {@link OMElement#setNamespace(OMNamespace)} and {@link
 * OMNamedInformationItem#setNamespace(OMNamespace, boolean)}.
 */
public class TestSetNamespace extends SetNamespaceTestCase {
    /**
     * @param declare the value of the {@code declare} argument, or {@code null} to use the 1-arg
     *     overload
     */
    public record Params(
            String namespaceURI,
            String prefix,
            Boolean declare,
            String prefixInScope,
            boolean invalid,
            String expectedPrefix,
            boolean expectNSDecl)
            implements SetNamespaceTestCase.Params {}

    public static final ImmutableList<Params> PARAMS;

    static {
        ImmutableList.Builder<Params> builder = ImmutableList.builder();
        for (int i = 0; i < 3; i++) {
            Boolean declare = i == 0 ? null : Boolean.valueOf(i == 2);
            boolean implicitDeclare = declare == null || declare.booleanValue();
            // Valid
            builder.add(new Params("urn:test", "p", declare, null, false, "p", implicitDeclare));
            builder.add(new Params("urn:test", null, declare, null, false, null, implicitDeclare));
            builder.add(new Params("urn:test", "p", declare, "p", false, "p", false));
            builder.add(new Params("urn:test", "p", declare, "q", false, "p", implicitDeclare));
            builder.add(new Params("urn:test", null, declare, "p", false, "p", false));
            builder.add(new Params("urn:test", null, declare, "", false, "", false));
            builder.add(new Params("urn:test", "", declare, null, false, "", implicitDeclare));
            builder.add(new Params("urn:test", "", declare, "", false, "", false));
            builder.add(new Params("", "", declare, null, false, "", false));
            builder.add(new Params("", null, declare, null, false, "", false));
            builder.add(new Params(null, null, declare, null, false, "", false));
            // Invalid
            builder.add(new Params("", "p", declare, null, true, null, false));
        }
        PARAMS = builder.build();
    }

    private final Params params;

    @Inject
    public TestSetNamespace(OMMetaFactory metaFactory, Params params) {
        super(metaFactory, params);
        this.params = params;
    }

    @Override
    protected boolean context() {
        return true;
    }

    @Override
    protected OMNamedInformationItem node(OMFactory factory, OMElement context) {
        return context;
    }

    @Override
    protected void setNamespace(OMNamedInformationItem node, OMNamespace ns) {
        if (params.declare() == null) {
            ((OMElement) node).setNamespace(ns);
        } else {
            node.setNamespace(ns, params.declare().booleanValue());
        }
    }
}
