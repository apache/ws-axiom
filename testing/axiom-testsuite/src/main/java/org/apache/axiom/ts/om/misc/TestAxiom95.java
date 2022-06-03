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
package org.apache.axiom.ts.om.misc;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;

/** Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-95">AXIOM-95</a>. */
public class TestAxiom95 extends AxiomTestCase {
    public TestAxiom95(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(
                        metaFactory.getOMFactory(),
                        TestAxiom95.class.getResourceAsStream("defaultNamespace2.xml"));

        String serializedXML = builder.getDocumentElement().toString();

        //        System.out.println("serializedXML = " + serializedXML);

        assertTrue(serializedXML.indexOf("xmlns=\"\"") == -1);
    }
}
