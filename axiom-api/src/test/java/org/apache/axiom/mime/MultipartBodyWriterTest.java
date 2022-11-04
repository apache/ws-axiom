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
package org.apache.axiom.mime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.util.UIDGenerator;

import junit.framework.TestCase;

public class MultipartBodyWriterTest extends TestCase {
    private void test(ContentTransferEncoding contentTransferEncoding) throws Exception {
        Random random = new Random();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MultipartBodyWriter mpw = new MultipartBodyWriter(baos, UIDGenerator.generateMimeBoundary());
        byte[] content = new byte[8192];
        random.nextBytes(content);
        OutputStream partOutputStream = mpw.writePart("application/octet-stream", contentTransferEncoding, UIDGenerator.generateContentId(), null);
        partOutputStream.write(content);
        partOutputStream.close();
        mpw.complete();
        
        MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(baos.toByteArray()));
        assertEquals(1, mp.getCount());
        MimeBodyPart bp = (MimeBodyPart)mp.getBodyPart(0);
        assertEquals(contentTransferEncoding.toString(), bp.getHeader("Content-Transfer-Encoding")[0]);
        baos.reset(); 
        bp.getDataHandler().writeTo(baos);
        assertTrue(Arrays.equals(content, baos.toByteArray()));
    }
    
    public void testBinary() throws Exception {
        test(ContentTransferEncoding.BINARY);
    }
    
    public void testBase64() throws Exception {
        test(ContentTransferEncoding.BASE64);
    }
}
