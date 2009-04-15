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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestSuite;

import org.apache.axiom.om.AbstractTestCase;
import org.xml.sax.InputSource;

public class StreamingOMSerializerTest extends AbstractTestCase {
    private final String file;

    public StreamingOMSerializerTest(String name, String file) {
        super(name);
        this.file = file;
    }

    protected void runTest() throws Throwable {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // Allow CDATA events
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
        writer.writeStartDocument();
        serializer.serialize(inputFactory.createXMLStreamReader(getTestResource(file)), writer, false);
        writer.writeEndDocument();
        assertXMLIdentical(compareXML(toDocumentWithoutDTD(getTestResource(file)),
                toDocumentWithoutDTD(new ByteArrayInputStream(out.toByteArray()))), true);
    }

    public static TestSuite suite() throws Exception {
        TestSuite suite = new TestSuite();
        String[] files = getConformanceTestFiles();
        for (int i=0; i<files.length; i++) {
            String file = files[i];
            int idx = file.lastIndexOf('/');
            String name = file.substring(idx+1);
            suite.addTest(new StreamingOMSerializerTest(name, file));
        }
        return suite;
    }
}
