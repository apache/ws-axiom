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

package org.apache.axiom.om;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class OMAttributeTestBase extends TestCase {
    private final OMImplementation omImplementation;

    public OMAttributeTestBase(OMImplementation omImplementation) {
        this.omImplementation = omImplementation;
    }
    
    /**
     * Make sure getQName() works correctly on an OMAttribute implementation.
     * @throws Exception
     */
    public void testQNames() throws Exception {
        String ATTR = "attr";
        String NSURI = "http://ns1";
        OMFactory fac = omImplementation.getOMFactory();
        OMNamespace ns = fac.createOMNamespace(NSURI, null);
        OMAttribute attr = fac.createOMAttribute(ATTR, ns, "value");
        QName qname = attr.getQName();
        assertEquals("Wrong namespace", NSURI, qname.getNamespaceURI());
        assertEquals("Wrong localPart", ATTR, qname.getLocalPart());
        assertEquals("Wrong prefix", "", qname.getPrefix());
    }
}
