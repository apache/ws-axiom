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
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.junit.Assert;

/**
 * Scenario that lets {@link XMLStreamWriter#writeAttribute(String, String, String)} automatically
 * select an appropriate prefix.
 */
public class WriteAttributeAutoPrefixScenario implements PushOMDataSourceScenario {
    @Override
    public void addTestParameters(TestParameterTarget testCase) {
        testCase.addTestParameter("scenario", "writeAttributeAutoPrefix");
    }

    @Override
    public Map<String, String> getNamespaceContext() {
        return Collections.emptyMap();
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(null, "root", null);
        writer.writeNamespace("p", "urn:test");
        writer.setPrefix("p", "urn:test");
        writer.writeAttribute("urn:test", "attr", "value");
        writer.writeEndElement();
    }

    @Override
    public void validate(OMElement element, boolean blobsPreserved) throws Throwable {
        Iterator<OMAttribute> it = element.getAllAttributes();
        Assert.assertTrue(it.hasNext());
        OMAttribute attr = it.next();
        Assert.assertEquals("p", attr.getPrefix());
        Assert.assertEquals("urn:test", attr.getNamespaceURI());
        Assert.assertEquals("attr", attr.getLocalName());
        Assert.assertEquals("value", attr.getAttributeValue());
    }
}
