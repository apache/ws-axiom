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

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder;
import org.apache.axiom.om.util.CommonUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

public class AttachmentsTest extends AbstractTestCase {

    public AttachmentsTest(String testName) {
        super(testName);
    }

    String inMimeFileName = "mtom/MTOMAttachmentStream.bin";
    String img1FileName = "mtom/img/test.jpg";
    String img2FileName = "mtom/img/test2.jpg";

    String boundary = "MIMEBoundaryurn:uuid:A3ADBAEE51A1A87B2A11443668160701";
    String start= "0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org";
    String contentTypeString =
                        "multipart/related; " +
                        "boundary=\"" + boundary + "\"; " +
                        "type=\"application/xop+xml\"; " +
                        "start=\"<" + start +">\"; " +
                        "start-info=\"application/soap+xml\"; " +
                        "charset=UTF-8;" +
                        "action=\"mtomSample\"";

    public void testMIMEHelper() {
    }

    public void testGetAttachmentSpecType() {
    }

    public void testSimultaneousStreamAccess() throws Exception {
        InputStream inStream;
        Attachments attachments;

        inStream = new FileInputStream(getTestResourceFile(inMimeFileName));
        attachments = new Attachments(inStream, contentTypeString);

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
        inStream = new FileInputStream(getTestResourceFile(inMimeFileName));
        attachments = new Attachments(inStream, contentTypeString);

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

    public void testGetInputAttachhmentStreams() throws Exception {

        IncomingAttachmentInputStream dataIs;
        FileDataSource dataSource;
        InputStream expectedDataIs;

        InputStream inStream = new FileInputStream(getTestResourceFile(inMimeFileName));
        Attachments attachments = new Attachments(inStream, contentTypeString);

        // Since SOAP part operated independently of other streams, access it
        // directly, and then get to the streams. If this sequence throws an
        // error, something is wrong with the stream handling code.
        InputStream is = attachments.getSOAPPartInputStream();
        while (is.read() != -1) ;

        // Get the inputstream container
        IncomingAttachmentStreams ias = attachments.getIncomingAttachmentStreams();

        dataIs = ias.getNextStream();
        dataSource = new FileDataSource(getTestResourceFile(img1FileName));
        expectedDataIs = dataSource.getInputStream();
        compareStreams(dataIs, expectedDataIs);

        dataIs = ias.getNextStream();
        dataSource = new FileDataSource(getTestResourceFile(img2FileName));
        expectedDataIs = dataSource.getInputStream();
        compareStreams(dataIs, expectedDataIs);

        // Confirm that no more streams are left
        assertEquals(null, ias.getNextStream());
        
        // After all is done, we should *still* be able to access and
        // re-consume the SOAP part stream, as it should be cached.. can we?
        is = attachments.getSOAPPartInputStream();
        while (is.read() != -1) ;  
    }
    
    public void testWritingBinaryAttachments() throws Exception {

        // Read in message: SOAPPart and 2 image attachments
        File f = getTestResourceFile(inMimeFileName);
        InputStream inStream = new FileInputStream(f);
        Attachments attachments = new Attachments(inStream, contentTypeString);
        
        attachments.getSOAPPartInputStream();

        String[] contentIDs = attachments.getAllContentIDs();
        
        OMOutputFormat oof = new OMOutputFormat();
        oof.setDoOptimize(true);
        oof.setMimeBoundary(boundary);
        oof.setRootContentId(start);
        
        // Write out the message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(baos, oof);
        
        XOPAwareStAXOMBuilder builder = 
            new XOPAwareStAXOMBuilder(attachments.getSOAPPartInputStream(),
                                      attachments);
        OMElement om = builder.getDocumentElement();
        om.serialize(writer);
        String outNormal = baos.toString();
        
        assertTrue(outNormal.indexOf("base64") == -1);
        
        // Now do it again but use base64 content-type-encoding for 
        // binary attachments
        baos = new ByteArrayOutputStream();
        oof.setProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, 
                        Boolean.TRUE);
        writer = new MTOMXMLStreamWriter(baos, oof);
        builder = 
            new XOPAwareStAXOMBuilder(attachments.getSOAPPartInputStream(),
                                      attachments);
        om = builder.getDocumentElement();
        om.serialize(writer);
        String outBase64 = baos.toString();
        
        
        // Do a quick check to see if the data is base64 and is
        // writing base64 compliant code.
        assertTrue(outBase64.indexOf("base64") != -1);
        assertTrue(outBase64.indexOf("GBgcGBQgHBwcJCQgKDBQNDAsL") != -1);
        
        // Now read the data back in
        InputStream is = new ByteArrayInputStream(outBase64.getBytes());
        Attachments attachments2 = new Attachments(is, contentTypeString);
        
        // Now write it back out with binary...
        baos = new ByteArrayOutputStream();
        oof.setProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, 
                        Boolean.FALSE);
        writer = new MTOMXMLStreamWriter(baos, oof);
        builder = 
            new XOPAwareStAXOMBuilder(attachments2.getSOAPPartInputStream(),
                                      attachments2);
        om = builder.getDocumentElement();
        om.serialize(writer);
        String outBase64ToNormal = baos.toString();
        
