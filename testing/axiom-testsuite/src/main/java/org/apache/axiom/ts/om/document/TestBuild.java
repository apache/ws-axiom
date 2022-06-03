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
package org.apache.axiom.ts.om.document;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.io.InstrumentedInputStream;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.xml.XMLSample;

public class TestBuild extends AxiomTestCase {
    public TestBuild(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        InstrumentedInputStream in = new InstrumentedInputStream(XMLSample.LARGE.getInputStream());
        OMDocument doc =
                OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in).getDocument();
        assertFalse(doc.isComplete());
        long countBeforeBuild = in.getCount();
        doc.build();
        assertTrue(doc.isComplete());
        long countAfterBuild = in.getCount();
        assertTrue(countAfterBuild > countBeforeBuild);
        OMNode node = doc.getFirstOMChild();
        while (node != null) {
            node = node.getNextOMSibling();
        }
        assertEquals(countAfterBuild, in.getCount());
    }
}
