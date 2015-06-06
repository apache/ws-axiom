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

package org.apache.axiom.om.impl.serialize;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestSuite;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.ts.xml.XMLSample;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectDetector;
import org.xml.sax.InputSource;

public class StreamingOMSerializerTest extends AbstractTestCase {
    private final XMLSample file;

    public StreamingOMSerializerTest(String name, XMLSample file) {
        super(name);
        this.file = file;
    }

    protected void runTest() throws Throwable {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        StAXDialect dialect = StAXDialectDetector.getDialect(inputFactory.getClass());
        inputFactory = dialect.normalize(inputFactory);
        // Allow CDATA events
        inputFactory = dialect.enableCDataReporting(inputFactory);
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        XMLOutputFactory outputFactory = dialect.normalize(XMLOutputFactory.newInstance());
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StreamSource(file.getUrl().toString()));
        String encoding = reader.getEncoding();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out, encoding);
        writer.writeStartDocument(encoding, reader.getVersion());
        serializer.serialize(reader, writer, false);
        writer.writeEndDocument();
        writer.flush();
        InputSource actual = new InputSource();
        actual.setByteStream(new ByteArrayInputStream(out.toByteArray()));
        actual.setSystemId(file.getUrl().toString());
        assertAbout(xml())
                .that(xml(actual))
                .hasSameContentAs(xml(file.getUrl()));
    }

    public static TestSuite suite() throws Exception {
        TestSuite suite = new TestSuite();
        for (Iterator it = Multiton.getInstances(XMLSample.class).iterator(); it.hasNext(); ) {
            XMLSample file = (XMLSample)it.next();
            suite.addTest(new StreamingOMSerializerTest(file.getName(), file));
        }
        return suite;
    }
}
