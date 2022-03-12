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

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.commons.io.output.NullOutputStream;

import javax.activation.DataHandler;

import junit.framework.TestCase;

public class OMOutputTest extends TestCase {
    private OMElement envelope;

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DataHandler dataHandler;

        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMNamespace soap = fac.createOMNamespace(
                "http://schemas.xmlsoap.org/soap/envelope/", "soap");
        envelope = fac.createOMElement("Envelope", soap);
        OMElement body = fac.createOMElement("Body", soap);

        OMNamespace dataName = fac.createOMNamespace(
                "http://www.example.org/stuff", "m");
        OMElement data = fac.createOMElement("data", dataName);

        OMNamespace mime = fac.createOMNamespace(
                "http://www.w3.org/2003/06/xmlmime", "mime");

        OMElement text = fac.createOMElement("name", dataName);
        OMAttribute cType1 = fac.createOMAttribute("contentType", mime,
                                                 "text/plain");
        text.addAttribute(cType1);
        byte[] byteArray = new byte[] { 13, 56, 65, 32, 12, 12, 7, -3, -2, -1,
                98 };
        dataHandler = new DataHandler(new ByteArrayDataSource(byteArray));
        OMText textData = fac.createOMText(dataHandler, false);

        envelope.addChild(body);
        body.addChild(data);
        data.addChild(text);
        text.addChild(textData);
    }

    public void testComplete() throws Exception {
        OMOutputFormat mtomOutputFormat = new OMOutputFormat();
        mtomOutputFormat.setDoOptimize(true);
        OMOutputFormat baseOutputFormat = new OMOutputFormat();
        baseOutputFormat.setDoOptimize(false);

        envelope.serializeAndConsume(NullOutputStream.NULL_OUTPUT_STREAM, baseOutputFormat);
        envelope.serializeAndConsume(NullOutputStream.NULL_OUTPUT_STREAM, mtomOutputFormat);
    }
}