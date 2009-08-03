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

package org.apache.axiom.om.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

public class StAXUtilsTest extends TestCase {
    public interface Action {
        void execute() throws Exception;
    }
    
    private void testThreadSafety(final Action action) throws Throwable {
        int threadCount = 10;
        final List results = new ArrayList(threadCount);
        for (int i=0; i<threadCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    Throwable result;
                    try {
                        for (int i=0; i<1000; i++) {
                            action.execute();
                        }
                        result = null;
                    } catch (Throwable ex) {
                        result = ex;
                    }
                    synchronized (results) {
                        results.add(result);
                        results.notifyAll();
                    }
                }
            }).start();
        }
        synchronized (results) {
            while (results.size() < threadCount) {
                results.wait();
            }
        }
        for (Iterator it = results.iterator(); it.hasNext(); ) {
            Throwable result = (Throwable)it.next();
            if (result != null) {
                throw result;
            }
        }
    }
    
    // Regression test for WSCOMMONS-489
    public void testCreateXMLStreamReaderIsThreadSafe() throws Throwable {
        testThreadSafety(new Action() {
            public void execute() throws Exception {
                String text = String.valueOf((int)(Math.random() * 10000));
                String xml = "<root>" + text + "</root>";
                XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader(xml));
                assertEquals(XMLStreamReader.START_DOCUMENT, reader.getEventType());
                assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
                assertEquals(XMLStreamReader.CHARACTERS, reader.next());
                assertEquals(text, reader.getText());
                assertEquals(XMLStreamReader.END_ELEMENT, reader.next());
                assertEquals(XMLStreamReader.END_DOCUMENT, reader.next());
                reader.close();
            }
        });
    }
    
    // Regression test for WSCOMMONS-489
    public void testCreateXMLStreamWriterIsThreadSafe() throws Throwable {
        testThreadSafety(new Action() {
            public void execute() throws Exception {
                String text = String.valueOf((int)(Math.random() * 10000));
                StringWriter out = new StringWriter();
                XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
                writer.writeStartElement("root");
                writer.writeCharacters(text);
                writer.writeEndElement();
                writer.writeEndDocument();
                writer.flush();
                writer.close();
                assertEquals("<root>" + text + "</root>", out.toString());
            }
        });
    }
    
    public void testCreateXMLStreamWriterWithNullEncoding() throws Exception {
        // This should not cause a NullPointerException
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(System.out, null);
        writer.writeEmptyElement("root");
        writer.close();
    }
}
