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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.junit.Assert;

/**
 * Tests that {@link XMLStreamWriter#writeNamespace(String, String)} creates the expected namespace
 * declaration.
 */
public class WriteNamespaceScenario implements PushOMDataSourceScenario {
    private final String prefix;
    private final String namespaceURI;

    public WriteNamespaceScenario(String prefix, String namespaceURI) {
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    @Override
    public void addTestParameters(TestParameterTarget testCase) {
        testCase.addTestParameter("scenario", "writeNamespace");
        testCase.addTestParameter("prefix", prefix);
        testCase.addTestParameter("uri", namespaceURI);
    }

    @Override
    public Map<String, String> getNamespaceContext() {
        return Collections.emptyMap();
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("_p_", "root", "urn:__test__");
        writer.writeNamespace("_p_", "urn:__test__");
        writer.writeNamespace(prefix, namespaceURI);
        writer.writeEndElement();
    }

    @Override
    public void validate(OMElement element, boolean blobsPreserved) throws Throwable {
        OMNamespace decl = null;
        Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        while (it.hasNext()) {
            OMNamespace ns = it.next();
            if (!ns.getPrefix().equals("_p_")) {
                if (decl != null) {
                    Assert.fail("Found unexpected namespace declaration");
                } else {
                    decl = ns;
                }
            }
        }
        Assert.assertNotNull(decl);
        Assert.assertEquals(prefix, decl.getPrefix());
        Assert.assertEquals(namespaceURI, decl.getNamespaceURI());
    }
}
