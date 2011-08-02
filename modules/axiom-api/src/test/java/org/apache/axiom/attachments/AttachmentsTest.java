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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.io.IOUtils;

public class AttachmentsTest extends AbstractTestCase {
    String img1FileName = "mtom/img/test.jpg";
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

    public void testGetDataHandlerNonExistingMIMEPartWithoutStream() throws Exception {
        Attachments attachments = new Attachments();
        attachments.addDataHandler("id@apache.org", new DataHandler("test", "text/plain"));
        assertNull(attachments.getDataHandler("non-existing@apache.org"));
    }

    public void testGetAllContentIDs() throws Exception {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        String[] contentIDs = attachments.getAllContentIDs();
        assertEquals(3, contentIDs.length);
        assertEquals("0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org", contentIDs[0]);
        assertEquals("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org", contentIDs[1]);
        assertEquals("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org", contentIDs[2]);
    }
    
    public void testGetAllContentIDsWithoutStream() {
        Attachments attachments = new Attachments();
        // The choice of content IDs here makes sure that we test that the attachments are returned
        // in the order in which they have been added (instead of sorted by content ID as in
        // earlier Axiom versions)
        attachments.addDataHandler("contentB@apache.org", new DataHandler("content1", "text/plain"));
        attachments.addDataHandler("contentA@apache.org", new DataHandler("content2", "text/plain"));
        
        String[] contentIDs = attachments.getAllContentIDs();
        assertEquals(2, contentIDs.length);
        assertEquals("contentB@apache.org", contentIDs[0]);
        assertEquals("contentA@apache.org", contentIDs[1]);
    }
    
    public void testGetContentIDSet() {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        Set idSet = attachments.getContentIDSet();
        assertTrue(idSet.contains("0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org"));
        assertTrue(idSet.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
        assertTrue(idSet.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
    }
    
    public void testGetContentIDSetWithoutStream() {
        Attachments attachments = new Attachments();
        attachments.addDataHandler("id1@apache.org", new DataHandler("content1", "text/plain"));
        attachments.addDataHandler("id2@apache.org", new DataHandler("content2", "text/plain"));
        attachments.addDataHandler("id3@apache.org", new DataHandler("content3", "text/plain"));
        
        Set idSet = attachments.getContentIDSet();
        assertTrue(idSet.contains("id1@apache.org"));
        assertTrue(idSet.contains("id2@apache.org"));
        assertTrue(idSet.contains("id3@apache.org"));
    }
    
    public void testGetContentLength() throws IOException {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);
        
        // Make sure the length is correct
        long length = attachments.getContentLength();
        long fileSize = IOUtils.toByteArray(getTestResource(TestConstants.MTOM_MESSAGE)).length;
        assertEquals("Return value of getContentLength()", fileSize, length);
    }
    
    /**
     * Tests that {@link Attachments#getContentLength()} returns <code>-1</code> if the object is
     * not stream backed.
     * 
     * @throws IOException
     */
    public void testGetContentLengthWithoutStream() throws IOException {
        Attachments attachments = new Attachments();
        attachments.addDataHandler(UIDGenerator.generateContentId(), new DataHandler("test", "text/plain"));
        assertEquals(-1, attachments.getContentLength());
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
    
    public void testGetIncomingAttachmentStreams2() throws Exception {

        IncomingAttachmentInputStream dataIs;
        InputStream expectedDataIs;

        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);

        // Since SOAP part operated independently of other streams, access it
        // directly, and then get to the streams. If this sequence throws an
        // error, something is wrong with the stream handling code.
        InputStream is = attachments.getSOAPPartInputStream();
        while (is.read() != -1) ;

        // Get the inputstream container
        IncomingAttachmentStreams ias = attachments.getIncomingAttachmentStreams();

        dataIs = ias.getNextStream();
        expectedDataIs = getTestResource(img1FileName);
        IOTestUtils.compareStreams(dataIs, expectedDataIs);

        dataIs = ias.getNextStream();
        expectedDataIs = getTestResource(img2FileName);
        IOTestUtils.compareStreams(dataIs, expectedDataIs);

        // Confirm that no more streams are left
        assertNull(ias.getNextStream());
        
        // After all is done, we should *still* be able to access and
        // re-consume the SOAP part stream, as it should be cached.. can we?
        is = attachments.getSOAPPartInputStream();
        while (is.read() != -1) ;  
    }
    
    public void testSimultaneousStreamAccess() throws Exception {
        InputStream inStream;
        Attachments attachments;
    
        inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);
    
        attachments.getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
    
        // This should throw an error
        try {
            attachments.getIncomingAttachmentStreams();
            fail("No exception caught when attempting to access datahandler and stream at the same time");
        } catch (IllegalStateException ise) {
            // Nothing
        }
    
        inStream.close();
    
        // Try the other way around.
        inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);
    
