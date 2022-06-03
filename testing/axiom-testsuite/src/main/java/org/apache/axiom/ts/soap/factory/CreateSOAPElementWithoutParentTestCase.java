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
import org.apache.axiom.ts.soap.SOAPElementType;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public abstract class CreateSOAPElementWithoutParentTestCase extends SOAPTestCase {
    protected final SOAPElementType type;

    public CreateSOAPElementWithoutParentTestCase(
            OMMetaFactory metaFactory, SOAPSpec spec, SOAPElementType type) {
        super(metaFactory, spec);
        this.type = type;
        type.getAdapter(SOAPElementTypeAdapter.class).addTestParameters(this);
    }

    @Override
    protected final void runTest() throws Throwable {
        QName expectedName = type.getQName(spec);
        if (expectedName == null) {
            try {
                createSOAPElement();
                fail("Expect UnsupportedOperationException");
            } catch (UnsupportedOperationException ex) {
                // Expected
            }
        } else {
            String expectedPrefix =
                    expectedName.getNamespaceURI().length() == 0
                            ? ""
                            : SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX;
            OMElement element = createSOAPElement();
            assertTrue(element.isComplete());
            QName actualName = element.getQName();
            assertEquals(expectedName, actualName);
            assertEquals(expectedPrefix, actualName.getPrefix());
            assertNull(element.getParent());
            Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
            if (expectedPrefix.length() != 0) {
                assertTrue(it.hasNext());
                OMNamespace ns = it.next();
                assertEquals(expectedName.getNamespaceURI(), ns.getNamespaceURI());
                assertEquals(expectedPrefix, ns.getPrefix());
            }
            assertFalse(it.hasNext());
            assertFalse(element.getAllAttributes().hasNext());
            assertNull(element.getFirstOMChild());
        }
    }

    protected abstract OMElement createSOAPElement();
}
