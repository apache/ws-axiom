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

package org.apache.axiom.ts.om.document;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxp.sax.SAXImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class TestGetSAXResultSAXParser extends AxiomTestCase {
    private final SAXImplementation saxImplementation;
    private final XMLSample file;
    
    public TestGetSAXResultSAXParser(OMMetaFactory metaFactory, SAXImplementation saxImplementation, XMLSample file) {
        super(metaFactory);
        this.saxImplementation = saxImplementation;
        this.file = file;
        addTestParameter("parser", saxImplementation.getName());
        addTestParameter("file", file.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        SAXParserFactory factory = saxImplementation.newSAXParserFactory();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        OMDocument document = metaFactory.getOMFactory().createOMDocument();
        ContentHandler handler = document.getSAXResult().getHandler();
        reader.setContentHandler(handler);
        reader.setDTDHandler((DTDHandler)handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        reader.setProperty("http://xml.org/sax/properties/declaration-handler", handler);
        reader.parse(new InputSource(file.getUrl().toString()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.serialize(baos);
        InputSource actual = new InputSource();
        actual.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
        actual.setSystemId(file.getUrl().toString());
        assertAbout(xml())
                .that(actual)
                .ignoringWhitespaceInPrologAndEpilog()
                .expandingEntityReferences()
                .hasSameContentAs(file.getUrl());
    }
}
