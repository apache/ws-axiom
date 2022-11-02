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
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Random;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.base64.Base64EncodingStringBufferOutputStream;
import org.apache.commons.io.IOUtils;

public class XMLStreamReaderUtilsTest extends TestCase {
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)}
     * returns an empty {@link Blob} when the element is empty. The test uses
     * an {@link XMLStreamReader} instance that doesn't implement the
     * {@link org.apache.axiom.ext.stax.BlobReader} extension.
     * 
     * @throws Exception
     */
    public void testGetBlobFromElementWithZeroLengthNonBlobReader() throws Exception {
        testGetBlobFromElementWithZeroLength(false);
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)}
     * returns an empty {@link Blob} when the element is empty. The test uses
     * an {@link XMLStreamReader} instance that implements the
     * {@link org.apache.axiom.ext.stax.BlobReader} extension.
     * 
     * @throws Exception
     */
    public void testGetBlobFromElementWithZeroLengthBlobReader() throws Exception {
        testGetBlobFromElementWithZeroLength(true);
    }
    
    private void testGetBlobFromElementWithZeroLength(boolean useBlobReader) throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<test/>"));
        if (useBlobReader) {
            reader = new XMLStreamReaderWithBlobReader(reader);
        }
        try {
            reader.next();
            
            // Check precondition
            assertTrue(reader.isStartElement());
            
            Blob blob = XMLStreamReaderUtils.getBlobFromElement(reader);
            
            // Check postcondition
            assertTrue(reader.isEndElement());
            assertEquals(-1, blob.getInputStream().read());
        } finally {
            reader.close();
        }
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)}
     * throws an exception if the element has unexpected content. The test uses
     * an {@link XMLStreamReader} instance that doesn't implement the
     * {@link org.apache.axiom.ext.stax.BlobReader} extension.
     * 
     * @throws Exception
     */
    public void testGetBlobFromElementWithUnexpectedContentNonBlobReader() throws Exception {
        testGetBlobFromElementWithUnexpectedContent(false);
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)}
     * throws an exception if the element has unexpected content. The test uses
     * an {@link XMLStreamReader} instance that implements the
     * {@link org.apache.axiom.ext.stax.BlobReader} extension.
     * 
     * @throws Exception
     */
    public void testGetBlobFromElementWithUnexpectedContentBlobReader() throws Exception {
        testGetBlobFromElementWithUnexpectedContent(true);
    }
    
    private void testGetBlobFromElementWithUnexpectedContent(boolean useBlobReader) throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<test>\n<child/>\n</test>"));
        if (useBlobReader) {
            reader = new XMLStreamReaderWithBlobReader(reader);
        }
        try {
            reader.next();
            
            // Check precondition
            assertTrue(reader.isStartElement());
            
            try {
                XMLStreamReaderUtils.getBlobFromElement(reader);
                fail("Expected XMLStreamException");
            } catch (XMLStreamException ex) {
                // Expected
            }
        } finally {
            reader.close();
        }
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)}
     * correctly decodes base64 data if the parser is non coalescing and produces the data
     * as multiple {@code CHARACTER} events. The test uses an {@link XMLStreamReader} instance
     * that doesn't implement the {@link org.apache.axiom.ext.stax.BlobReader}
     * extension.
     * 
     * @throws Exception
     */
    public void testgetBlobFromElementNonCoalescingNonBlobReader() throws Exception {
        testgetBlobFromElementNonCoalescing(false);
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)}
     * correctly decodes base64 data if the parser is non coalescing and produces the data
     * as multiple {@code CHARACTER} events. The test uses an {@link XMLStreamReader} instance
     * that implements the {@link org.apache.axiom.ext.stax.BlobReader}
     * extension.
     * 
     * @throws Exception
     */
    public void testgetBlobFromElementNonCoalescingBlobReader() throws Exception {
        testgetBlobFromElementNonCoalescing(true);
    }
    
    private void testgetBlobFromElementNonCoalescing(boolean useBlobReader) throws Exception {
        // We generate base64 that is sufficiently large to force the parser to generate
        // multiple CHARACTER events
        StringBuffer buffer = new StringBuffer("<test>");
        Base64EncodingStringBufferOutputStream out = new Base64EncodingStringBufferOutputStream(buffer);
        byte[] data = new byte[65536];
        new Random().nextBytes(data);
        out.write(data);
        out.complete();
        buffer.append("</test>");
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(StAXParserConfiguration.NON_COALESCING,
                new StringReader(buffer.toString()));
        if (useBlobReader) {
            reader = new XMLStreamReaderWithBlobReader(reader);
        }
        try {
            reader.next();
            
            // Check precondition
            assertTrue(reader.isStartElement());
            
            Blob blob = XMLStreamReaderUtils.getBlobFromElement(reader);
            
            // Check postcondition
            assertTrue(reader.isEndElement());
            assertTrue(Arrays.equals(data, IOUtils.toByteArray(blob.getInputStream())));
        } finally {
            reader.close();
        }
    }
    
    /**
     * Test that {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)} correctly
     * decodes base64 encoded content that contains whitespace. This is a regression test for <a
     * href="https://issues.apache.org/jira/browse/AXIOM-380">AXIOM-380</a>.
     * 
     * @throws Exception
     */
    public void testgetBlobFromElementWithWhitespace() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader(
                "<data>MS4wMToxNDIdMS4wMjowMzAwHTEuMDM6MR8wMx4yHzAwHjQfMDEeNB8wMh0xLjA0OlBOUx0xLjA1\r\n" + 
                "OjIwMTEwODAyHTEuMDY6Mh0xLjA3OkZMRkRMRUNWWh0xLjA4OkZMMDM3ODhXMB0xLjA5OjExMDgw\r\n" + 
                "MjAwMDcdMS4xMToxOS42OR0xLjEyOjE5LjY5HDIuMDAxOjE4HTIuMDAyOjAwHAAA</data>"));
        try {
            reader.next();
            Blob blob = XMLStreamReaderUtils.getBlobFromElement(reader);
            assertEquals("1.01:1421.02:03001.03:1032004014021.04:PNS1.05:201108021.06:21.07:FLFDLECVZ1.08:FL03788W01.09:11080200071.11:19.691.12:19.692.001:182.002:00  ",
                    IOUtils.toString(blob.getInputStream(), "ascii"));
        } finally {
            reader.close();
        }
    }
    
    public void testGetElementTextAsStream() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<a>test</a>"));
        reader.next();
        Reader in = XMLStreamReaderUtils.getElementTextAsStream(reader, false);
        assertEquals("test", IOUtils.toString(in));
        assertEquals(XMLStreamReader.END_ELEMENT, reader.getEventType());
    }
    
    public void testGetElementTextAsStreamWithAllowedNonTextChildren() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<a>xxx<b>yyy</b>zzz</a>"));
        reader.next();
        Reader in = XMLStreamReaderUtils.getElementTextAsStream(reader, true);
        assertEquals("xxxzzz", IOUtils.toString(in));
    }
    
    public void testGetElementTextAsStreamWithForbiddenNonTextChildren() throws Exception {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new StringReader("<a>xxx<b>yyy</b>zzz</a>"));
        reader.next();
        Reader in = XMLStreamReaderUtils.getElementTextAsStream(reader, false);
        try {
            IOUtils.toString(in);
            fail("Expected exception");
        } catch (IOException ex) {
            // Expected
        }
    }
}
