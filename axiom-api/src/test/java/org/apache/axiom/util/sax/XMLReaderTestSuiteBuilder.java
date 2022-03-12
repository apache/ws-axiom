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
package org.apache.axiom.util.sax;

import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.xml.sax.XMLReader;

public class XMLReaderTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final XMLReader xmlReader;

    public XMLReaderTestSuiteBuilder(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    @Override
    protected void addTests() {
        addTest(new TestGetSetFeature(xmlReader, "http://xml.org/sax/features/namespaces"));
        addTest(new TestGetSetFeature(xmlReader, "http://xml.org/sax/features/namespace-prefixes"));
        addTest(new TestGetSetFeature(xmlReader, "http://xml.org/sax/features/external-general-entities"));
    }
}
