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
package org.apache.axiom.samples;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.mime.PartBlob;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.testutils.PortAllocator;
import org.apache.commons.io.IOUtils;

import jakarta.xml.ws.Endpoint;

public class MTOMSample extends TestCase {
    // START SNIPPET: retrieveContent
    public void retrieveContent(URL serviceURL, String id, OutputStream result) throws Exception {
        // Build the SOAP request
        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope request = soapFactory.getDefaultEnvelope();
        OMElement retrieveContent = soapFactory.createOMElement(
                new QName("urn:test", "retrieveContent"), request.getBody());
        OMElement fileId = soapFactory.createOMElement(new QName("fileId"), retrieveContent);
        fileId.setText(id);
        
        // Use the java.net.URL API to connect to the service and send the request
        URLConnection connection = serviceURL.openConnection();
        connection.setDoOutput(true);
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setCharSetEncoding("UTF-8");
        connection.addRequestProperty("Content-Type", format.getContentType());
        OutputStream out = connection.getOutputStream();
        request.serialize(out, format);
        out.close();
        
        // Get the SOAP response
        InputStream in = connection.getInputStream();
        MultipartBody multipartBody = MultipartBody.builder()
                .setInputStream(in)
                .setContentType(connection.getContentType())
                .build();
        SOAPEnvelope response = OMXMLBuilderFactory.createSOAPModelBuilder(multipartBody).getSOAPEnvelope();
        OMElement retrieveContentResponse = response.getBody().getFirstElement();
        OMElement content = retrieveContentResponse.getFirstElement();
        // Extract the Blob representing the optimized binary data
        Blob blob = ((OMText)content.getFirstOMChild()).getBlob();
        // Stream the content of the MIME part
        InputStream contentStream = ((PartBlob)blob).getPart().getInputStream(false);
        // Write the content to the result stream
        IOUtils.copy(contentStream, result);
        contentStream.close();
        
        in.close();
    }
    // END SNIPPET: retrieveContent
    
    public void test() throws Exception {
        int port = PortAllocator.allocatePort();
        Endpoint endpoint = Endpoint.publish("http://localhost:" + port + "/mtom", new MTOMService());
        retrieveContent(new URL("http://localhost:" + port + "/mtom"), "G87ZX20047", System.out);
        endpoint.stop();
    }
}
