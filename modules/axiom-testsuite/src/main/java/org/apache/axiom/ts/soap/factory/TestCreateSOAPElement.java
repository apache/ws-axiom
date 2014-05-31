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
package org.apache.axiom.ts.soap.factory;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests {@link SOAPFactory#createSOAPEnvelope()}, {@link SOAPFactory#createSOAPFaultCode()},
 * {@link SOAPFactory#createSOAPFaultValue()}, {@link SOAPFactory#createSOAPFaultSubCode()},
 * {@link SOAPFactory#createSOAPFaultReason()}, {@link SOAPFactory#createSOAPFaultText()},
 * {@link SOAPFactory#createSOAPFaultRole()} and {@link SOAPFactory#createSOAPFaultDetail()}.
 */
public class TestCreateSOAPElement extends SOAPTestCase {
    private final SOAPElementType type;
    
    public TestCreateSOAPElement(OMMetaFactory metaFactory, SOAPSpec spec, SOAPElementType type) {
        super(metaFactory, spec);
        this.type = type;
        type.addTestParameters(this);
    }

    protected void runTest() throws Throwable {
        QName expectedName = type.getQName(spec);
        if (expectedName == null) {
            try {
                type.create(soapFactory);
                fail("Expect UnsupportedOperationException");
            } catch (UnsupportedOperationException ex) {
                // Expected
            }
        } else {
            String expectedPrefix = expectedName.getNamespaceURI().length() == 0 ? "" : SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX; 
            OMElement child = type.create(soapFactory);
            QName actualName = child.getQName();
            assertEquals(expectedName, actualName);
            assertEquals(expectedPrefix, actualName.getPrefix());
            assertNull(child.getParent());
            Iterator it = child.getAllDeclaredNamespaces();
            if (expectedPrefix.length() != 0) {
                assertTrue(it.hasNext());
                OMNamespace ns = (OMNamespace)it.next();
                assertEquals(expectedName.getNamespaceURI(), ns.getNamespaceURI());
                assertEquals(expectedPrefix, ns.getPrefix());
            }
            assertFalse(it.hasNext());
            assertFalse(child.getAllAttributes().hasNext());
            assertNull(child.getFirstOMChild());
        }
    }
}
