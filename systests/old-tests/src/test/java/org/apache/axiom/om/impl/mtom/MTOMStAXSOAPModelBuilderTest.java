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

package org.apache.axiom.om.impl.mtom;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.MTOMSample;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class MTOMStAXSOAPModelBuilderTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    private Attachments createAttachmentsForTestMTOMMessage() throws Exception {
        InputStream inStream = MTOMSample.SAMPLE2.getInputStream();
        return new Attachments(inStream, MTOMSample.SAMPLE2.getContentType());
    }

    private OMElement createTestMTOMMessage() throws Exception {
        return OMXMLBuilderFactory.createSOAPModelBuilder(
                        createAttachmentsForTestMTOMMessage().getMultipartBody())
                .getDocumentElement();
    }

    private void checkSerialization(OMElement root, boolean optimize) throws Exception {
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(optimize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        root.serialize(baos, format);
        String msg = baos.toString();
        if (optimize) {
            // Make sure there is an xop:Include element and an optimized attachment
            assertTrue(msg.indexOf("xop:Include") > 0);
            assertTrue(msg.indexOf("Content-ID: <-1609420109260943731>") > 0);
        } else {
            assertTrue(msg.indexOf("xop:Include") < 0);
            assertTrue(msg.indexOf("Content-ID: <-1609420109260943731>") < 0);
        }
    }

    public void testCreateOMElement() throws Exception {
        OMElement root = createTestMTOMMessage();
        OMElement body = (OMElement) root.getFirstOMChild();
        OMElement data = (OMElement) body.getFirstOMChild();

        Iterator childIt = data.getChildren();
        OMElement child = (OMElement) childIt.next();
        OMText blobNode = (OMText) child.getFirstOMChild();
        /*
         * Following is the procedure the user has to follow to read objects in
         * OBBlob User has to know the object type & whether it is serializable.
         * If it is not he has to use a Custom Defined DataSource to get the
         * Object.
         */
        byte[] expectedObject = new byte[] {13, 56, 65, 32, 12, 12, 7, -3, -2, -1, 98};
        Blob actualBlob = blobNode.getBlob();
        // ByteArrayInputStream object = (ByteArrayInputStream) actualDH
        // .getContent();
        // byte[] actualObject= null;
        //  object.read(actualObject,0,10);

        //  assertEquals("Object check", expectedObject[5],actualObject[5] );
    }

    /**
     * Test that MIME parts are not loaded before requesting the DataHandlers from the corresponding
     * OMText nodes.
     *
     * @throws Exception
     */
    public void testDeferredLoadingOfAttachments() throws Exception {
        Attachments attachments = createAttachmentsForTestMTOMMessage();
        SOAPModelBuilder builder =
                OMXMLBuilderFactory.createSOAPModelBuilder(attachments.getMultipartBody());
        OMDocument doc = builder.getDocument();
        // Find all the binary nodes
        List<OMText> binaryNodes = new ArrayList<>();
        for (Iterator<OMSerializable> it = doc.getDescendants(false); it.hasNext(); ) {
            OMSerializable node = it.next();
            if (node instanceof OMText text && text.isBinary()) {
                binaryNodes.add(text);
            }
        }
        assertFalse(binaryNodes.isEmpty());
        // At this moment only the SOAP part should have been loaded
        assertEquals(1, attachments.getContentIDList().size());
        for (OMText node : binaryNodes) {
            // Request the Blob and do something with it to make sure
            // the part is loaded
            node.getBlob().getInputStream().close();
        }
        assertEquals(binaryNodes.size() + 1, attachments.getContentIDList().size());
    }

    /**
     * Test reading a message containing XOP and ensuring that the the XOP is preserved when it is
     * serialized.
     *
     * @throws Exception
     */
    public void testCreateAndSerializeOptimized() throws Exception {
        OMElement root = createTestMTOMMessage();
        checkSerialization(root, true);
    }

    /**
     * Test reading a message containing XOP. Then make a copy of the message. Then ensure that the
     * XOP is preserved when it is serialized.
     *
     * @throws Exception
     */
    public void testCreateCloneAndSerializeOptimized() throws Exception {
        OMElement root = createTestMTOMMessage();

        // Create a clone of root
        OMElement root2 = root.cloneOMElement();

        // Write out the source
        checkSerialization(root, true);

        // Write out the clone
        checkSerialization(root2, true);
    }

    /**
     * Test reading a message containing XOP. Fully build the tree. Then make a copy of the message.
     * Then ensure that the XOP is preserved when it is serialized.
     *
     * @throws Exception
     */
    public void testCreateBuildCloneAndSerializeOptimized() throws Exception {
        OMElement root = createTestMTOMMessage();

        // Fully build the root
        root.buildWithAttachments();

        // Create a clone of root
        OMElement root2 = root.cloneOMElement();

        // Write out the source
        checkSerialization(root, true);

        // Write out the clone
        checkSerialization(root2, true);
    }

    /**
     * Test reading a message containing XOP. Serialize the tree (with caching). Then ensure that
     * the XOP is preserved when it is serialized again.
     *
     * <p>Regression test for AXIOM-264.
     *
     * @throws Exception
     */
    public void testCreateSerializeAndSerializeOptimized() throws Exception {
        OMElement root = createTestMTOMMessage();

        // Serialize the tree (with caching).
        root.serialize(new ByteArrayOutputStream());

        // Write out the source
        checkSerialization(root, true);
    }

    /**
     * Test reading a message containing XOP. Enable inlining serialization Then ensure that the
     * data is inlined when written
     *
     * @throws Exception
     */
    public void testCreateAndSerializeInlined() throws Exception {
        OMElement root = createTestMTOMMessage();

        checkSerialization(root, false);
    }

    public void testUTF16MTOMMessage() throws Exception {
        String contentTypeString =
                "multipart/Related; charset=\"UTF-8\"; type=\"application/xop+xml\"; boundary=\"----=_AxIs2_Def_boundary_=42214532\"; start=\"SOAPPart\"";
        String cid = "1.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org";
        String xmlPlusMime1 =
                "------=_AxIs2_Def_boundary_=42214532\r\n"
                        + "Content-Type: application/xop+xml; charset=UTF-16; type=\"application/soap+xml\"\r\n"
                        + "Content-Transfer-Encoding: 8bit\r\n"
                        + "Content-ID: SOAPPart\r\n"
                        + "\r\n";
        String xmlPlusMime2 =
                "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body><m:data xmlns:m=\"http://www.example.org/stuff\"><m:name m:contentType=\"text/plain\"><xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:"
                        + cid
                        + "\"></xop:Include></m:name></m:data></soapenv:Body></soapenv:Envelope>\r\n";
        String xmlPlusMime3 =
                "\r\n------=_AxIs2_Def_boundary_=42214532\r\n"
                        + "Content-Transfer-Encoding: binary\r\n"
                        + "Content-ID: "
                        + cid
                        + "\r\n"
                        + "\r\n"
                        + "Foo Bar\r\n"
                        + "------=_AxIs2_Def_boundary_=42214532--\r\n";
        byte[] bytes1 = xmlPlusMime1.getBytes();
        byte[] bytes2 = xmlPlusMime2.getBytes(StandardCharsets.UTF_16);
        byte[] bytes3 = xmlPlusMime3.getBytes();
        byte[] full = append(bytes1, bytes2);
        full = append(full, bytes3);

        InputStream inStream = new BufferedInputStream(new ByteArrayInputStream(full));
        Attachments attachments = new Attachments(inStream, contentTypeString);
        SOAPModelBuilder builder =
                OMXMLBuilderFactory.createSOAPModelBuilder(attachments.getMultipartBody());
        OMElement root = builder.getDocumentElement();
        root.build();
    }

    private byte[] append(byte[] a, byte[] b) {
        byte[] z = new byte[a.length + b.length];
        System.arraycopy(a, 0, z, 0, a.length);
        System.arraycopy(b, 0, z, a.length, b.length);
        return z;
    }
}
