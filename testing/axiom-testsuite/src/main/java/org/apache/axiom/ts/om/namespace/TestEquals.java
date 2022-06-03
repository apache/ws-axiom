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
package org.apache.axiom.ts.om.namespace;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMNamespace#equals(String, String)} for an {@link OMNamespace}
 * instance with non null prefix.
 */
public class TestEquals extends AxiomTestCase {
    public TestEquals(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");
        assertTrue(ns.equals("http://www.w3.org/XML/1998/namespace", "xml"));
        // The implementation must not assume that namespace URI or prefixes are interned
        assertTrue(
                ns.equals(new String("http://www.w3.org/XML/1998/namespace"), new String("xml")));
        assertFalse(ns.equals("http://www.w3.org/XML/1998/namespace", "xml2"));
        assertFalse(ns.equals("http://www.w3.org/XML/1998/namespace", null));
        assertFalse(ns.equals("http://www.w3.org/XML/2001/namespace", "xml"));
        assertFalse(ns.equals(null, "xml"));
    }
}
