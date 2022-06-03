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
package org.apache.axiom.ts.om.sourcedelement;

import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.sourcedelement.push.PushOMDataSourceScenario;

/**
 * Tests that the {@link SAXSource} returned by {@link OMContainer#getSAXSource(boolean)} correctly
 * serializes an {@link OMSourcedElement} backed by an {@link AbstractPushOMDataSource}.
 */
public class TestGetSAXSourceWithPushOMDataSource extends AxiomTestCase {
    private final PushOMDataSourceScenario scenario;
    private boolean serializeParent;

    public TestGetSAXSourceWithPushOMDataSource(
            OMMetaFactory metaFactory, PushOMDataSourceScenario scenario, boolean serializeParent) {
        super(metaFactory);
        this.scenario = scenario;
        this.serializeParent = serializeParent;
        scenario.addTestParameters(this);
        addTestParameter("serializeParent", serializeParent);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement sourcedElement =
                factory.createOMElement(
                        new AbstractPushOMDataSource() {
                            @Override
                            public void serialize(XMLStreamWriter writer)
                                    throws XMLStreamException {
                                scenario.serialize(writer);
                            }

                            @Override
                            public boolean isDestructiveWrite() {
                                return false;
                            }
                        });
        Iterator<Map.Entry<String, String>> it =
                scenario.getNamespaceContext().entrySet().iterator();
        OMElement parent;
        if (it.hasNext()) {
            Map.Entry<String, String> binding = it.next();
            parent =
                    factory.createOMElement(
                            "parent",
                            factory.createOMNamespace(binding.getValue(), binding.getKey()));
            while (it.hasNext()) {
                binding = it.next();
                parent.declareNamespace(
                        factory.createOMNamespace(binding.getValue(), binding.getKey()));
            }
        } else {
            parent = factory.createOMElement("parent", null);
        }
        parent.addChild(sourcedElement);
        SAXSource saxSource = (serializeParent ? parent : sourcedElement).getSAXSource(true);
        OMElement element =
                OMXMLBuilderFactory.createOMBuilder(factory, saxSource, false).getDocumentElement();
        if (serializeParent) {
            element = element.getFirstElement();
        }
        scenario.validate(element, false);
    }
}