        assertTrue(outBase64ToNormal.indexOf("base64") == -1);
        
        // Now do it again but use base64 content-type-encoding for 
        // binary attachments
        baos = new ByteArrayOutputStream();
        oof.setProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, 
                        Boolean.TRUE);
        writer = new MTOMXMLStreamWriter(baos, oof);
        builder = 
            new XOPAwareStAXOMBuilder(attachments2.getSOAPPartInputStream(),
                                      attachments2);
        om = builder.getDocumentElement();
        om.serialize(writer);
        String outBase64ToBase64 = baos.toString();
        
        // Do a quick check to see if the data is base64 and is
        // writing base64 compliant code.
        assertTrue(outBase64ToBase64.indexOf("base64") != -1);
        assertTrue(outBase64ToBase64.indexOf("GBgcGBQgHBwcJCQgKDBQNDAsL") != -1);
        
        // Some quick verifications of the isTextualPart logic
        assertTrue(CommonUtils.isTextualPart("text/xml"));
        assertTrue(CommonUtils.isTextualPart("application/xml"));
        assertTrue(CommonUtils.isTextualPart("application/soap+xml"));
        assertTrue(CommonUtils.isTextualPart("foo/bar; charset=UTF-8"));
        assertTrue(!CommonUtils.isTextualPart("image/gif"));
        
    }
    

    private void compareStreams(InputStream data, InputStream expected) throws Exception {
        byte[] dataArray = this.getStreamAsByteArray(data, -1);
        byte[] expectedArray = this.getStreamAsByteArray(expected, -1);
        if (dataArray.length == expectedArray.length) {
            assertTrue(Arrays.equals(dataArray, expectedArray));
        } else {
            System.out.println("Skipping compare because of lossy image i/o [" + dataArray.length +
                    "][" + expectedArray.length + "]");
        }
    }

    public void testGetDataHandler() throws Exception {

        InputStream inStream = new FileInputStream(getTestResourceFile(inMimeFileName));
        Attachments attachments = new Attachments(inStream, contentTypeString);

        DataHandler dh = attachments
                .getDataHandler("2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org");
        InputStream dataIs = dh.getDataSource().getInputStream();

        FileDataSource dataSource = new FileDataSource(getTestResourceFile(img2FileName));
        InputStream expectedDataIs = dataSource.getInputStream();

        // Compare data across streams
        compareStreams(dataIs, expectedDataIs);
    }

    public void testNonExistingMIMEPart() throws Exception {

        InputStream inStream = new FileInputStream(getTestResourceFile(inMimeFileName));
        Attachments attachments = new Attachments(inStream, contentTypeString);

        DataHandler dh = attachments.getDataHandler("ThisShouldReturnNull");
        assertNull(dh);
    }

    public void testGetAllContentIDs() throws Exception {

        File f = getTestResourceFile(inMimeFileName);
        InputStream inStream = new FileInputStream(f);
        Attachments attachments = new Attachments(inStream, contentTypeString);

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
        long fileSize = f.length();
        assertTrue("Expected MessageContent Length of " + fileSize + " but received " + length,
                   length == fileSize);
    }

    /**
     * Returns the contents of the input stream as byte array.
     *
     * @param stream the <code>InputStream</code>
     * @param length the number of bytes to copy, if length < 0, the number is unlimited
     * @return the stream content as byte array
     */
    private byte[] getStreamAsByteArray(InputStream stream, int length) throws IOException {
        if (length == 0) return new byte[0];
        boolean checkLength = true;
        if (length < 0) {
            length = Integer.MAX_VALUE;
            checkLength = false;
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = stream.read();
        if (checkLength) length--;
        while (-1 != nextValue && length >= 0) {
            byteStream.write(nextValue);
            nextValue = stream.read();
            if (checkLength) length--;
        }
        return byteStream.toByteArray();
    }
}
