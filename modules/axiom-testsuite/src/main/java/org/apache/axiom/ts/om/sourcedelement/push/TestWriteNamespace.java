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

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link XMLStreamWriter#writeNamespace(String, String)} creates the expected namespace
 * declaration.
 */
public class TestWriteNamespace extends AxiomTestCase {
    private final String prefix;
    private final String namespaceURI;
    
    public TestWriteNamespace(OMMetaFactory metaFactory, String prefix, String namespaceURI) {
        super(metaFactory);
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement(new AbstractPushOMDataSource() {
            public void serialize(XMLStreamWriter writer) throws XMLStreamException {
                writer.writeStartElement("_p_", "root", "urn:__test__");
                writer.writeNamespace("_p_", "urn:test");
                writer.writeNamespace(prefix, namespaceURI);
                writer.writeEndElement();
            }
            
            public boolean isDestructiveWrite() {
                return false;
            }
        });
        OMNamespace decl = null;
        Iterator it = element.getAllDeclaredNamespaces();
        while (it.hasNext()) {
            OMNamespace ns = (OMNamespace)it.next();
            if (!ns.getPrefix().equals("_p_")) {
                if (decl != null) {
                    fail("Found unexpected namespace declaration");
                } else {
                    decl = ns;
                }
            }
        }
        assertNotNull(decl);
        assertEquals(prefix, decl.getPrefix());
        assertEquals(namespaceURI, decl.getNamespaceURI());
    }
}
