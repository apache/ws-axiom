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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the {@link XMLStreamReader} returned by {@link OMContainer#getXMLStreamReader()} for a
 * programmatically created OM tree correctly implements the {@link DTDReader} extension.
 */
public class TestDTDReader extends AxiomTestCase {
    public TestDTDReader(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDocument document = factory.createOMDocument();
        factory.createOMDocType(
                document, "root", "-//MY//DTD", "my.dtd", "<!ELEMENT root (#PCDATA)>");
        factory.createOMElement("root", null, document);
        XMLStreamReader reader = document.getXMLStreamReader();
        // Note that according to the specification of the DTDReader interface, it is
        // allowed to look up the extension before reaching the DTD event.
        DTDReader dtdReader = (DTDReader) reader.getProperty(DTDReader.PROPERTY);
        assertNotNull(dtdReader);
        assertEquals(XMLStreamReader.DTD, reader.next());
        assertEquals("root", dtdReader.getRootName());
        assertEquals("-//MY//DTD", dtdReader.getPublicId());
        assertEquals("my.dtd", dtdReader.getSystemId());
        assertEquals("<!ELEMENT root (#PCDATA)>", reader.getText());
    }
}
