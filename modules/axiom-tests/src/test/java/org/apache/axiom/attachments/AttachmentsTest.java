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
import org.apache.axiom.om.MIMEResource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.om.impl.MIMEOutputUtils;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPModelBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

public class AttachmentsTest extends AbstractTestCase {
    public void testWritingBinaryAttachments() throws Exception {
        MIMEResource testMessage = TestConstants.MTOM_MESSAGE;

        // Read in message: SOAPPart and 2 image attachments
        InputStream inStream = getTestResource(testMessage.getName());
        Attachments attachments = new Attachments(inStream, testMessage.getContentType());
        
        attachments.getRootPartInputStream();

        String[] contentIDs = attachments.getAllContentIDs();
        
        OMOutputFormat oof = new OMOutputFormat();
        oof.setDoOptimize(true);
        oof.setMimeBoundary(testMessage.getBoundary());
        oof.setRootContentId(testMessage.getStart());
        
        // Write out the message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(baos, oof);
        
        OMXMLParserWrapper builder =
            OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, attachments);
        OMElement om = builder.getDocumentElement();
        om.serialize(writer);
        om.close(false);
        String outNormal = baos.toString();
        
        assertTrue(outNormal.indexOf("base64") == -1);
        
        // Now do it again but use base64 content-type-encoding for 
        // binary attachments
        baos = new ByteArrayOutputStream();
        oof.setProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, 
                        Boolean.TRUE);
        writer = new MTOMXMLStreamWriter(baos, oof);
        builder = 
            OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, attachments);
        om = builder.getDocumentElement();
        om.serialize(writer);
        om.close(false);
        String outBase64 = baos.toString();
        
        
        // Do a quick check to see if the data is base64 and is
        // writing base64 compliant code.
        assertTrue(outBase64.indexOf("base64") != -1);
        assertTrue(outBase64.indexOf("GBgcGBQgHBwcJCQgKDBQNDAsL") != -1);
        
        // Now read the data back in
        InputStream is = new ByteArrayInputStream(outBase64.getBytes());
        Attachments attachments2 = new Attachments(is, testMessage.getContentType());
        
        // Now write it back out with binary...
        baos = new ByteArrayOutputStream();
        oof.setProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, 
                        Boolean.FALSE);
        writer = new MTOMXMLStreamWriter(baos, oof);
        builder = 
            OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, attachments2);
        om = builder.getDocumentElement();
        om.serialize(writer);
        om.close(false);
        String outBase64ToNormal = baos.toString();
        
        assertTrue(outBase64ToNormal.indexOf("base64") == -1);
        
        // Now do it again but use base64 content-type-encoding for 
        // binary attachments
        is = new ByteArrayInputStream(outBase64.getBytes());
        attachments2 = new Attachments(is, testMessage.getContentType());
        baos = new ByteArrayOutputStream();
        oof.setProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, 
                        Boolean.TRUE);
        writer = new MTOMXMLStreamWriter(baos, oof);
        builder = 
            OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, attachments2);
        om = builder.getDocumentElement();
        om.serialize(writer);
        om.close(false);
        String outBase64ToBase64 = baos.toString();
        
        // Do a quick check to see if the data is base64 and is
        // writing base64 compliant code.
        assertTrue(outBase64ToBase64.indexOf("base64") != -1);
        assertTrue(outBase64ToBase64.indexOf("GBgcGBQgHBwcJCQgKDBQNDAsL") != -1);
    }
    
    public void testSWAWriteWithIncomingOrder() throws Exception {

        // Read the stream that has soap xml followed by BAttachment then AAttachment
        InputStream inStream = getTestResource(TestConstants.SWA_MESSAGE.getName());
        Attachments attachments = new Attachments(inStream, TestConstants.SWA_MESSAGE.getContentType());

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
