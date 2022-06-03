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
package org.apache.axiom.ts.om.sourcedelement;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/** Make sure that the incomplete setting of an OMSE is not propogated to the root */
public class TestComplete extends AxiomTestCase {
    public TestComplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        // Build a root element and child OMSE
        OMFactory f = metaFactory.getOMFactory();
        OMNamespace ns = f.createOMNamespace("http://www.sosnoski.com/uwjws/library", "");
        OMNamespace rootNS = f.createOMNamespace("http://sampleroot", "rootPrefix");
        OMElement child =
                f.createOMElement(
                        new PullOMDataSource(TestDocument.DOCUMENT1.getContent()), "library", ns);
        OMElement root = f.createOMElement("root", rootNS);

        // Trigger expansion of the child OMSE
        // This will cause the child to be partially parsed (i.e. incomplete)
        child.getFirstOMChild();

        // Add the child OMSE to the root.
        root.addChild(child);

        // Normally adding an incomplete child to a parent will
        // cause the parent to be marked as incomplete.
        // But OMSE's are self-contained...therefore the root
        // should still be complete
        assertTrue(!child.isComplete());
        assertTrue(root.isComplete());

        // Now repeat the test, but this time trigger the
        // partial parsing of the child after adding it to the root.
        child =
                f.createOMElement(
                        new PullOMDataSource(TestDocument.DOCUMENT1.getContent()), "library", ns);
        root = f.createOMElement("root", rootNS);

        root.addChild(child);
        child.getFirstOMChild(); // causes partial parsing...i.e. incomplete child

        assertTrue(!child.isComplete());
        assertTrue(root.isComplete());
    }
}
