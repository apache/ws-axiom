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

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.HeaderBlockAttribute;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests that setting a SOAP header block attribute uses the correct namespace prefix from the
 * attribute's QName, not the attribute's local name.
 *
 * <p>This is a regression test for a bug in {@code AxiomElementMixin._setAttributeValue()} where
 * {@code qname.getLocalPart()} was incorrectly used as the namespace prefix instead of
 * {@code qname.getPrefix()}. This produced redundant namespace declarations where the attribute
 * name was used as its own namespace prefix (e.g. {@code xmlns:role="..."} and
 * {@code role:role="..."} instead of reusing the SOAP envelope prefix).
 */
public class TestSetAttributeNamespacePrefix extends SOAPTestCase {
    private final HeaderBlockAttribute attribute;

    public TestSetAttributeNamespacePrefix(
            OMMetaFactory metaFactory, SOAPSpec spec, HeaderBlockAttribute attribute) {
        super(metaFactory, spec);
        this.attribute = attribute;
        addTestParameter("attribute", attribute.getName(spec));
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeaderBlock headerBlock = soapFactory.createSOAPHeaderBlock(
                "block", soapFactory.createOMNamespace("urn:test", "p"));

        // Set the attribute using the appropriate setter
        if (attribute == HeaderBlockAttribute.MUST_UNDERSTAND) {
            headerBlock.setMustUnderstand(true);
        } else if (attribute == HeaderBlockAttribute.ROLE) {
            headerBlock.setRole("urn:testrole");
        } else if (attribute == HeaderBlockAttribute.RELAY) {
            headerBlock.setRelay(true);
        }

        String attrName = attribute.getName(spec);
        OMAttribute attr = headerBlock.getAttribute(attribute.getQName(spec));
        assertThat(attr).isNotNull();

        OMNamespace attrNs = attr.getNamespace();
        assertThat(attrNs).isNotNull();
        assertThat(attrNs.getPrefix()).isNotEmpty();
        // The namespace prefix must not be the attribute's own local name.
        // That would indicate the prefix was derived from getLocalPart() instead of getPrefix().
        assertThat(attrNs.getPrefix()).isNotEqualTo(attrName);
    }
}
