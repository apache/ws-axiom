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
package org.apache.axiom.ts.om.element;

import javax.xml.transform.sax.SAXResult;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.ts.AxiomTestCase;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Tests that for an {@link OMElement}, a {@link LexicalHandler#startDTD(String, String, String)}
 * event sent to the {@link LexicalHandler} linked to the {@link SAXResult} object returned by
 * {@link OMContainer#getSAXResult()} is silently ignored.
 */
public class TestGetSAXResultWithDTD extends AxiomTestCase {
    public TestGetSAXResultWithDTD(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement root = metaFactory.getOMFactory().createOMElement("root", null);
        SAXResult result = root.getSAXResult();
        LexicalHandler lexicalHandler = result.getLexicalHandler();
        ContentHandler contentHandler = result.getHandler();
        contentHandler.startDocument();
        lexicalHandler.startDTD("test", null, "my.dtd");
        lexicalHandler.endDTD();
        contentHandler.startElement("", "test", "test", new AttributesImpl());
        contentHandler.endElement("", "test", "test");
        contentHandler.endDocument();
        OMNode child = root.getFirstOMChild();
        assertTrue(child instanceof OMElement);
        assertEquals("test", ((OMElement) child).getLocalName());
    }
}
