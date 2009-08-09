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

package org.apache.axiom.util.stax.dialect;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.apache.axiom.om.util.StAXUtils;

public class StAXDialectTest extends TestCase {
    public void testCreateXMLStreamWriterWithNullEncoding() {
        // This should cause an exception
        try {
            StAXUtils.createXMLStreamWriter(System.out, null);
        } catch (Throwable ex) {
            // Expected
            return;
        }
        // Attention here: since the fail method works by throwing an exception and we
        // catch Throwable, it must be invoked outside of the catch block!
        fail("Expected createXMLStreamWriter to throw an exception");
    }
    
    public void testWriteStartDocumentWithNullEncoding() throws Exception {
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(System.out, "UTF-8");
        try {
            writer.writeStartDocument(null, "1.0");
        } catch (Throwable ex) {
            // Expected
            return;
        }
        fail("Expected writeStartDocument to throw an exception");
    }
    
    public void testGetEncoding() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new ByteArrayInputStream(
                "<?xml version='1.0' encoding='iso-8859-15'?><root/>".getBytes("iso-8859-15")));
        assertEquals("iso-8859-15", reader.getEncoding());
        reader.next();
        try {
            reader.getEncoding();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // Expected
        }
        reader.close();
    }

    public void testGetVersion() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(
                new StringReader("<?xml version='1.0'?><root/>"));
        assertEquals("1.0", reader.getVersion());
        reader.next();
        try {
            reader.getVersion();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // Expected
        }
        reader.close();
    }

    public void testIsStandalone() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(
                new StringReader("<?xml version='1.0' standalone='no'?><root/>"));
        assertEquals(false, reader.isStandalone());
        reader.next();
        try {
            reader.isStandalone();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // Expected
        }
        reader.close();
    }

    public void testStandaloneSet() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(
                new StringReader("<?xml version='1.0'?><root/>"));
        assertEquals(false, reader.standaloneSet());
        reader.next();
        try {
            reader.standaloneSet();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // Expected
        }
        reader.close();
    }

    public void testGetCharacterEncodingScheme() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new ByteArrayInputStream(
                "<?xml version='1.0' encoding='iso-8859-15'?><root/>".getBytes("iso-8859-15")));
        assertEquals("iso-8859-15", reader.getCharacterEncodingScheme());
        reader.next();
        try {
            reader.getCharacterEncodingScheme();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            // Expected
        }
        reader.close();
    }
    
    public void testDisallowDoctypeDeclWithExternalSubset() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
        factory = dialect.disallowDoctypeDecl(dialect.normalize(factory));
        DummyHTTPServer server = new DummyHTTPServer();
        server.start();
        try {
            boolean gotException = false;
            boolean reachedDocumentElement = false;
            try {
                XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(
                        "<?xml version='1.0'?><!DOCTYPE root SYSTEM '" + server.getBaseURL() +
                        "dummy.dtd'><root/>"));
                try {
                    while (reader.hasNext()) {
                        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                            reachedDocumentElement = true;
                        }
                    }
                } finally {
                    reader.close();
                }
            } catch (XMLStreamException ex) {
                gotException = true;
            } catch (RuntimeException ex) {
                gotException = true;
            }
            assertTrue("Expected exception", gotException);
            assertFalse("The parser tried to load external DTD subset", server.isRequestReceived());
            assertFalse("The parser failed to throw an exception before reaching the document element", reachedDocumentElement);
        } finally {
            server.stop();
        }
    }
    
    public void testDisallowDoctypeDeclWithInternalSubset() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
        factory = dialect.disallowDoctypeDecl(dialect.normalize(factory));
        boolean gotException = false;
        boolean reachedDocumentElement = false;
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(
                    "<?xml version='1.0'?><!DOCTYPE root []><root/>"));
            try {
                while (reader.hasNext()) {
                    if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                        reachedDocumentElement = true;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException ex) {
            gotException = true;
        } catch (RuntimeException ex) {
            gotException = true;
        }
        assertTrue("Expected exception", gotException);
        assertFalse("The parser failed to throw an exception before reaching the document element", reachedDocumentElement);
    }
    
    public void testDisallowDoctypeDeclWithDenialOfService() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
        factory = dialect.disallowDoctypeDecl(dialect.normalize(factory));
        InputStream in = StAXDialectTest.class.getResourceAsStream("doctype_dos.xml");
        try {
            boolean gotException = false;
            boolean reachedDocumentElement = false;
            try {
                XMLStreamReader reader = factory.createXMLStreamReader(in);
                try {
                    while (reader.hasNext()) {
                        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                            reachedDocumentElement = true;
                        }
                    }
                } finally {
                    reader.close();
                }
            } catch (XMLStreamException ex) {
                gotException = true;
            } catch (RuntimeException ex) {
                gotException = true;
            }
            assertTrue("Expected exception", gotException);
            assertFalse("The parser failed to throw an exception before reaching the document element", reachedDocumentElement);
        } finally {
            in.close();
        }
    }
}
