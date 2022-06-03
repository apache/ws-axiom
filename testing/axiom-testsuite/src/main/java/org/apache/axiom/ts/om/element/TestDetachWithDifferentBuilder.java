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

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

public class TestDetachWithDifferentBuilder extends AxiomTestCase {
    public TestDetachWithDifferentBuilder(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        String xml1 = "<root><a/><b/></root>";
        String xml2 = "<child>test</child>";
        OMElement parent =
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader(xml1))
                        .getDocumentElement();
        OMElement child =
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader(xml2))
                        .getDocumentElement(true);
        parent.getFirstOMChild().insertSiblingBefore(child);
        // Detaching the child should not build it because its parent is built by a different
        // builder.
        child.detach();
        assertThat(child.isComplete()).isFalse();
        assertAbout(xml()).that(xml(OMElement.class, parent)).hasSameContentAs(xml1);
        assertAbout(xml()).that(xml(OMElement.class, child)).hasSameContentAs(xml2);
    }
}
