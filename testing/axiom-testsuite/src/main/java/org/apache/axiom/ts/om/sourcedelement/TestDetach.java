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

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.truth.xml.XMLTruth.xml;
import static org.apache.axiom.ts.dimension.ExpansionStrategy.DONT_EXPAND;
import static org.apache.axiom.ts.dimension.ExpansionStrategy.PARTIAL;

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.ExpansionStrategy;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

public class TestDetach extends AxiomTestCase {
    private final ExpansionStrategy expansionStrategy;

    public TestDetach(OMMetaFactory metaFactory, ExpansionStrategy expansionStrategy) {
        super(metaFactory);
        this.expansionStrategy = expansionStrategy;
        expansionStrategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        String xml1 = "<root><a/><b/></root>";
        String xml2 = "<child><c/><d/></child>";
        OMElement parent =
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader(xml1))
                        .getDocumentElement();
        PullOMDataSource ds = new PullOMDataSource(xml2);
        OMSourcedElement omse = factory.createOMElement(ds, "child", null);
        parent.getFirstOMChild().insertSiblingBefore(omse);
        expansionStrategy.apply(omse);
        omse.detach();
        assertThat(ds.hasUnclosedReaders()).isEqualTo(expansionStrategy == PARTIAL);
        assertThat(ds.getReaderRequestCount()).isEqualTo(expansionStrategy == DONT_EXPAND ? 0 : 1);
        assertAbout(xml()).that(xml(OMElement.class, parent)).hasSameContentAs(xml1);
        assertAbout(xml()).that(xml(OMElement.class, omse)).hasSameContentAs(xml2);
    }
}
