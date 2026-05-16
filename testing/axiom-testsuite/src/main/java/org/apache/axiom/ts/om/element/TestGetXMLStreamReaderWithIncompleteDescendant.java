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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.StringReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the sequence of events produced by the {@link XMLStreamReader} returned by {@link
 * OMContainer#getXMLStreamReader(boolean)} is correct for a programmatically created {@link
 * OMElement} that has an incomplete descendant (produced by {@link
 * OMXMLParserWrapper#getDocumentElement(boolean)} with <code>discardDocument</code> set to true).
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-431">AXIOM-431</a>.
 */
public class TestGetXMLStreamReaderWithIncompleteDescendant extends AxiomTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("cache")
    private boolean cache;

    @Override
    protected void runTest() throws Throwable {
        OMElement root = factory.createOMElement(new QName("root"));
        OMElement child = OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<a>test</a>"))
                .getDocumentElement(true);
        root.addChild(child);
        assertThat(child.isComplete()).isFalse();
        XMLStreamReader stream = root.getXMLStreamReader(cache);
        assertThat(stream.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("root");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("a");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.CHARACTERS);
        assertThat(stream.getText()).isEqualTo("test");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.END_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("a");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.END_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("root");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.END_DOCUMENT);
        assertThat(child.isComplete()).isEqualTo(cache);
    }
}
