/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.attachments;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.axiom.om.AbstractTestCase;


/**
 * Test the PartOnFile class
 */

public class IncomingAttachmentInputTest extends AbstractTestCase {
	
	public IncomingAttachmentInputTest(String testName) {
        super(testName);
    }

    String inMimeFileName = "mtom/MTOMAttachmentStream.bin";
    String contentTypeString = "multipart/related; boundary=\"MIMEBoundaryurn:uuid:A3ADBAEE51A1A87B2A11443668160701\"; type=\"application/xop+xml\"; start=\"<0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org>\"; start-info=\"application/soap+xml\"; charset=UTF-8;action=\"mtomSample\"";
    File temp;

	public void testIncomingAttachmentInputStreamFunctions() throws Exception {
        InputStream inStream = new FileInputStream(getTestResourceFile(inMimeFileName));
        Attachments attachments = new Attachments(inStream, contentTypeString);

        // Get the inputstream container
        IncomingAttachmentStreams ias = attachments.getIncomingAttachmentStreams();
        
        IncomingAttachmentInputStream dataIs;

        // Img1 stream
        dataIs = ias.getNextStream();
        
        // Make sure it was the first attachment
        assertEquals("<1.urn:uuid:A3ADBAEE51A1A87B2A11443668160943@apache.org>", dataIs.getContentId());
        
        // Consume the stream
        while (dataIs.read() != -1);
        
        // Img2 stream
        dataIs = ias.getNextStream();
        assertEquals("<2.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org>", dataIs.getContentId());

        // Test if getContentType() works..
        assertEquals("image/jpeg", dataIs.getContentType());

        // Test if a adding/getting a header works
        dataIs.addHeader("new-header", "test-value");
        assertEquals("test-value", dataIs.getHeader("new-header"));
	}
}
