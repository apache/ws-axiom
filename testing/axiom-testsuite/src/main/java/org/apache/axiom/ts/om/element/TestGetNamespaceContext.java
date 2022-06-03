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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests {@link OMElement#getNamespaceContext(boolean)}. */
public class TestGetNamespaceContext extends AxiomTestCase {
    private final boolean detached;

    public TestGetNamespaceContext(OMMetaFactory metaFactory, boolean detached) {
        super(metaFactory);
        this.detached = detached;
        addTestParameter("detached", detached);
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream in = TestGetNamespaceContext.class.getResourceAsStream("namespacecontext.xml");
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in)
                        .getDocumentElement();
        OMElement inner = root.getFirstElement().getFirstElement();
        NamespaceContext context = inner.getNamespaceContext(detached);
        assertEquals("urn:test2", context.getNamespaceURI("p"));
        assertEquals("urn:test3", context.getNamespaceURI("q"));
        assertEquals("urn:test3", context.getNamespaceURI("r"));
        assertEquals("urn:test4", context.getNamespaceURI(""));
        assertEquals("", context.getNamespaceURI("unbound"));

        assertNull(context.getPrefix("urn:test1"));
        assertEquals("p", context.getPrefix("urn:test2"));
        String prefix = context.getPrefix("urn:test3");
        assertTrue(prefix.equals("q") || prefix.equals("r"));
        assertEquals("", context.getPrefix("urn:test4"));
        assertNull(context.getPrefix("unbound"));

        Iterator<?> it = context.getPrefixes("urn:test1");
        assertFalse(it.hasNext());

        it = context.getPrefixes("urn:test2");
        assertTrue(it.hasNext());
        assertEquals("p", it.next());
        assertFalse(it.hasNext());

        it = context.getPrefixes("urn:test3");
        Set<String> prefixes = new HashSet<>();
        while (it.hasNext()) {
            prefixes.add((String) it.next());
        }
        assertEquals(2, prefixes.size());
        assertTrue(prefixes.contains("q"));
        assertTrue(prefixes.contains("r"));
    }
}
