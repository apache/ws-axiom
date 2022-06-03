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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.xml.XOPSample;

public class TestCreateOMBuilderXOP extends AxiomTestCase {
    private final XOPSample sample;
    private final boolean build;

    public TestCreateOMBuilderXOP(OMMetaFactory metaFactory, XOPSample sample, boolean build) {
        super(metaFactory);
        this.sample = sample;
        addTestParameter("file", sample.getName());
        this.build = build;
        addTestParameter("build", build);
    }

    @Override
    protected void runTest() throws Throwable {
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(sample.getInputStream())
                        .setContentType(sample.getContentType())
                        .build();
        OMElement content =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(), StAXParserConfiguration.DEFAULT, mb)
                        .getDocumentElement();
        if (build) {
            content.build();
        }
        assertAbout(xml())
                .that(xml(OMElement.class, content))
                .hasSameContentAs(sample.getInlinedMessage());
    }
}
