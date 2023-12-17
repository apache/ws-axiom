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
package org.apache.axiom.ts.dom.document;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;

/**
 * Tests that the {@link DOMConfiguration} object returned by {@link Document#getDomConfig()} is
 * initialized with the correct default values as defined by the DOM specification.
 */
public class TestGetDomConfigDefaults extends DOMTestCase {
    public TestGetDomConfigDefaults(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        DOMConfiguration domConfig = document.getDomConfig();
        assertThat(domConfig.getParameter("canonical-form")).isEqualTo(Boolean.FALSE);
        assertThat(domConfig.getParameter("cdata-sections")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("check-character-normalization"))
                .isEqualTo(Boolean.FALSE);
        assertThat(domConfig.getParameter("comments")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("datatype-normalization")).isEqualTo(Boolean.FALSE);
        assertThat(domConfig.getParameter("element-content-whitespace")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("entities")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("namespaces")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("namespace-declarations")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("normalize-characters")).isEqualTo(Boolean.FALSE);
        assertThat(domConfig.getParameter("split-cdata-sections")).isEqualTo(Boolean.TRUE);
        assertThat(domConfig.getParameter("validate")).isEqualTo(Boolean.FALSE);
        assertThat(domConfig.getParameter("validate-if-schema")).isEqualTo(Boolean.FALSE);
        assertThat(domConfig.getParameter("well-formed")).isEqualTo(Boolean.TRUE);
    }
}
