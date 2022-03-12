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
        assertEquals(Boolean.FALSE, domConfig.getParameter("canonical-form"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("cdata-sections"));
        assertEquals(Boolean.FALSE, domConfig.getParameter("check-character-normalization"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("comments"));
        assertEquals(Boolean.FALSE, domConfig.getParameter("datatype-normalization"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("element-content-whitespace"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("entities"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("namespaces"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("namespace-declarations"));
        assertEquals(Boolean.FALSE, domConfig.getParameter("normalize-characters"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("split-cdata-sections"));
        assertEquals(Boolean.FALSE, domConfig.getParameter("validate"));
        assertEquals(Boolean.FALSE, domConfig.getParameter("validate-if-schema"));
        assertEquals(Boolean.TRUE, domConfig.getParameter("well-formed"));
    }
}
