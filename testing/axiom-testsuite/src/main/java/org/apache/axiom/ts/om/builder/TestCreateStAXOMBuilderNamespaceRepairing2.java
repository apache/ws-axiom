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

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMMetaFactory#createStAXOMBuilder(XMLStreamReader)} correctly performs
 * namespace repairing in cases where a namespace declaration of the form {@code xmlns=""} is
 * required. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-408">AXIOM-408</a>.
 */
public class TestCreateStAXOMBuilderNamespaceRepairing2 extends AxiomTestCase {
    public TestCreateStAXOMBuilderNamespaceRepairing2(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        XMLStreamReader reader =
                StAXUtils.createXMLStreamReader(
                        new StringReader(
                                "<test xmlns='urn:test'><arg0 xmlns=''>dGVzdA==</arg0></test>"));
        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(
                                metaFactory.getOMFactory(), new NamespaceDeclarationFilter(reader))
                        .getDocumentElement();

        Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        assertTrue(it.hasNext());
        OMNamespace ns = it.next();
        assertEquals("", ns.getPrefix());
        assertEquals("urn:test", ns.getNamespaceURI());
        assertFalse(it.hasNext());

        OMElement child = element.getFirstElement();
        it = child.getAllDeclaredNamespaces();
        assertTrue(it.hasNext());
        ns = it.next();
        assertEquals("", ns.getPrefix());
        assertEquals("", ns.getNamespaceURI());
        assertFalse(it.hasNext());
    }
}
