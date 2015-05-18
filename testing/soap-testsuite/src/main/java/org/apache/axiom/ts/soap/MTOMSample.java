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
package org.apache.axiom.ts.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MTOMSample extends MIMESample {
    public MTOMSample(String name, String contentType) {
        super(name, contentType);
    }
    
    public InputStream getInlinedMessage() {
        try {
            MimeMultipart mp = new MimeMultipart(new DataSource() {
                @Override
                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public String getName() {
                    return null;
                }
                
                @Override
                public InputStream getInputStream() throws IOException {
                    return MTOMSample.this.getInputStream();
                }
                
                @Override
                public String getContentType() {
                    return MTOMSample.this.getContentType();
                }
            });
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            Document rootPart = documentBuilderFactory.newDocumentBuilder().parse(
                    mp.getBodyPart(0).getInputStream());
            process(rootPart.getDocumentElement(), mp);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(rootPart), new StreamResult(baos));
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    
    private void process(Element element, MimeMultipart mp) throws Exception {
        if (element.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")
                && element.getLocalName().equals("Include")) {
            String cid = element.getAttribute("href").substring(4);
            BodyPart part = mp.getBodyPart("<" + cid + ">");
            String base64 = Base64.encodeBase64String(IOUtils.toByteArray(part.getInputStream()));
            element.getParentNode().replaceChild(
                    element.getOwnerDocument().createTextNode(base64), element);
        } else {
            for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child instanceof Element) {
                    process((Element)child, mp);
                }
            }
        }
    }
}