        attachments.getIncomingAttachmentStreams();
    
        // These should NOT throw error even though they are using part based access
        try {
            String contentType = attachments.getSOAPPartContentType();
            assertTrue(contentType.indexOf("application/xop+xml;") >=0);
            assertTrue(contentType.indexOf("charset=UTF-8;") >=0);
            assertTrue(contentType.indexOf("type=\"application/soap+xml\";") >=0);
        } catch (IllegalStateException ise) {
            fail("No exception expected when requesting SOAP part data");
            ise.printStackTrace();
        }
    
        try {
            attachments.getSOAPPartInputStream();
        } catch (IllegalStateException ise) {
            fail("No exception expected when requesting SOAP part data");
        }
    
        // These should throw an error
        try {
            attachments.getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
            fail("No exception caught when attempting to access stream and datahandler at the same time");
        } catch (IllegalStateException ise) {
            // Nothing
        }
    
        // Additionally, we also need to ensure mutual exclusion if someone
        // tries to access part data directly
    
        try {
            attachments.getAllContentIDs();
            fail("No exception caught when attempting to access stream and contentids list at the same time");
        } catch (IllegalStateException ise) {
            // Nothing
        }
    
        try {
            attachments.getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
            fail("No exception caught when attempting to access stream and part at the same time");
        } catch (IllegalStateException ise) {
            // Nothing
        }
    }

    public void testRemoveDataHandlerAfterParsing() {
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

    /**
     * Tests the behavior of {@link Attachments#removeDataHandler(String)} for a MIME part that has
     * not yet been processed.
     */
    public void testRemoveDataHandlerBeforeParsing() {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);
        attachments.removeDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");
        Set idSet = attachments.getContentIDSet();
        assertEquals(2, idSet.size());
        assertTrue(idSet.contains("0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org"));
        assertFalse(idSet.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
        assertTrue(idSet.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
    }
    
    /**
     * Tests that {@link Attachments#removeDataHandler(String)} returns silently if the message
     * doesn't contain a MIME part with the specified content ID.
     */
    public void testRemoveDataHandlerNonExistingWithStream() {
        InputStream inStream = getTestResource(TestConstants.MTOM_MESSAGE);
        Attachments attachments = new Attachments(inStream, TestConstants.MTOM_MESSAGE_CONTENT_TYPE);
        attachments.removeDataHandler("non-existing@apache.org");
        assertEquals(3, attachments.getContentIDSet().size());
    }

    /**
     * Tests that {@link Attachments#removeDataHandler(String)} returns silently if no data handler
     * with the given content ID has been added to the object.
     */
    public void testRemoveDataHandlerNonExistingWithoutStream() {
        Attachments attachments = new Attachments();
        attachments.addDataHandler("id@apache.org", new DataHandler("test", "text/plain"));
        attachments.removeDataHandler("non-existing@apache.org");
        assertEquals(1, attachments.getContentIDSet().size());
    }

    private void testReadBase64EncodedAttachment(boolean useFile) throws Exception {
        // Note: We are only interested in the MimeMultipart, but we need to create a
        //       MimeMessage to be able to calculate the correct content type
        MimeMessage message = new MimeMessage((Session)null);
        MimeMultipart mp = new MimeMultipart("related");
        
        // Prepare the "SOAP" part
        MimeBodyPart bp1 = new MimeBodyPart();
        // Obviously this is not SOAP, but this is irrelevant for this test
        bp1.setText("<root/>", "utf-8", "xml");
        bp1.addHeader("Content-Transfer-Encoding", "binary");
        bp1.addHeader("Content-ID", "part1@apache.org");
        mp.addBodyPart(bp1);
        
        // Prepare the attachment
        MimeBodyPart bp2 = new MimeBodyPart();
        byte[] content = new byte[8192];
        new Random().nextBytes(content);
        bp2.setDataHandler(new DataHandler(new ByteArrayDataSource(content, "application/octet-stream")));
        bp2.addHeader("Content-Transfer-Encoding", "base64");
        bp2.addHeader("Content-ID", "part2@apache.org");
        mp.addBodyPart(bp2);
        
        message.setContent(mp);
        // Compute the correct content type
        message.saveChanges();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mp.writeTo(baos);
        String contentType = message.getContentType();
        
        InputStream in = new ByteArrayInputStream(baos.toByteArray());
        Attachments attachments;
        if (useFile) {
            attachments = new Attachments(in, contentType, true, System.getProperty("basedir", ".") + "/target", "1024");
        } else {
            attachments = new Attachments(in, contentType);
        }
        DataHandler dh = attachments.getDataHandler("part2@apache.org");
        byte[] content2 = IOUtils.toByteArray(dh.getInputStream());
        assertTrue(Arrays.equals(content, content2));
    }

    public void testReadBase64EncodedAttachmentWithPartOnMemory() throws Exception {
        testReadBase64EncodedAttachment(false);
    }

    public void testReadBase64EncodedAttachmentWithPartOnFile() throws Exception {
        testReadBase64EncodedAttachment(true);
    }

    public void testCachedFilesExpired() throws Exception {
        
        // Set file expiration to 10 seconds
        long INTERVAL = 3 * 1000; // 3 seconds for Thread to sleep

       
        // Get the AttachmentCacheMonitor and force it to remove files after
        // 10 seconds.
        AttachmentCacheMonitor acm = AttachmentCacheMonitor.getAttachmentCacheMonitor();
        int previousTime = acm.getTimeout();
        
        try {
            acm.setTimeout(10); 


            File aFile = new File("A");
            aFile.createNewFile();
            String aFileName = aFile.getCanonicalPath();
            acm.register(aFileName);

            Thread.sleep(INTERVAL);

            File bFile = new File("B");
            bFile.createNewFile();
            String bFileName = bFile.getCanonicalPath();
            acm.register(bFileName);

            Thread.sleep(INTERVAL);

            acm.access(aFileName);

            // time since file A registration <= cached file expiration
            assertTrue("File A should still exist", aFile.exists());

            Thread.sleep(INTERVAL);

            acm.access(bFileName);

            // time since file B registration <= cached file expiration
            assertTrue("File B should still exist", bFile.exists());

            Thread.sleep(INTERVAL);

            File cFile = new File("C");
            cFile.createNewFile();
            String cFileName = cFile.getCanonicalPath();
            acm.register(cFileName);
            acm.access(bFileName);

            Thread.sleep(INTERVAL);

            acm.checkForAgedFiles();

            // time since file C registration <= cached file expiration
            assertTrue("File C should still exist", cFile.exists());

            Thread.sleep(10* INTERVAL);  // Give task loop time to delete aged files


            // All files should be gone by now
            assertFalse("File A should no longer exist", aFile.exists());
            assertFalse("File B should no longer exist", bFile.exists());
            assertFalse("File C should no longer exist", cFile.exists());
        } finally {
       
            // Reset the timeout to the previous value so that no 
            // other tests are affected
            acm.setTimeout(previousTime);
        }
    }
}
