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
package org.apache.axiom.ts.om.element;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMElement#getXMLStreamReader(boolean,
 * OMXMLStreamReaderConfiguration)} with {@link
 * OMXMLStreamReaderConfiguration#isNamespaceURIInterning()} set to <code>true</code>.
 */
public class TestGetXMLStreamReaderWithNamespaceURIInterning extends AxiomTestCase {
    public TestGetXMLStreamReaderWithNamespaceURIInterning(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        // Use "new String" to create String objects that are not interned
        OMNamespace ns1 = factory.createOMNamespace(new String("urn:ns1"), "p");
        OMNamespace ns2 = factory.createOMNamespace(new String("urn:ns2"), "q");
        OMElement root = factory.createOMElement("root", ns1);
        root.addAttribute("attr", "value", ns2);
        factory.createOMElement("child", ns2, root);

        OMXMLStreamReaderConfiguration configuration = new OMXMLStreamReaderConfiguration();
        configuration.setNamespaceURIInterning(true);
        XMLStreamReader reader = root.getXMLStreamReader(true, configuration);
        reader.nextTag();
        assertInterned(reader.getNamespaceURI());
        assertInterned(reader.getAttributeNamespace(0));
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            assertInterned(reader.getNamespaceURI(i));
        }
        reader.nextTag();
        assertInterned(reader.getNamespaceURI("p"));
        NamespaceContext nc = reader.getNamespaceContext();
        assertInterned(nc.getNamespaceURI("p"));
    }

    private static void assertInterned(String s) {
        assertSame("String not interned", s.intern(), s);
    }
}
