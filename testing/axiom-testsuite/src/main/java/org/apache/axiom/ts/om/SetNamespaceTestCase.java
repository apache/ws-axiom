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
package org.apache.axiom.ts.om;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

public abstract class SetNamespaceTestCase extends AxiomTestCase {
    private final String namespaceURI;
    private final String prefix;
    private final String prefixInScope;
    private final boolean invalid;
    private final String expectedPrefix;
    private final boolean expectNSDecl;

    /**
     * Constructor.
     *
     * @param metaFactory the meta factory
     * @param namespaceURI the namespace URI to set or <code>null</code> to set a <code>null</code>
     *     {@link OMNamespace}
     * @param prefix the prefix to set or <code>null</code> to generate a prefix
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
    public SetNamespaceTestCase(
            OMMetaFactory metaFactory,
            String namespaceURI,
            String prefix,
            String prefixInScope,
            boolean invalid,
            String expectedPrefix,
            boolean expectNSDecl) {
        super(metaFactory);
        this.namespaceURI = namespaceURI;
        this.prefix = prefix;
        this.prefixInScope = prefixInScope;
        this.invalid = invalid;
        this.expectedPrefix = expectedPrefix;
        this.expectNSDecl = expectNSDecl;
        if (namespaceURI != null) {
            addTestParameter("uri", namespaceURI);
        }
        if (prefix != null) {
            addTestParameter("prefix", prefix);
        }
        if (prefixInScope != null) {
            addTestParameter("prefixInScope", prefixInScope);
        }
        addTestParameter("invalid", invalid);
    }

    protected abstract boolean context();

    protected abstract OMNamedInformationItem node(OMFactory factory, OMElement context);

    protected abstract void setNamespace(OMNamedInformationItem node, OMNamespace ns);

    @Override
    protected final void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element;
        OMNamedInformationItem node;
        if (context()) {
            // To avoid collisions if prefixInScope is the empty string
            OMNamespace dummyNS = factory.createOMNamespace("__dummy__", "__dummy__");
            OMElement parent = factory.createOMElement("parent", dummyNS);
            element = factory.createOMElement("test", dummyNS, parent);
            if (prefixInScope != null) {
                if (prefixInScope.length() == 0) {
                    parent.declareDefaultNamespace(namespaceURI);
                } else {
                    parent.declareNamespace(namespaceURI, prefixInScope);
                }
            }
        } else {
            element = null;
        }
        node = node(factory, element);
        OMNamespace ns =
                namespaceURI == null ? null : factory.createOMNamespace(namespaceURI, prefix);
        try {
            setNamespace(node, ns);
            if (invalid) {
                fail("Expected IllegalArgumentException");
            }
        } catch (IllegalArgumentException ex) {
            if (invalid) {
                return;
            } else {
                throw ex;
            }
        }
        String expectedPrefix;
        if (this.expectedPrefix == null) {
            expectedPrefix = node.getPrefix();
            assertNotNull(expectedPrefix);
            assertFalse(expectedPrefix.length() == 0);
        } else {
            expectedPrefix = this.expectedPrefix;
            if (expectedPrefix.length() == 0) {
                assertNull(node.getPrefix());
            } else {
                assertEquals(expectedPrefix, node.getPrefix());
            }
        }
        if (namespaceURI == null || namespaceURI.length() == 0) {
            assertNull(node.getNamespace());
        } else {
            OMNamespace actualNS = node.getNamespace();
            assertEquals(expectedPrefix, actualNS.getPrefix());
            assertEquals(namespaceURI, actualNS.getNamespaceURI());
        }
        if (namespaceURI == null || namespaceURI.length() == 0) {
            assertNull(node.getNamespaceURI());
        } else {
            assertEquals(namespaceURI, node.getNamespaceURI());
        }
        QName qname = node.getQName();
        assertEquals(expectedPrefix, qname.getPrefix());
        assertEquals(namespaceURI == null ? "" : namespaceURI, qname.getNamespaceURI());
        if (element != null) {
            Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
            if (expectNSDecl) {
                assertTrue(it.hasNext());
                OMNamespace decl = it.next();
                assertEquals(expectedPrefix, decl.getPrefix());
                assertEquals(namespaceURI == null ? "" : namespaceURI, decl.getNamespaceURI());
            }
            assertFalse(it.hasNext());
        }
    }
}
