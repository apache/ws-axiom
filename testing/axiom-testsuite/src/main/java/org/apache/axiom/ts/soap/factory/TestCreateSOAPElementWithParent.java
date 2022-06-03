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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.ts.soap.SOAPElementType;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests {@link SOAPFactory#createSOAPHeader(SOAPEnvelope)}, {@link
 * SOAPFactory#createSOAPBody(SOAPEnvelope)}, {@link SOAPFactory#createSOAPFault(SOAPBody)}, {@link
 * SOAPFactory#createSOAPFaultCode(SOAPFault)}, {@link
 * SOAPFactory#createSOAPFaultValue(SOAPFaultCode)}, {@link
 * SOAPFactory#createSOAPFaultValue(SOAPFaultSubCode)}, {@link
 * SOAPFactory#createSOAPFaultSubCode(SOAPFaultCode)}, {@link
 * SOAPFactory#createSOAPFaultSubCode(SOAPFaultSubCode)}, {@link
 * SOAPFactory#createSOAPFaultReason(SOAPFault)}, {@link
 * SOAPFactory#createSOAPFaultText(SOAPFaultReason)}, {@link
 * SOAPFactory#createSOAPFaultNode(SOAPFault)}, {@link SOAPFactory#createSOAPFaultRole(SOAPFault)}
 * and {@link SOAPFactory#createSOAPFaultDetail(SOAPFault)} with a non null parent.
 */
public class TestCreateSOAPElementWithParent extends SOAPTestCase {
    private final SOAPElementType type;
    private final SOAPElementType parentType;

    public TestCreateSOAPElementWithParent(
            OMMetaFactory metaFactory,
            SOAPSpec spec,
            SOAPElementType type,
            SOAPElementType parentType) {
        super(metaFactory, spec);
        this.type = type;
        this.parentType = parentType;
        addTestParameter(
                "type", type.getAdapter(SOAPElementTypeAdapter.class).getType().getSimpleName());
        addTestParameter(
                "parentType",
                parentType.getAdapter(SOAPElementTypeAdapter.class).getType().getSimpleName());
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement parent = parentType.getAdapter(SOAPElementTypeAdapter.class).create(soapFactory);
        QName expectedName = type.getQName(spec);
        if (expectedName == null) {
            try {
                type.getAdapter(SOAPElementTypeAdapter.class)
                        .create(soapFactory, parentType, parent);
                fail("Expect UnsupportedOperationException");
            } catch (UnsupportedOperationException ex) {
                // Expected
            }
        } else {
            String expectedPrefix =
                    expectedName.getNamespaceURI().length() == 0
                            ? ""
                            : SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX;
            OMElement child =
                    type.getAdapter(SOAPElementTypeAdapter.class)
                            .create(soapFactory, parentType, parent);
            assertTrue(child.isComplete());
            QName actualName = child.getQName();
            assertEquals(expectedName, actualName);
            assertEquals(expectedPrefix, actualName.getPrefix());
            assertSame(parent, child.getParent());
            assertSame(child, parent.getFirstOMChild());
            assertNull(child.getNextOMSibling());
            // All relevant namespaces have already been declared on the parent
            assertFalse(child.getAllDeclaredNamespaces().hasNext());
            assertFalse(child.getAllAttributes().hasNext());
            assertNull(child.getFirstOMChild());
        }
    }
}
