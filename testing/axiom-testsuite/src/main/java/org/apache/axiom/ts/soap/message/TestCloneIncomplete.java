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
package org.apache.axiom.ts.soap.message;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.ts.soap.SOAPSampleAdapter;
import org.apache.axiom.ts.soap.SOAPSampleSet;

/**
 * Tests that {@link OMInformationItem#clone(OMCloneOptions)} correctly clones a {@link SOAPMessage}
 * that is incomplete. In particular, the test checks that {@link OMInformationItem#getOMFactory()}
 * returns the correct factory (which is determined lazily) on the clone.
 */
public class TestCloneIncomplete extends SOAPTestCase {
    private final boolean preserveModel;

    public TestCloneIncomplete(OMMetaFactory metaFactory, SOAPSpec spec, boolean preserveModel) {
        super(metaFactory, spec);
        this.preserveModel = preserveModel;
        addTestParameter("preserveModel", preserveModel);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPMessage message =
                SOAPSampleSet.WSA
                        .getMessage(spec)
                        .getAdapter(SOAPSampleAdapter.class)
                        .getSOAPMessage(metaFactory);
        OMCloneOptions options = new OMCloneOptions();
        options.setPreserveModel(preserveModel);
        OMInformationItem clone = message.clone(options);
        if (preserveModel) {
            assertTrue(clone instanceof SOAPMessage);
            assertSame(soapFactory, clone.getOMFactory());
        } else {
            assertTrue(clone instanceof OMDocument);
            assertFalse(clone instanceof SOAPMessage);
        }
    }
}
