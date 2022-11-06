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
package org.apache.axiom.ts.om.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.ByteStreamComparator;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.axiom.util.base64.Base64DecodingOutputStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Tests that when an {@link OMText} node is serialized by the {@link SAXSource} returned by {@link
 * OMContainer#getSAXSource(boolean)}, the implementation doesn't construct an in-memory base64
 * representation of the complete binary content, but writes it in chunks (streaming).
 *
 * <p>Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-442">AXIOM-442</a>.
 */
public class TestBase64StreamingWithGetSAXSource extends AxiomTestCase {
    private static class Base64Comparator extends DefaultHandler {
        private final Writer out;

        public Base64Comparator(InputStream expected) {
            out = new Base64DecodingOutputStreamWriter(new ByteStreamComparator(expected));
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            try {
                out.write(ch, start, length);
            } catch (IOException ex) {
                throw new SAXException(ex);
            }
        }

        @Override
        public void endDocument() throws SAXException {
            try {
                out.close();
            } catch (IOException ex) {
                throw new SAXException(ex);
            }
        }
    }

    public TestBase64StreamingWithGetSAXSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement elem = factory.createOMElement("test", null);
        // Create a data source that would eat up all memory when loaded. If the test
        // doesn't fail with an OutOfMemoryError, we know that the OMText implementation
        // supports streaming.
        DataSource ds = new RandomDataSource(654321L, Runtime.getRuntime().maxMemory());
        OMText text = factory.createOMText(DataHandlerUtils.toBlob(new DataHandler(ds)), false);
        elem.addChild(text);
        SAXSource saxSource = elem.getSAXSource(true);
        XMLReader xmlReader = saxSource.getXMLReader();
        xmlReader.setContentHandler(new Base64Comparator(ds.getInputStream()));
        xmlReader.parse(saxSource.getInputSource());
    }
}
