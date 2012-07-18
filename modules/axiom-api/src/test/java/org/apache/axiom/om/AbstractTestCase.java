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

package org.apache.axiom.om;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.dom.DOMResult;

import org.custommonkey.xmlunit.XMLTestCase;
import org.w3c.dom.Document;

/** Abstract base class for test cases. */
public abstract class AbstractTestCase
        extends XMLTestCase {
    public static final String[] soapFiles = {
        "emtyBodymessage.xml",
        "invalidMustUnderstandSOAP12.xml",
        "minimalMessage.xml",
        "OMElementTest.xml",
        "reallyReallyBigMessage.xml",
        "sample1.xml",
        "security2-soap.xml",
        "soap12/message.xml",
        "soap12/roleMessage.xml",
        "soapmessage.xml",
        "soapmessage1.xml",
        "whitespacedMessage.xml"
    };

    public AbstractTestCase() {
        this(null);
    }
    
    /** @param testName  */
    public AbstractTestCase(String testName) {
        super(testName);
    }

    public DataSource getTestResourceDataSource(String relativePath) {
        URL url = AbstractTestCase.class.getClassLoader().getResource(relativePath);
        if (url == null) {
            fail("The test resource " + relativePath + " could not be found");
        }
        return new URLDataSource(url);
    }

    public static InputStream getTestResource(String relativePath) {
        InputStream in = AbstractTestCase.class.getClassLoader().getResourceAsStream(relativePath);
        if (in == null) {
            fail("The test resource " + relativePath + " could not be found");
        }
        return in;
    }
    
    public static OMElement getTestResourceAsElement(OMMetaFactory omMetaFactory, String relativePath) {
        return OMXMLBuilderFactory.createOMBuilder(omMetaFactory.getOMFactory(), getTestResource(relativePath)).getDocumentElement();
    }
    
    public static Document toDocumentWithoutDTD(InputStream in) throws Exception {
        return toDocumentWithoutDTD(in, false);
    }
    
    public static Document toDocumentWithoutDTD(InputStream in, boolean replaceEntityReferences) throws Exception {
        // We use StAX to parse the document because in contrast to DOM, it allows references to undeclared entities.
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.valueOf(replaceEntityReferences));
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventReader reader = inputFactory.createXMLEventReader(in);
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        XMLEventWriter writer = outputFactory.createXMLEventWriter(new DOMResult(document));
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            switch (event.getEventType()) {
                case XMLEvent.DTD:
                    // Skip the DTD
                    break;
                case XMLEvent.ENTITY_REFERENCE:
                    // Replace entity references by elements so that we can compare them (XMLUnit doesn't handle entity references)
                    Attribute attr = eventFactory.createAttribute("name", ((EntityReference)event).getName());
                    writer.add(eventFactory.createStartElement(new QName("entity-reference"), Collections.singleton(attr).iterator(), null));
                    break;
                default:
                    writer.add(event);
            }
        }
        return document;
    }
}

