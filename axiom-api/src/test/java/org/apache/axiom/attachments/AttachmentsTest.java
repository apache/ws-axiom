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

import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.ext.activation.SizeAwareDataSource;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MIMEException;
import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.activation.TextDataSource;
import org.apache.axiom.testutils.io.ExceptionInputStream;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.soap.MTOMSample;
import org.apache.axiom.ts.soap.SwASample;
import org.apache.axiom.ts.xml.MIMESample;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

public class AttachmentsTest extends AbstractTestCase {
    private static String getAttachmentsDir() {
        File attachmentsDir = new File(System.getProperty("basedir", ".") + "/target/attachments");
        attachmentsDir.mkdirs();
        return attachmentsDir.getAbsolutePath();
    }
    
    public void testGetDataHandler() throws Exception {
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());

        DataHandler dh = attachments
                .getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
        InputStream dataIs = dh.getDataSource().getInputStream();

        InputStream expectedDataIs = MTOMSample.SAMPLE1.getPart(2);

        // Compare data across streams
        IOTestUtils.compareStreams(dataIs, expectedDataIs);
    }

    public void testGetDataHandlerNonExistingMIMEPart() throws Exception {
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());

        DataHandler dh = attachments.getDataHandler("ThisShouldReturnNull");
        assertNull(dh);
    }

    public void testGetDataHandlerNonExistingMIMEPartWithoutStream() throws Exception {
        Attachments attachments = new Attachments();
        attachments.addDataHandler("id@apache.org", new DataHandler("test", "text/plain"));
        assertNull(attachments.getDataHandler("non-existing@apache.org"));
    }

    public void testGetAllContentIDs() throws Exception {
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());

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
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());

        Set<String> idSet = attachments.getContentIDSet();
        assertTrue(idSet.contains("0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org"));
        assertTrue(idSet.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
        assertTrue(idSet.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
    }
    
    public void testGetContentIDSetWithoutStream() {
        Attachments attachments = new Attachments();
        attachments.addDataHandler("id1@apache.org", new DataHandler("content1", "text/plain"));
        attachments.addDataHandler("id2@apache.org", new DataHandler("content2", "text/plain"));
        attachments.addDataHandler("id3@apache.org", new DataHandler("content3", "text/plain"));
        
        Set<String> idSet = attachments.getContentIDSet();
        assertTrue(idSet.contains("id1@apache.org"));
        assertTrue(idSet.contains("id2@apache.org"));
        assertTrue(idSet.contains("id3@apache.org"));
    }
    
    public void testGetContentLength() throws IOException {
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());
        
        // Make sure the length is correct
        long length = attachments.getContentLength();
        long fileSize = IOUtils.toByteArray(MTOMSample.SAMPLE1.getInputStream()).length;
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

    private void testGetRootPartContentID(String contentTypeStartParam, String contentId)
            throws Exception {
        MimeMessage message = new MimeMessage((Session)null);
        MimeMultipart mp = new MimeMultipart("related");
        MimeBodyPart rootPart = new MimeBodyPart();
        rootPart.setDataHandler(new DataHandler(new TextDataSource("<root/>", "utf-8", "xml")));
        rootPart.addHeader("Content-Transfer-Encoding", "binary");
        rootPart.addHeader("Content-ID", "<" + contentId + ">");
        mp.addBodyPart(rootPart);
        message.setContent(mp);
        message.saveChanges();
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        mp.writeTo(out);
        out.close();

        Attachments attachments = new Attachments(
                blob.getInputStream(),
                new ContentType(message.getContentType())
                        .toBuilder()
                        .setParameter("start", contentTypeStartParam)
                        .build()
                        .toString());
        assertEquals("Did not obtain correct content ID", contentId,
                attachments.getRootPartContentID());
    }
    
    public void testGetRootPartContentIDWithoutBrackets() throws Exception {
        testGetRootPartContentID("my-content-id@localhost", "my-content-id@localhost");
    }
    
    public void testGetRootPartContentIDWithBrackets() throws Exception {
        testGetRootPartContentID("<my-content-id@localhost>", "my-content-id@localhost");
    }
    
    // Not sure when exactly somebody uses the "cid:" prefix in the start parameter, but
    // this is how the code currently works.
    public void testGetRootPartContentIDWithCidPrefix() throws Exception {
        testGetRootPartContentID("cid:my-content-id@localhost", "my-content-id@localhost");
    }
    
    // Regression test for AXIOM-195
    public void testGetRootPartContentIDWithCidPrefix2() throws Exception {
        testGetRootPartContentID("<cid-73920@192.168.0.1>", "cid-73920@192.168.0.1");
    }
    
    public void testGetRootPartContentIDShort() throws Exception {
        testGetRootPartContentID("bbb", "bbb");
    }
    
    public void testGetRootPartContentIDShortWithBrackets() throws Exception {
        testGetRootPartContentID("<b>", "b");
    }
    
    public void testGetRootPartContentIDBorderline() throws Exception {
        testGetRootPartContentID("cid:", "cid:");
    }
    
    /**
     * Tests that {@link Attachments#getRootPartContentType()} throws a meaningful exception if it
     * is unable to determine the content type.
     */
    public void testGetRootPartContentTypeWithContentIDMismatch() {
        String contentType = "multipart/related; boundary=\"" + MTOMSample.SAMPLE1.getBoundary() +
                "\"; type=\"text/xml\"; start=\"<wrong-content-id@example.org>\"";
        try {
            Attachments attachments = new Attachments(MTOMSample.SAMPLE1.getInputStream(), contentType);
            attachments.getRootPartContentType();
            fail("Expected MIMEException");
        } catch (MIMEException ex) {
            // OK, expected
        } catch (Throwable ex) {
            fail("Unexpected exception: " + ex.getClass().getName());
        }
    }
    
    public void testGetIncomingAttachmentStreams() throws Exception {
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());

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
        MTOMSample sample = MTOMSample.SAMPLE1;

        IncomingAttachmentInputStream dataIs;
        InputStream expectedDataIs;

        InputStream inStream = sample.getInputStream();
        Attachments attachments = new Attachments(inStream, sample.getContentType());

        // Since SOAP part operated independently of other streams, access it
        // directly, and then get to the streams. If this sequence throws an
        // error, something is wrong with the stream handling code.
        InputStream is = attachments.getRootPartInputStream();
        while (is.read() != -1) ;

        // Get the inputstream container
        IncomingAttachmentStreams ias = attachments.getIncomingAttachmentStreams();

        dataIs = ias.getNextStream();
        expectedDataIs = sample.getPart(1);
        IOTestUtils.compareStreams(dataIs, expectedDataIs);

        dataIs = ias.getNextStream();
        expectedDataIs = sample.getPart(2);
        IOTestUtils.compareStreams(dataIs, expectedDataIs);

        // Confirm that no more streams are left
        assertNull(ias.getNextStream());
        
        // After all is done, we should *still* be able to access and
        // re-consume the SOAP part stream, as it should be cached.. can we?
        is = attachments.getRootPartInputStream();
        while (is.read() != -1) ;  
    }
    
    public void testSimultaneousStreamAccess() throws Exception {
        InputStream inStream;
        Attachments attachments;
    
        inStream = MTOMSample.SAMPLE1.getInputStream();
        attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());
    
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
        inStream = MTOMSample.SAMPLE1.getInputStream();
        attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());
    
        attachments.getIncomingAttachmentStreams();
    
        // These should NOT throw error even though they are using part based access
        try {
            String contentType = attachments.getRootPartContentType();
            assertTrue(contentType.indexOf("application/xop+xml;") >=0);
            assertTrue(contentType.indexOf("charset=UTF-8;") >=0);
            assertTrue(contentType.indexOf("type=\"application/soap+xml\";") >=0);
        } catch (IllegalStateException ise) {
            fail("No exception expected when requesting SOAP part data");
            ise.printStackTrace();
        }
    
        try {
            attachments.getRootPartInputStream();
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
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());

        Set<String> list = attachments.getContentIDSet();
        assertEquals(3, list.size());
        
        assertTrue(list.contains("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org"));
        assertTrue(list.contains("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org"));
        
        attachments.removeDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");

        List<String> list2 = attachments.getContentIDList();
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
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());
        attachments.removeDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");
        Set<String> idSet = attachments.getContentIDSet();
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
        InputStream inStream = MTOMSample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE1.getContentType());
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
        bp1.setDataHandler(new DataHandler(new TextDataSource("<root/>", "utf-8", "xml")));
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
            attachments = new Attachments(in, contentType, true, getAttachmentsDir(), "1024");
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

    /**
     * Tests that {@link Attachments} can successfully read an attachment with zero length. This is
     * a regression test for
     * <a href="https://issues.apache.org/jira/browse/AXIOM-383">AXIOM-383</a>.
     * 
     * @throws Exception
     */
    public void testZeroLengthAttachment() throws Exception {
        InputStream in = getTestResource("mtom/zero-length-attachment.bin");
        try {
            Attachments attachments = new Attachments(in,
                    "multipart/related; " +
                    "boundary=MIMEBoundaryurn_uuid_0549F3F826EC3041861188639371825; " +
                    "type=\"application/xop+xml\"; " +
                    "start=\"0.urn:uuid:0549F3F826EC3041861188639371826@apache.org\"; " +
                    "start-info=\"application/soap+xml\"; action=\"urn:test\"");
            DataHandler dh = attachments.getDataHandler("1.urn:uuid:0549F3F826EC3041861188639371827@apache.org");
            InputStream content = dh.getInputStream();
            assertEquals(-1, content.read());
        } finally {
            in.close();
        }
    }
    
    public void testPurgeDataSource() throws Exception {
        InputStream in = getTestResource("mtom/msg-soap-wls81.txt");
        MyLifecycleManager manager = new MyLifecycleManager();
        Attachments attachments = new Attachments(manager, in,
                "multipart/related;type=\"text/xml\";boundary=\"----=_Part_0_3437046.1188904239130\";start=__WLS__1188904239161__SOAP__",
                true, getAttachmentsDir(), "1024");
        
        // Read the attachment once to make sure it is buffered
        DataHandler dh = attachments.getDataHandler("__WLS__1188904239162__SOAP__");
        assertTrue(dh instanceof DataHandlerExt);
        InputStream content = dh.getInputStream();
        IOUtils.copy(content, NullOutputStream.NULL_OUTPUT_STREAM);
        content.close();
        
        assertEquals(1, manager.getFileCount());
        ((DataHandlerExt)dh).purgeDataSource();
        assertEquals(0, manager.getFileCount());
        
        in.close();
    }
    
    /**
     * Tests that after consuming the input stream returned by {@link DataHandlerExt#readOnce()} for
     * an attachment that has been buffered on disk, the temporary file for that attachment is
     * deleted.
     * 
     * @throws Exception
     */
    public void testReadOnceOnBufferedPart() throws Exception {
        InputStream in = getTestResource("mtom/msg-soap-wls81.txt");
        MyLifecycleManager manager = new MyLifecycleManager();
        Attachments attachments = new Attachments(manager, in,
                "multipart/related;type=\"text/xml\";boundary=\"----=_Part_0_3437046.1188904239130\";start=__WLS__1188904239161__SOAP__",
                true, getAttachmentsDir(), "1024");
        
        // Read the attachment once to make sure it is buffered
        DataHandler dh = attachments.getDataHandler("__WLS__1188904239162__SOAP__");
        InputStream content = dh.getInputStream();
        IOUtils.copy(content, NullOutputStream.NULL_OUTPUT_STREAM);
        content.close();
        
        assertEquals(1, manager.getFileCount());

        // Now consume the content of the attachment
        content = ((DataHandlerExt)dh).readOnce();
        IOUtils.copy(content, NullOutputStream.NULL_OUTPUT_STREAM);
        content.close();
        
        // The temporary file should have been deleted
        assertEquals(0, manager.getFileCount());
        
        in.close();
    }
    
    /**
     * Tests that attachments are correctly buffered on file if the threshold is very low. This is a
     * regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-61">AXIOM-61</a>.
     * 
     * @throws Exception
     */
    public void testFileBufferingWithLowThreshold() throws Exception {
        InputStream in = getTestResource("mtom/msg-soap-wls81.txt");
        Attachments attachments = new Attachments(in,
                "multipart/related;type=\"text/xml\";boundary=\"----=_Part_0_3437046.1188904239130\";start=__WLS__1188904239161__SOAP__",
                true, getAttachmentsDir(), "1");
        
        DataHandler dh = attachments.getDataHandler("__WLS__1188904239162__SOAP__");
        BufferedReader reader = new BufferedReader(new InputStreamReader(dh.getInputStream(), "UTF-8"));
        assertEquals("%PDF-1.3", reader.readLine());
        reader.close();
        
        in.close();
    }

    /**
     * Tests that a call to {@link DataHandlerExt#readOnce()} on a {@link DataHandler} returned by
     * the {@link Attachments} object streams the content of the MIME part.
     * 
     * @throws Exception
     */
    public void testDataHandlerStreaming() throws Exception {
        // Note: We are only interested in the MimeMultipart, but we need to create a
        //       MimeMessage to be able to calculate the correct content type
        MimeMessage message = new MimeMessage((Session)null);
        final MimeMultipart mp = new MimeMultipart("related");
        
        // Prepare the "SOAP" part
        MimeBodyPart bp1 = new MimeBodyPart();
        // Obviously this is not SOAP, but this is irrelevant for this test
        bp1.setDataHandler(new DataHandler(new TextDataSource("<root/>", "utf-8", "xml")));
        bp1.addHeader("Content-Transfer-Encoding", "binary");
        bp1.addHeader("Content-ID", "part1@apache.org");
        mp.addBodyPart(bp1);
        
        // Create an attachment that is larger than the maximum heap
        DataSource dataSource = new RandomDataSource((int)Math.min(Runtime.getRuntime().maxMemory(), Integer.MAX_VALUE));
        MimeBodyPart bp2 = new MimeBodyPart();
        bp2.setDataHandler(new DataHandler(dataSource));
        bp2.addHeader("Content-Transfer-Encoding", "binary");
        bp2.addHeader("Content-ID", "part2@apache.org");
        mp.addBodyPart(bp2);
        
        message.setContent(mp);
        // Compute the correct content type
        message.saveChanges();
        
        // We use a pipe (with a producer running in a separate thread) because obviously we can't
        // store the multipart in memory.
        final PipedOutputStream pipeOut = new PipedOutputStream();
        PipedInputStream pipeIn = new PipedInputStream(pipeOut);
        
        Thread producerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    try {
                        mp.writeTo(pipeOut);
                    } finally {
                        pipeOut.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        producerThread.start();
        
        try {
            // We configure Attachments to buffer MIME parts in memory. If the part content is not
            // streamed, then this will result in an OOM error.
            Attachments attachments = new Attachments(pipeIn, message.getContentType());
            DataHandlerExt dh = (DataHandlerExt)attachments.getDataHandler("part2@apache.org");
            IOTestUtils.compareStreams(dataSource.getInputStream(), dh.readOnce());
        } finally {
            pipeIn.close();
        }
    }

    private void testTurkishLocale(String contentIDHeaderName) throws Exception {
        Locale locale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
        try {
            MimeMessage message = new MimeMessage((Session)null);
            MimeMultipart mp = new MimeMultipart("related");
            
            MimeBodyPart bp1 = new MimeBodyPart();
            bp1.setDataHandler(new DataHandler(new TextDataSource("<root/>", "utf-8", "xml")));
            bp1.addHeader("Content-Transfer-Encoding", "binary");
            mp.addBodyPart(bp1);
            
            MimeBodyPart bp2 = new MimeBodyPart();
            byte[] content = new byte[8192];
            new Random().nextBytes(content);
            bp2.setDataHandler(new DataHandler(new TextDataSource("Test", "utf-8", "plain")));
            bp2.addHeader("Content-Transfer-Encoding", "binary");
            bp2.addHeader(contentIDHeaderName, "part@apache.org");
            mp.addBodyPart(bp2);
            
            message.setContent(mp);
            message.saveChanges();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mp.writeTo(baos);
            String contentType = message.getContentType();
            
            InputStream in = new ByteArrayInputStream(baos.toByteArray());
            Attachments attachments = new Attachments(in, contentType);
            assertNotNull(attachments.getDataHandler("part@apache.org"));
        } finally {
            Locale.setDefault(locale);
        }
    }

    // Regression test for AXIOM-389
    public void testTurkishLocale1() throws Exception {
        testTurkishLocale("Content-ID");
    }

    public void testTurkishLocale2() throws Exception {
        testTurkishLocale("content-id");
    }
    
    private void testGetAttachmentSpecType(MIMESample resource, String expectedResult) throws Exception {
        InputStream in = resource.getInputStream();
        try {
            Attachments attachments = new Attachments(in, resource.getContentType());
            assertEquals(expectedResult, attachments.getAttachmentSpecType());
        } finally {
            in.close();
        }
    }
    
    public void testGetAttachmentSpecTypeMTOM() throws Exception {
        testGetAttachmentSpecType(MTOMSample.SAMPLE1, MTOMConstants.MTOM_TYPE);
    }
    
    public void testGetAttachmentSpecTypeSWA() throws Exception {
        testGetAttachmentSpecType(SwASample.SAMPLE1, MTOMConstants.SWA_TYPE);
    }
    
    public void testGetAttachmentSpecTypeWithoutStream() {
        try {
            new Attachments().getAttachmentSpecType();
            fail("Expected OMException");
        } catch (OMException ex) {
            // Expected
        }
    }

    private void testGetSizeOnDataSource(boolean useFiles) throws Exception {
        InputStream in = MTOMSample.SAMPLE1.getInputStream();
        try {
            Attachments attachments;
            if (useFiles) {
                attachments = new Attachments(in, MTOMSample.SAMPLE1.getContentType(),
                        true, getAttachmentsDir(), "4096");
            } else {
                attachments = new Attachments(in, MTOMSample.SAMPLE1.getContentType());
            }
            DataHandler dh = attachments
                    .getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
            DataSource ds = dh.getDataSource();
            assertTrue(ds instanceof SizeAwareDataSource);
            assertEquals(13887, ((SizeAwareDataSource)ds).getSize());
        } finally {
            in.close();
        }
    }
    
    public void testGetSizeOnDataSourceOnMemory() throws Exception {
        testGetSizeOnDataSource(false);
    }
    
    public void testGetSizeOnDataSourceOnFile() throws Exception {
        testGetSizeOnDataSource(true);
    }
    
    public void testIOExceptionInPartHeaders() throws Exception {
        InputStream in = MTOMSample.SAMPLE1.getInputStream();
        try {
            Attachments attachments = new Attachments(new ExceptionInputStream(in, 1050), MTOMSample.SAMPLE1.getContentType());
            // TODO: decide what exception should be thrown exactly here
            try {
                attachments.getDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");
                fail("Expected exception");
            } catch (MIMEException ex) {
                // Expected
            }
        } finally {
            in.close();
        }
    }
    
    public void testIOExceptionInPartContent() throws Exception {
        InputStream in = MTOMSample.SAMPLE1.getInputStream();
        try {
            Attachments attachments = new Attachments(new ExceptionInputStream(in, 1500), MTOMSample.SAMPLE1.getContentType());
            DataHandler dh = attachments.getDataHandler("1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org");
            // TODO: decide what exception should be thrown exactly here
            try {
                dh.getInputStream();
                fail("Expected exception");
            } catch (MIMEException ex) {
                // Expected
            }
        } finally {
            in.close();
        }
    }

    /**
     * Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-467">AXIOM-467</a>.
     */
    public void testQuotedPrintable() throws Exception {
        Attachments attachments = new Attachments(
                MTOMSample.QUOTED_PRINTABLE.getInputStream(),
                MTOMSample.QUOTED_PRINTABLE.getContentType());
        DataHandler dh = attachments.getDataHandler("SDESS_COREP_00000_KO_SNG.xml");
        IOTestUtils.compareStreams(
                MTOMSample.QUOTED_PRINTABLE.getPart(1),
                dh.getInputStream());
    }

    /**
     * Tests access to a root part that doesn't have a content ID. In this case, the
     * {@link Attachments} API generates a fake content ID.
     * 
     * @throws Exception
     */
    public void testFakeRootPartContentID() throws Exception {
        MimeMessage message = new MimeMessage((Session)null);
        MimeMultipart mp = new MimeMultipart("related");

        DataHandler expectedRootPart = new DataHandler(new TextDataSource("<root/>", "utf-8", "xml"));
        MimeBodyPart bp1 = new MimeBodyPart();
        bp1.setDataHandler(expectedRootPart);
        bp1.addHeader("Content-Transfer-Encoding", "binary");
        mp.addBodyPart(bp1);
        
        MimeBodyPart bp2 = new MimeBodyPart();
        bp2.setDataHandler(new DataHandler(new TextDataSource("Test", "utf-8", "plain")));
        bp2.addHeader("Content-Transfer-Encoding", "binary");
        mp.addBodyPart(bp2);
        
        message.setContent(mp);
        message.saveChanges();
        
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        mp.writeTo(out);
        out.close();
        String contentType = message.getContentType();
        
        Attachments attachments = new Attachments(blob.getInputStream(), contentType);
        String rootPartContentID = attachments.getRootPartContentID();
        assertThat(rootPartContentID).isNotNull();
        DataHandler rootPart = attachments.getDataHandler(rootPartContentID);
        IOTestUtils.compareStreams(rootPart.getInputStream(), expectedRootPart.getInputStream());
    }
}
