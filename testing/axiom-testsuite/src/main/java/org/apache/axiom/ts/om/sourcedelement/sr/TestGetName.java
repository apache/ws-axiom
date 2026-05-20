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
package org.apache.axiom.ts.om.sourcedelement.sr;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that {@link XMLStreamReader#getName()} returns a {@link QName} with the correct prefix for
 * the {@link XMLStreamConstants#START_ELEMENT} event corresponding to an {@link OMSourcedElement},
 * even if the prefix is not known in advance.
 */
public class TestGetName implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {
        OMElement root = factory.createOMElement("root", null);
        OMSourcedElement el = factory.createOMElement(
                new PullOMDataSource("<p:el xmlns:p='urn:ns'>content</p:el>"),
                "el",
                factory.createOMNamespace("urn:ns", null));
        root.addChild(el);
        XMLStreamReader reader = root.getXMLStreamReader();
        assertThat(reader.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(reader.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        QName name = reader.getName();
        assertThat(name.getPrefix()).isEqualTo("p");
        assertThat(name.getNamespaceURI()).isEqualTo("urn:ns");
        assertThat(name.getLocalPart()).isEqualTo("el");
    }
}
