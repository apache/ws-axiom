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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetXMLStreamReaderOnNonRootElement extends AxiomTestCase {
    private final boolean cache;
    
    public TestGetXMLStreamReaderOnNonRootElement(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestProperty("cache", Boolean.toString(cache));
    }

    protected void runTest() throws Throwable {
        OMElement root = AXIOMUtil.stringToOM(metaFactory.getOMFactory(),
                "<a><b><c/></b></a>");
        OMElement child = (OMElement)root.getFirstOMChild();
        XMLStreamReader stream = cache ? child.getXMLStreamReader()
                : child.getXMLStreamReaderWithoutCaching();
        assertEquals(XMLStreamReader.START_DOCUMENT, stream.getEventType());
        assertEquals(XMLStreamReader.START_ELEMENT, stream.next());
        assertEquals("b", stream.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, stream.next());
        assertEquals("c", stream.getLocalName());
        assertEquals(XMLStreamReader.END_ELEMENT, stream.next());
        assertEquals(XMLStreamReader.END_ELEMENT, stream.next());
        assertEquals(XMLStreamReader.END_DOCUMENT, stream.next());
        root.close(false);
    }
}
