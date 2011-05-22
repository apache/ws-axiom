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
package org.apache.axiom.attachments;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.testutils.io.IOTestUtils;

public class AttachmentsTest extends AbstractTestCase {
    String img2FileName = "mtom/img/test2.jpg";
    
    public void testGetDataHandler() throws Exception {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        DataHandler dh = attachments
                .getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
        InputStream dataIs = dh.getDataSource().getInputStream();

        InputStream expectedDataIs = getTestResource(img2FileName);

        // Compare data across streams
        IOTestUtils.compareStreams(dataIs, expectedDataIs);
    }

    public void testGetDataHandlerNonExistingMIMEPart() throws Exception {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        DataHandler dh = attachments.getDataHandler("ThisShouldReturnNull");
        assertNull(dh);
    }

    public void testGetAllContentIDs() throws Exception {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        String[] contentIDs = attachments.getAllContentIDs();
        assertEquals(contentIDs.length, 3);
        assertEquals(contentIDs[0], "0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org");
        assertEquals(contentIDs[1], "1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");
        assertEquals(contentIDs[2], "2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");

        Set idSet = attachments.getContentIDSet();
        assertTrue(idSet.contains("0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org"));
        assertTrue(idSet.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
        assertTrue(idSet.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
        
        // Make sure the length is correct
        long length = attachments.getContentLength();
        long fileSize = IOUtils.getStreamAsByteArray(getTestResource(TestConstants.MTOM_MESSAGE)).length;
        assertTrue("Expected MessageContent Length of " + fileSize + " but received " + length,
                   length == fileSize);
    }

    private void testGetSOAPPartContentID(String contentTypeStartParam, String contentId)
            throws Exception {
        // It doesn't actually matter what the stream *is* it just needs to exist
        String contentType = "multipart/related; boundary=\"" + TestConstants.MTOM_MESSAGE_BOUNDARY +
                "\"; type=\"text/xml\"; start=\"" + contentTypeStartParam + "\"";
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, contentType);
        assertEquals("Did not obtain correct content ID", contentId,
                attachments.getSOAPPartContentID());
    }
    
    public void testGetSOAPPartContentIDWithoutBrackets() throws Exception {
        testGetSOAPPartContentID("my-content-id@localhost", "my-content-id@localhost");
    }
    
    public void testGetSOAPPartContentIDWithBrackets() throws Exception {
        testGetSOAPPartContentID("<my-content-id@localhost>", "my-content-id@localhost");
    }
    
    // Not sure when exactly somebody uses the "cid:" prefix in the start parameter, but
    // this is how the code currently works.
    public void testGetSOAPPartContentIDWithCidPrefix() throws Exception {
        testGetSOAPPartContentID("cid:my-content-id@localhost", "my-content-id@localhost");
    }
    
    // Regression test for WSCOMMONS-329
    public void testGetSOAPPartContentIDWithCidPrefix2() throws Exception {
        testGetSOAPPartContentID("<cid-73920@192.168.0.1>", "cid-73920@192.168.0.1");
    }
    
    public void testGetSOAPPartContentIDShort() throws Exception {
        testGetSOAPPartContentID("bbb", "bbb");
    }
    
    public void testGetSOAPPartContentIDShortWithBrackets() throws Exception {
        testGetSOAPPartContentID("<b>", "b");
    }
    
    public void testGetSOAPPartContentIDBorderline() throws Exception {
        testGetSOAPPartContentID("cid:", "cid:");
    }
    
    /**
     * Tests that {@link Attachments#getSOAPPartContentType()} throws a meaningful exception if it
     * is unable to determine the content type.
     */
    public void testGetSOAPPartContentTypeWithContentIDMismatch() {
        String contentType = "multipart/related; boundary=\"" + TestConstants.MTOM_MESSAGE_BOUNDARY +
                "\"; type=\"text/xml\"; start=\"<wrong-content-id@example.org>\"";
        Attachments attachments = new Attachments(getTestResource(TestConstants.MTOM_MESSAGE), contentType);
        try {
            attachments.getSOAPPartContentType();
            fail("Expected OMException");
        } catch (OMException ex) {
            // OK, expected
        } catch (Throwable ex) {
            fail("Unexpected exception: " + ex.getClass().getName());
        }
    }
    
    public void testGetIncomingAttachmentStreams() throws Exception {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        // Get the inputstream container
        IncomingAttachmentStreams ias = attachments.getIncomingAttachmentStreams();

        IncomingAttachmentInputStream dataIs;

        // Img1 stream
        dataIs = ias.getNextStream();

        // Make sure it was the first attachment
        assertEquals("<1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org>",
                     dataIs.getContentId());

        // Consume the stream
        while (dataIs.read() != -1) ;

        // Img2 stream
        dataIs = ias.getNextStream();
        assertEquals("<2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org>",
                     dataIs.getContentId());

        // Test if getContentType() works..
        assertEquals("image/jpeg", dataIs.getContentType());

        // Test if a adding/getting a header works
        dataIs.addHeader("new-header", "test-value");
        assertEquals("test-value", dataIs.getHeader("new-header"));
    }
    
    public void testRemoveDataHandlerWithStream() throws Exception {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        Collection list = attachments.getContentIDSet();
        assertEquals(3, list.size());
        
        assertTrue(list.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
        assertTrue(list.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
        
        attachments.removeDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");

        List list2 = attachments.getContentIDList();
        assertEquals(2, list2.size());
        assertEquals(2, attachments.getMap().size());

        assertFalse(list2.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
        assertTrue(list2.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
    }
}
