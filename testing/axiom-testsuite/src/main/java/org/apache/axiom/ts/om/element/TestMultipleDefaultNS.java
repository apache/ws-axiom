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
package org.apache.axiom.ts.om.element;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

public class TestMultipleDefaultNS extends AxiomTestCase {
    public TestMultipleDefaultNS(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory omFactory = metaFactory.getOMFactory();
        OMNamespace defaultNS1 = omFactory.createOMNamespace("http://defaultNS1.org", null);
        OMNamespace defaultNS2 = omFactory.createOMNamespace("http://defaultNS2.org", null);

        OMElement omElementOne =
                omFactory.createOMElement(
                        "DocumentElement",
                        omFactory.createOMNamespace("http://defaultNS1.org", ""));
        OMElement omElementOneChild = omFactory.createOMElement("ChildOne", null, omElementOne);

        OMElement omElementTwo = omFactory.createOMElement("Foo", defaultNS2, omElementOne);
        omElementTwo.declareDefaultNamespace("http://defaultNS2.org");
        OMElement omElementTwoChild = omFactory.createOMElement("ChildOne", null, omElementTwo);

        OMElement omElementThree = omFactory.createOMElement("Bar", defaultNS1, omElementTwo);
        omElementThree.declareDefaultNamespace("http://defaultNS1.org");

        OMNamespace omElementOneChildNS = omElementOneChild.getNamespace();
        OMNamespace omElementTwoChildNS = omElementTwoChild.getNamespace();
        // TODO: LLOM's and DOOM's behaviors are slightly different here; need to check if both are
        // allowed
        assertTrue(omElementOneChildNS == null || "".equals(omElementOneChildNS.getNamespaceURI()));
        assertTrue(omElementTwoChildNS == null || "".equals(omElementTwoChildNS.getNamespaceURI()));
    }
}
