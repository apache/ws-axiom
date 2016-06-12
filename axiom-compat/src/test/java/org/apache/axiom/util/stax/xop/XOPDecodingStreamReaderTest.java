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

package org.apache.axiom.util.stax.xop;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.impl.builder.AttachmentsMimePartProvider;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.ts.soap.MTOMSample;
import org.apache.commons.codec.binary.Base64;

public class XOPDecodingStreamReaderTest extends TestCase {
    private XMLStreamReader getXOPDecodingStreamReader() throws Exception {
        Attachments attachments = new Attachments(MTOMSample.SAMPLE1.getInputStream(),
                MTOMSample.SAMPLE1.getContentType());
        return new XOPDecodingStreamReader(
                StAXUtils.createXMLStreamReader(attachments.getRootPartInputStream()),
                new AttachmentsMimePartProvider(attachments));
    }
    
    public void testCompareToInlined() throws Exception {
        XMLStreamReader expected = StAXUtils.createXMLStreamReader(
                MTOMSample.SAMPLE1.getInlinedMessage());
        XMLStreamReader actual = getXOPDecodingStreamReader();
        XMLStreamReaderComparator comparator = new XMLStreamReaderComparator(expected, actual);
        comparator.addPrefix("xop");
        comparator.compare();
        expected.close();
        actual.close();
    }
    
    public void testGetElementText() throws Exception {
        XMLStreamReader reader = getXOPDecodingStreamReader();
        while (!reader.isStartElement() || !reader.getLocalName().equals("image1")) {
            reader.next();
        }
        String base64 = reader.getElementText();
        byte[] data = Base64.decodeBase64(base64);
        // The data is actually a JPEG image. Try to decode it to check that the data is not
        // corrupted.
        ImageIO.read(new ByteArrayInputStream(data));
        reader.close();
    }
}
