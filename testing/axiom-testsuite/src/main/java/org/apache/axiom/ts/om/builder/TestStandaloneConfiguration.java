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
package org.apache.axiom.ts.om.builder;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMXMLBuilderFactory#createOMBuilder(StAXParserConfiguration,
 * InputStream)} with {@link StAXParserConfiguration#STANDALONE}.
 */
public class TestStandaloneConfiguration extends AxiomTestCase {
    public TestStandaloneConfiguration(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream is = TestStandaloneConfiguration.class.getResourceAsStream("web_w_dtd2.xml");
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(
                        metaFactory.getOMFactory(), StAXParserConfiguration.STANDALONE, is);
        OMElement root = builder.getDocumentElement();
        assertTrue(root.getLocalName().equals("web-app"));
        OMDocument document = builder.getDocument();
        Iterator<OMNode> i = document.getChildren();
        OMDocType docType = null;
        while (docType == null && i.hasNext()) {
            OMNode obj = i.next();
            if (obj instanceof OMDocType) {
                docType = (OMDocType) obj;
            }
        }
        assertTrue(docType != null);
        root.close(false);
    }
}
