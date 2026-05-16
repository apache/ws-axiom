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
package org.apache.axiom.ts.om.element.sr;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetNamespaceContext extends AxiomTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("cache")
    private boolean cache;

    @Override
    protected void runTest() throws Throwable {
        OMElement element =
                AXIOMUtil.stringToOM(factory, "<a xmlns='urn:ns1' xmlns:ns2='urn:ns2'><b xmlns:ns3='urn:ns3'/></a>");
        XMLStreamReader stream = cache ? element.getXMLStreamReader() : element.getXMLStreamReaderWithoutCaching();
        stream.next();
        assertThat(stream.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("b");
        NamespaceContext context = stream.getNamespaceContext();
        assertThat(context.getNamespaceURI("")).isEqualTo("urn:ns1");
        assertThat(context.getNamespaceURI("ns2")).isEqualTo("urn:ns2");
        assertThat(context.getNamespaceURI("ns3")).isEqualTo("urn:ns3");
        assertThat(context.getPrefix("urn:ns2")).isEqualTo("ns2");
        element.close(false);
    }
}
