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

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMElement#serializeAndConsume(java.io.Writer)} correctly serializes an object
 * model tree that has been partially built. This is a regression test for AXIOM-151.
 */
public class TestSerializeAndConsumePartiallyBuilt extends AxiomTestCase {
    public TestSerializeAndConsumePartiallyBuilt(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        String xml =
                "<root><child><grandchild1>text</grandchild1></child><child><grandchild2>text</grandchild2></child></root>";

        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(), new StringReader(xml))
                        .getDocumentElement();

        // Partially build the tree
        root.getFirstElement().getFirstElement();

        StringWriter out = new StringWriter();
        root.serializeAndConsume(out);
        assertEquals(xml, out.toString());
    }
}
