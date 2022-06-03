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
package org.apache.axiom.ts.om.element.sr;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetElementText extends AxiomTestCase {
    public TestGetElementText(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();

        OMNamespace namespace = factory.createOMNamespace("http://testuri.org", "test");
        OMElement documentElement = factory.createOMElement("DocumentElement", namespace);
        factory.createOMText(documentElement, "this is a TEXT");
        factory.createOMComment(documentElement, "this is a comment");
        factory.createOMText(documentElement, "this is a TEXT block 2");

        XMLStreamReader xmlStreamReader = documentElement.getXMLStreamReader();
        // move to the Start_Element
        while (xmlStreamReader.getEventType() != XMLStreamReader.START_ELEMENT) {
            xmlStreamReader.next();
        }

        String elementText = xmlStreamReader.getElementText();
        assertEquals("this is a TEXTthis is a TEXT block 2", elementText);
    }
}
