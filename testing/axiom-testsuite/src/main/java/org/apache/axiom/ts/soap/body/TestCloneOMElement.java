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
package org.apache.axiom.ts.soap.body;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

public class TestCloneOMElement extends SampleBasedSOAPTestCase {
    public TestCloneOMElement(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec, SOAPSampleSet.WSA);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        SOAPBody body = envelope.getBody();

        OMElement firstClonedBodyElement = body.cloneOMElement();
        OMElement secondClonedBodyElement = body.cloneOMElement();

        // cloneOMElement creates plain OMElements
        assertFalse(firstClonedBodyElement instanceof SOAPBody);
        assertFalse(secondClonedBodyElement instanceof SOAPBody);

        // first check whether both have the same information
        assertAbout(xml())
                .that(xml(OMElement.class, firstClonedBodyElement))
                .ignoringNamespaceDeclarations()
                .hasSameContentAs(xml(OMElement.class, body));
        assertAbout(xml())
                .that(xml(OMElement.class, secondClonedBodyElement))
                .ignoringNamespaceDeclarations()
                .hasSameContentAs(xml(OMElement.class, body));
        assertAbout(xml())
                .that(xml(OMElement.class, secondClonedBodyElement))
                .hasSameContentAs(xml(OMElement.class, firstClonedBodyElement));

        // The clone is expected to be orphaned
        assertNull(firstClonedBodyElement.getParent());
        assertNull(secondClonedBodyElement.getParent());

        envelope.close(false);
    }
}
