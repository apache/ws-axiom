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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.om.sourcedelement.push.PushOMDataSourceScenario;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the expansion of an {@link OMSourcedElement} backed by an {@link AbstractPushOMDataSource}.
 */
public class TestPushOMDataSourceExpansion implements Executable {
    @Inject
    private OMFactory factory;

    class PushOMDataSource extends AbstractPushOMDataSource {
        private boolean expanded;

        @Override
        public void serialize(XMLStreamWriter writer) throws XMLStreamException {
            scenario.serialize(writer);
            expanded = true;
        }

        @Override
        public boolean isDestructiveWrite() {
            return false;
        }

        boolean isExpanded() {
            return expanded;
        }
    }

    @Inject
    private PushOMDataSourceScenario scenario;

    @Override
    public void execute() throws Throwable {
        PushOMDataSource ds = new PushOMDataSource();
        OMElement element = factory.createOMElement(ds);
        Iterator<Map.Entry<String, String>> it =
                scenario.getNamespaceContext().entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<String, String> binding = it.next();
            OMElement parent =
                    factory.createOMElement("parent", factory.createOMNamespace(binding.getValue(), binding.getKey()));
            while (it.hasNext()) {
                binding = it.next();
                parent.declareNamespace(factory.createOMNamespace(binding.getValue(), binding.getKey()));
            }
            parent.addChild(element);
        }
        scenario.validate(element, true);
        assertThat(ds.isExpanded()).isTrue();
    }
}
