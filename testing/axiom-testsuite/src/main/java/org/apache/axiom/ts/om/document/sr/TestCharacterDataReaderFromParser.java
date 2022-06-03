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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the {@link CharacterDataReader} returned by {@link OMContainer#getXMLStreamReader()}
 * for an OM tree created by a builder correctly implements the {@link DTDReader} extension.
 */
public class TestCharacterDataReaderFromParser extends AxiomTestCase {
    private final boolean cache;

    public TestCharacterDataReaderFromParser(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        String text = "This is a test for the CharacterDataReader extension";
        OMDocument doc =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(),
                                new StringReader("<root>" + text + "</root>"))
                        .getDocument();
        XMLStreamReader reader = doc.getXMLStreamReader(cache);
        CharacterDataReader cdataReader =
                (CharacterDataReader) reader.getProperty(CharacterDataReader.PROPERTY);
        assertNotNull(cdataReader);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        StringWriter sw = new StringWriter();
        while (reader.next() == XMLStreamReader.CHARACTERS) {
            cdataReader.writeTextTo(sw);
        }
        assertEquals(text, sw.toString());
    }
}
