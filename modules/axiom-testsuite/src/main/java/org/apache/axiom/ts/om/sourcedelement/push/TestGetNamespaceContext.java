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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link XMLStreamWriter#getNamespaceContext()} gives access to pre-existing namespace
 * bindings (defined by the ancestors of the {@link OMSourcedElement}).
 */
public class TestGetNamespaceContext extends AxiomTestCase {
    public TestGetNamespaceContext(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", factory.createOMNamespace("urn:test", "p"));
        final String[] resultHolder = new String[1];
        OMElement element = factory.createOMElement(new AbstractPushOMDataSource() {
            public void serialize(XMLStreamWriter writer) throws XMLStreamException {
                resultHolder[0] = writer.getNamespaceContext().getNamespaceURI("p");
                writer.writeStartElement(null, "root", null);
                writer.writeEndElement();
            }
            
            public boolean isDestructiveWrite() {
                return false;
            }
        });
        parent.addChild(element);
        // Expand element
        element.getFirstOMChild();
        assertEquals("urn:test", resultHolder[0]);
    }
}
