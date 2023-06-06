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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.io.InstrumentedInputStream;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link XMLStreamReader#close()} on the {@link XMLStreamReader} returned by
 * a call {@link OMContainer#getXMLStreamReaderWithoutCaching()} on an {@link OMDocument} created by
 * a builder. In that case, closing the reader is expected to close the builder without parsing the
 * rest of the document.
 */
public class TestCloseWithoutCaching extends AxiomTestCase {
    public TestCloseWithoutCaching(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        writer.write("<root><a>");
        for (int i = 0; i < 20000; i++) {
            writer.write('a');
        }
        writer.write("</a></root>");
        writer.close();
        InstrumentedInputStream in =
                new InstrumentedInputStream(new ByteArrayInputStream(baos.toByteArray()));
        OMDocument doc =
                OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in).getDocument();
        XMLStreamReader reader = doc.getXMLStreamReaderWithoutCaching();
        reader.next();
        reader.next();
        long count = in.getCount();
        reader.close();
        assertEquals(count, in.getCount());
    }
}
