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
package org.apache.axiom.ts.om.sourcedelement.push;

import java.util.Collections;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.junit.Assert;

/**
 * Scenario that attempts to use {@link XMLStreamWriter#writeStartDocument()}, {@link
 * XMLStreamWriter#writeStartDocument(String)}, {@link XMLStreamWriter#writeStartDocument(String,
 * String)} and {@link XMLStreamWriter#writeEndDocument()}. These methods are disallowed, as
 * described in the documentation of {@link OMDataSource#serialize(XMLStreamWriter)}.
 */
public class WriteStartEndDocumentScenario implements PushOMDataSourceScenario {
    @Override
    public void addTestParameters(MatrixTestCase testCase) {
        testCase.addTestParameter("scenario", "writeStartEndDocument");
    }

    @Override
    public Map<String, String> getNamespaceContext() {
        return Collections.emptyMap();
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        try {
            writer.writeStartDocument();
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }
        try {
            writer.writeStartDocument("1.0");
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }
        try {
            writer.writeStartDocument("UTF-8", "1.0");
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }
        writer.writeEmptyElement(null, "root", null);
        try {
            writer.writeEndDocument();
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }
    }

    @Override
    public void validate(OMElement element, boolean dataHandlersPreserved) throws Throwable {
        // Just fore expansion
        element.getFirstOMChild();
    }
}
