package org.apache.axiom.om.impl.llom;

import junit.framework.TestCase;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class AttributeImplTest extends TestCase {
    public void testQNames() throws Exception {
        String ATTR = "attr";
        String NSURI = "http://ns1";
        OMFactory fac = new OMLinkedListImplFactory();
        OMNamespace ns = fac.createOMNamespace(NSURI, null);
        OMAttribute attr = new OMAttributeImpl(ATTR, ns, "value", fac);
        QName qname = attr.getQName();
        assertEquals("Wrong namespace", NSURI, qname.getNamespaceURI());
        assertEquals("Wrong localPart", ATTR, qname.getLocalPart());
        assertEquals("Wrong prefix", "", qname.getPrefix());

    }
}
