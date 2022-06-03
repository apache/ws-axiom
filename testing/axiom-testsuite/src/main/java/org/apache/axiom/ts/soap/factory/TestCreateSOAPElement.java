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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.ts.soap.SOAPElementType;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter;
import org.apache.axiom.ts.soap.SOAPSpec;

/**
 * Tests {@link SOAPFactory#createSOAPEnvelope()}, {@link SOAPFactory#createSOAPHeader()}, {@link
 * SOAPFactory#createSOAPBody()}, {@link SOAPFactory#createSOAPFault()}, {@link
 * SOAPFactory#createSOAPFaultCode()}, {@link SOAPFactory#createSOAPFaultValue()}, {@link
 * SOAPFactory#createSOAPFaultSubCode()}, {@link SOAPFactory#createSOAPFaultReason()}, {@link
 * SOAPFactory#createSOAPFaultText()}, {@link SOAPFactory#createSOAPFaultNode()}, {@link
 * SOAPFactory#createSOAPFaultRole()} and {@link SOAPFactory#createSOAPFaultDetail()}.
 */
public class TestCreateSOAPElement extends CreateSOAPElementWithoutParentTestCase {
    public TestCreateSOAPElement(OMMetaFactory metaFactory, SOAPSpec spec, SOAPElementType type) {
        super(metaFactory, spec, type);
    }

    @Override
    protected OMElement createSOAPElement() {
        return type.getAdapter(SOAPElementTypeAdapter.class).create(soapFactory);
    }
}
