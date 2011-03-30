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
package org.apache.axiom.ts.soap.headerblock;

import java.util.Iterator;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestSetMustUnderstandBoolean extends SOAPTestCase {
    private final boolean value;
    private final String stringValue;
    
    public TestSetMustUnderstandBoolean(OMMetaFactory metaFactory, SOAPSpec spec, boolean value, String stringValue) {
        super(metaFactory, spec);
        addTestProperty("value", Boolean.toString(value));
        this.value = value;
        this.stringValue = stringValue;
    }

    protected void runTest() throws Throwable {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        soapHeaderBlock.setMustUnderstand(value);
        assertEquals("getMustUnderstand return value", value, soapHeaderBlock.getMustUnderstand());
        Iterator it = soapHeaderBlock.getAllAttributes();
        assertTrue(it.hasNext());
        OMAttribute att = (OMAttribute)it.next();
        OMNamespace ns = att.getNamespace();
        assertEquals(spec.getEnvelopeNamespaceURI(), ns.getNamespaceURI());
        assertEquals(SOAPConstants.ATTR_MUSTUNDERSTAND, att.getLocalName());
        assertEquals(stringValue, att.getAttributeValue());
        assertFalse(it.hasNext());
    }
}
