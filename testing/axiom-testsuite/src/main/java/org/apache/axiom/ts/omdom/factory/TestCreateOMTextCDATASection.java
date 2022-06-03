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
package org.apache.axiom.ts.omdom.factory;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.CDATASection;

/**
 * Tests that {@link OMFactory#createOMText(String, int)} creates a node that implements {@link
 * CDATASection} if the specified type is {@link OMNode#CDATA_SECTION_NODE}.
 */
public class TestCreateOMTextCDATASection extends AxiomTestCase {
    public TestCreateOMTextCDATASection(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMText text =
                metaFactory
                        .getOMFactory()
                        .createOMText("cdata section content", OMNode.CDATA_SECTION_NODE);
        assertTrue(text instanceof CDATASection);
    }
}
