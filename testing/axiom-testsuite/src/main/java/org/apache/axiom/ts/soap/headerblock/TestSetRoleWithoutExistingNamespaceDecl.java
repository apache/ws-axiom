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
import static org.apache.axiom.truth.AxiomTruth.assertThat;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.HeaderBlockAttribute;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Tests that {@link SOAPHeaderBlock#setRole(String)} adds a namespace declaration if necessary. */
public class TestSetRoleWithoutExistingNamespaceDecl extends SOAPTestCase {
    public TestSetRoleWithoutExistingNamespaceDecl(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPHeaderBlock headerBlock =
                soapFactory.createSOAPHeaderBlock(
                        "block", soapFactory.createOMNamespace("urn:test", "p"));
        headerBlock.setRole("urn:testrole");
        OMAttribute roleAttr = headerBlock.getAttribute(HeaderBlockAttribute.ROLE.getQName(spec));
        assertThat(roleAttr).isNotNull();
        assertThat(headerBlock).hasNamespaceDeclaration(roleAttr.getNamespace());
    }
}
