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
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.junit.Assert;

/** Scenario that uses {@link XMLStreamWriter#writeCharacters(char[], int, int)}. */
public class WriteCharactersCharArrayScenario implements PushOMDataSourceScenario {
    @Override
    public void addTestParameters(MatrixTestCase testCase) {
        testCase.addTestParameter("scenario", "writeCharactersCharArray");
    }

    @Override
    public Map<String, String> getNamespaceContext() {
        return Collections.emptyMap();
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(null, "root", null);
        char[] ch = new char[6];
        "test".getChars(0, 4, ch, 0);
        writer.writeCharacters(ch, 0, 4);
        "case".getChars(0, 4, ch, 2);
        writer.writeCharacters(ch, 2, 4);
        writer.writeEndElement();
    }

    @Override
    public void validate(OMElement element, boolean dataHandlersPreserved) {
        OMNode child = element.getFirstOMChild();
        Assert.assertTrue(child instanceof OMText);
        Assert.assertEquals("test", ((OMText) child).getText());
        child = child.getNextOMSibling();
        Assert.assertTrue(child instanceof OMText);
        Assert.assertEquals("case", ((OMText) child).getText());
        Assert.assertNull(child.getNextOMSibling());
    }
}
