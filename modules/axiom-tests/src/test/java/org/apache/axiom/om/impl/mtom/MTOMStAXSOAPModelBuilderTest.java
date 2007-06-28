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

package org.apache.axiom.om.impl.mtom;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.builder.MTOMStAXSOAPModelBuilder;

import javax.activation.DataHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.util.Iterator;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.net.URLEncoder;

public class MTOMStAXSOAPModelBuilderTest extends AbstractTestCase {

    /** @param testName  */
    public MTOMStAXSOAPModelBuilderTest(String testName) {
        super(testName);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateOMElement() throws Exception {
        String contentTypeString =
                "multipart/Related; charset=\"UTF-8\"; type=\"application/xop+xml\"; boundary=\"----=_AxIs2_Def_boundary_=42214532\"; start=\"SOAPPart\"";
        String inFileName = "mtom/MTOMBuilderTestIn.txt";
        InputStream inStream = new FileInputStream(getTestResourceFile(inFileName));
        Attachments attachments = new Attachments(inStream, contentTypeString);
        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new BufferedReader(new InputStreamReader(attachments
                        .getSOAPPartInputStream())));
        OMXMLParserWrapper builder = new MTOMStAXSOAPModelBuilder(reader, attachments,
                                               SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        OMElement root = builder.getDocumentElement();
        OMElement body = (OMElement) root.getFirstOMChild();
        OMElement data = (OMElement) body.getFirstOMChild();

        Iterator childIt = data.getChildren();
        OMElement child = (OMElement) childIt.next();
        OMText blob = (OMText) child.getFirstOMChild();
        /*
         * Following is the procedure the user has to follow to read objects in
         * OBBlob User has to know the object type & whether it is serializable.
         * If it is not he has to use a Custom Defined DataSource to get the
         * Object.
         */
        byte[] expectedObject = new byte[] { 13, 56, 65, 32, 12, 12, 7, -3, -2,
                -1, 98 };
        DataHandler actualDH;
        actualDH = (DataHandler) blob.getDataHandler();
        //ByteArrayInputStream object = (ByteArrayInputStream) actualDH
        //.getContent();
        //byte[] actualObject= null;
        //  object.read(actualObject,0,10);

        //  assertEquals("Object check", expectedObject[5],actualObject[5] );
    }

    public void testUTF16MTOMMessage() throws Exception {
        String contentTypeString =
                "multipart/Related; charset=\"UTF-8\"; type=\"application/xop+xml\"; boundary=\"----=_AxIs2_Def_boundary_=42214532\"; start=\"SOAPPart\"";
        String originalCID = "1.urn:uuid:A3ADBAEE51A1A87B2A11443668160994@apache.org";
        String encodedCID = URLEncoder.encode(originalCID, "UTF-16");
        String xmlPlusMime1 = "------=_AxIs2_Def_boundary_=42214532\r\n" +
                "Content-Type: application/xop+xml; charset=UTF-16\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n" +
                "Content-ID: SOAPPart\r\n" +
                "\r\n";
        String xmlPlusMime2 = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body><m:data xmlns:m=\"http://www.example.org/stuff\"><m:name m:contentType=\"text/plain\"><xop:Include xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:" + encodedCID + "\"></xop:Include></m:name></m:data></soapenv:Body></soapenv:Envelope>\r\n";
        String xmlPlusMime3 = "\r\n------=_AxIs2_Def_boundary_=42214532\r\n" +
                "Content-Transfer-Encoding: binary\r\n" +
                "Content-ID: " + originalCID + "\r\n" +
                "\r\n" +
                "Foo Bar\r\n" +
                "------=_AxIs2_Def_boundary_=42214532--\r\n";
        byte[] bytes1 = xmlPlusMime1.getBytes();
        byte[] bytes2 = xmlPlusMime2.getBytes("UTF-16");
        byte[] bytes3 = xmlPlusMime3.getBytes();
        byte[] full = append(bytes1, bytes2);
        full = append(full, bytes3);
        
        InputStream inStream = new BufferedInputStream(new ByteArrayInputStream(full));
        Attachments attachments = new Attachments(inStream, contentTypeString);
        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(attachments
                        .getSOAPPartInputStream(),"UTF-16");
        MTOMStAXSOAPModelBuilder builder = new MTOMStAXSOAPModelBuilder(reader, attachments,
                                               SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        OMElement root = builder.getDocumentElement();
        root.build();
        System.out.println(root.toString());
    }

    private byte[] append(byte[] a, byte[] b) {
        byte[] z = new byte[a.length + b.length];
        System.arraycopy(a, 0, z, 0, a.length);
        System.arraycopy(b, 0, z, a.length, b.length);
        return z;
    }
}