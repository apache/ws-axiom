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

/**
 * Tests the behavior of {@link OMElement#setNamespace(OMNamespace)} and {@link
 * OMNamedInformationItem#setNamespace(OMNamespace, boolean)}.
 */
public class TestSetNamespace extends SetNamespaceTestCase {
    private final Boolean declare;

    public TestSetNamespace(
            OMMetaFactory metaFactory,
            String namespaceURI,
            String prefix,
            Boolean declare,
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
        if (declare != null) {
            addTestParameter("declare", declare.booleanValue());
        }
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
        if (declare == null) {
            ((OMElement) node).setNamespace(ns);
        } else {
            node.setNamespace(ns, declare.booleanValue());
        }
    }
}
