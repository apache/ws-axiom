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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Iterator;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.BooleanAttributeAccessor;
import org.apache.axiom.ts.soap.HeaderBlockAttribute;
import org.apache.axiom.ts.soap.SOAPSpec;

public class TestSetBooleanAttribute extends BooleanAttributeTestCase {
    @Inject
    private SOAPFactory soapFactory;

    private final boolean value;

    @Inject
    public TestSetBooleanAttribute(SOAPSpec spec, HeaderBlockAttribute attribute, @Named("value") boolean value) {
        super(spec, attribute);
        this.value = value;
    }

    @Override
    public void runTest() throws Throwable {
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        SOAPHeaderBlock soapHeaderBlock = soapFactory.createSOAPHeaderBlock("testHeaderBlock", namespace, soapHeader);
        BooleanAttributeAccessor accessor = attribute.getAdapter(BooleanAttributeAccessor.class);
        accessor.setValue(soapHeaderBlock, value);
        assertThat(accessor.getValue(soapHeaderBlock)).isEqualTo(value);
        Iterator<OMAttribute> it = soapHeaderBlock.getAllAttributes();
        assertThat(it.hasNext()).isTrue();
        OMAttribute att = it.next();
        OMNamespace ns = att.getNamespace();
        assertThat(ns.getNamespaceURI()).isEqualTo(spec.getEnvelopeNamespaceURI());
        assertThat(att.getLocalName()).isEqualTo(attribute.getName(spec));
        assertThat(att.getAttributeValue()).isEqualTo(spec.getCanonicalRepresentation(value));
        assertThat(it.hasNext()).isFalse();
    }
}
