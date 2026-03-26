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
    /**
     * Common test parameters for {@link SetNamespaceTestCase} subclasses.
     *
     * @see OMNamedInformationItem#setNamespace(OMNamespace, boolean)
     */
    public interface Params {
        /** The namespace URI to set or {@code null} to set a {@code null} {@link OMNamespace}. */
        String namespaceURI();

        /** The prefix to set or {@code null} to generate a prefix. */
        String prefix();

        /**
         * The prefix of an existing namespace declaration in scope for the given namespace URI, or
         * {@code null} if no matching namespace declaration is in scope.
         */
        String prefixInScope();

        /**
         * Flag indicating whether the namespace is invalid and {@link
         * OMNamedInformationItem#setNamespace(OMNamespace, boolean)} is expected to throw an {@link
         * IllegalArgumentException}.
         */
        boolean invalid();

        /**
         * The expected prefix of the attribute after the invocation of {@link
         * OMNamedInformationItem#setNamespace(OMNamespace, boolean)}, or null if the method is
         * expected to generate a prefix.
         */
        String expectedPrefix();

        /**
         * Indicates whether {@link OMNamedInformationItem#setNamespace(OMNamespace, boolean)} is
         * expected to generate a namespace declaration on the owner element.
         */
        boolean expectNSDecl();
    }

    private final Params params;

    /**
     * Constructor.
     *
     * @param metaFactory the meta factory
     * @param params the test parameters
     */
    public SetNamespaceTestCase(OMMetaFactory metaFactory, Params params) {
        super(metaFactory);
        this.params = params;
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
            if (params.prefixInScope() != null) {
                if (params.prefixInScope().length() == 0) {
                    parent.declareDefaultNamespace(params.namespaceURI());
                } else {
                    parent.declareNamespace(params.namespaceURI(), params.prefixInScope());
                }
            }
        } else {
            element = null;
        }
        node = node(factory, element);
        OMNamespace ns =
                params.namespaceURI() == null
                        ? null
                        : factory.createOMNamespace(params.namespaceURI(), params.prefix());
        try {
            setNamespace(node, ns);
            if (params.invalid()) {
                fail("Expected IllegalArgumentException");
            }
        } catch (IllegalArgumentException ex) {
            if (params.invalid()) {
                return;
            } else {
                throw ex;
            }
        }
        String expectedPrefix;
        if (params.expectedPrefix() == null) {
            expectedPrefix = node.getPrefix();
            assertNotNull(expectedPrefix);
            assertFalse(expectedPrefix.length() == 0);
        } else {
            expectedPrefix = params.expectedPrefix();
            if (expectedPrefix.length() == 0) {
                assertNull(node.getPrefix());
            } else {
                assertEquals(expectedPrefix, node.getPrefix());
            }
        }
        if (params.namespaceURI() == null || params.namespaceURI().length() == 0) {
            assertNull(node.getNamespace());
        } else {
            OMNamespace actualNS = node.getNamespace();
            assertEquals(expectedPrefix, actualNS.getPrefix());
            assertEquals(params.namespaceURI(), actualNS.getNamespaceURI());
        }
        if (params.namespaceURI() == null || params.namespaceURI().length() == 0) {
            assertNull(node.getNamespaceURI());
        } else {
            assertEquals(params.namespaceURI(), node.getNamespaceURI());
        }
        QName qname = node.getQName();
        assertEquals(expectedPrefix, qname.getPrefix());
        assertEquals(
                params.namespaceURI() == null ? "" : params.namespaceURI(),
                qname.getNamespaceURI());
        if (element != null) {
            Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
            if (params.expectNSDecl()) {
                assertTrue(it.hasNext());
                OMNamespace decl = it.next();
                assertEquals(expectedPrefix, decl.getPrefix());
                assertEquals(
                        params.namespaceURI() == null ? "" : params.namespaceURI(),
                        decl.getNamespaceURI());
            }
            assertFalse(it.hasNext());
        }
    }
}
