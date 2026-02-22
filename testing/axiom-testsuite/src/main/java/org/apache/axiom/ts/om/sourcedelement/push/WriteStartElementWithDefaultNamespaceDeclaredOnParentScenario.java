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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.junit.Assert;

/**
 * Tests that {@link XMLStreamWriter#writeStartElement(String, String, String)} creates the correct
 * {@link OMElement} if the element uses a default namespace declared on the parent.
 */
public class WriteStartElementWithDefaultNamespaceDeclaredOnParentScenario
        implements PushOMDataSourceScenario {
    @Override
    public void addTestParameters(TestParameterTarget testCase) {
        testCase.addTestParameter(
                "scenario", "writeStartElementWithDefaultNamespaceDeclaredOnParent");
    }

    @Override
    public Map<String, String> getNamespaceContext() {
        return Collections.emptyMap();
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("", "root", "urn:test");
        writer.writeDefaultNamespace("urn:test");
        writer.setDefaultNamespace("urn:test");
        writer.writeStartElement("", "child", "urn:test");
        writer.writeCharacters("test");
        writer.writeEndElement();
        writer.writeEndElement();
    }

    @Override
    public void validate(OMElement element, boolean blobsPreserved) throws Throwable {
        OMElement child = element.getFirstElement();
        Assert.assertNull(child.getPrefix());
        Assert.assertEquals("urn:test", child.getNamespaceURI());
        Assert.assertEquals("child", child.getLocalName());
        Assert.assertFalse(child.getAllDeclaredNamespaces().hasNext());
    }
}
