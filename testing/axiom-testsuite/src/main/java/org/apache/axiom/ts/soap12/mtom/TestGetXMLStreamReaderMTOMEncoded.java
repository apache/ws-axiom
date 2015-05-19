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
package org.apache.axiom.ts.soap12.mtom;

import java.io.InputStream;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.soap.MTOMSample;
import org.apache.axiom.util.stax.xop.XOPEncodedStream;
import org.apache.axiom.util.stax.xop.XOPUtils;

public class TestGetXMLStreamReaderMTOMEncoded extends AxiomTestCase {
    private final static QName XOP_INCLUDE = 
            new QName("http://www.w3.org/2004/08/xop/include", "Include");

    private final boolean cache;
    
    public TestGetXMLStreamReaderMTOMEncoded(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    protected void runTest() throws Throwable {
        InputStream inStream = MTOMSample.SAMPLE2.getInputStream();
        Attachments attachments = new Attachments(inStream, MTOMSample.SAMPLE2.getContentType());
        OMElement root = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, attachments).getDocumentElement();
        
        // Use tree as input to XMLStreamReader
        // Issue XOP:Include events for optimized MTOM text nodes
        XOPEncodedStream xopEncodedStream = XOPUtils.getXOPEncodedStream(root.getXMLStreamReader(cache));
        XMLStreamReader xmlStreamReader = xopEncodedStream.getReader();
        
        DataHandler dh = null;
        while(xmlStreamReader.hasNext() && dh == null) {
            xmlStreamReader.next();
            if (xmlStreamReader.isStartElement()) {
                QName qName =xmlStreamReader.getName();
                if (XOP_INCLUDE.equals(qName)) {
                    String hrefValue = xmlStreamReader.getAttributeValue("", "href");
                    if (hrefValue != null) {
                        dh = xopEncodedStream.getMimePartProvider().getDataHandler(
                                XOPUtils.getContentIDFromURL(hrefValue));
                    }
                }
            }
        }
        assertTrue(dh != null);   
        
        // Make sure next event is an an XOP_Include END element
        xmlStreamReader.next();
        assertTrue(xmlStreamReader.isEndElement());
        assertTrue(XOP_INCLUDE.equals(xmlStreamReader.getName()));
        
        // Make sure the next event is the end tag of name
        xmlStreamReader.next();
        assertTrue(xmlStreamReader.isEndElement());
        assertTrue("name".equals(xmlStreamReader.getLocalName()));
    }
}
