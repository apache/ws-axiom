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
package org.apache.axiom.ts.om.document.sr;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the {@link XMLStreamReader} returned by {@link OMContainer#getXMLStreamReader()} for a
 * programmatically created OM tree correctly implements the {@link DTDReader} extension.
 */
public class TestDTDReader extends AxiomTestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMDocument document = factory.createOMDocument();
        factory.createOMDocType(document, "root", "-//MY//DTD", "my.dtd", "<!ELEMENT root (#PCDATA)>");
        factory.createOMElement("root", null, document);
        XMLStreamReader reader = document.getXMLStreamReader();
        // Note that according to the specification of the DTDReader interface, it is
        // allowed to look up the extension before reaching the DTD event.
        DTDReader dtdReader = (DTDReader) reader.getProperty(DTDReader.PROPERTY);
        assertThat(dtdReader).isNotNull();
        assertThat(reader.next()).isEqualTo(XMLStreamReader.DTD);
        assertThat(dtdReader.getRootName()).isEqualTo("root");
        assertThat(dtdReader.getPublicId()).isEqualTo("-//MY//DTD");
        assertThat(dtdReader.getSystemId()).isEqualTo("my.dtd");
        assertThat(reader.getText()).isEqualTo("<!ELEMENT root (#PCDATA)>");
    }
}
