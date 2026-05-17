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
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests that {@link OMContainer#getXMLStreamReader(boolean)} produces the correct sequence of
 * events when called on an {@link OMElement} that is not the root element. Also tests that the rest
 * of the document can be built after consuming the {@link XMLStreamReader}.
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-288">AXIOM-288</a>.
 */
public class TestGetXMLStreamReaderOnNonRootElement implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("cache")
    private boolean cache;

    @Override
    public void runTest() throws Throwable {
        OMElement root = AXIOMUtil.stringToOM(factory, "<a><b><c/></b><d>content</d></a>");
        OMElement b = (OMElement) root.getFirstOMChild();
        XMLStreamReader stream = b.getXMLStreamReader(cache);
        assertThat(stream.getEventType()).isEqualTo(XMLStreamReader.START_DOCUMENT);
        assertThat(stream.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("b");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(stream.getLocalName()).isEqualTo("c");
        assertThat(stream.next()).isEqualTo(XMLStreamReader.END_ELEMENT);
        assertThat(stream.next()).isEqualTo(XMLStreamReader.END_ELEMENT);
        assertThat(stream.next()).isEqualTo(XMLStreamReader.END_DOCUMENT);
        OMElement d = (OMElement) b.getNextOMSibling();
        assertThat(d.getText()).isEqualTo("content");
        root.close(false);
    }
}
