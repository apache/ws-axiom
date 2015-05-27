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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.MIMEOutputUtils;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.SwASample;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.TestCase;

public class AttachmentsTest extends TestCase {
    public void testSWAWriteWithIncomingOrder() throws Exception {

        // Read the stream that has soap xml followed by BAttachment then AAttachment
        InputStream inStream = SwASample.SAMPLE1.getInputStream();
        Attachments attachments = new Attachments(inStream, SwASample.SAMPLE1.getContentType());

        // Get the contentIDs to force the reading
        String[] contentIDs = attachments.getAllContentIDs();
        
        // Get the root
        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(attachments.getRootPartInputStream(), "UTF-8");
        OMElement root = builder.getDocumentElement();
        StringWriter xmlWriter = new StringWriter();
        root.serialize(xmlWriter);
        
        // Serialize the message using the legacy behavior (order by content id)
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding("utf-8");
        format.setDoingSWA(true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        MIMEOutputUtils.writeSOAPWithAttachmentsMessage(xmlWriter, baos, attachments, format);
        
        String text = baos.toString();
        // Assert that AAttachment occurs before BAttachment since
        // that is the natural ordering of the content ids.
        assertTrue(text.indexOf("BAttachment") < text.indexOf("AAttachment"));
        
    }
}
