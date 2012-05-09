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
package org.apache.axiom.ts.om.sourcedelement;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetXMLStreamReaderWithPushOMDataSource extends AxiomTestCase {
    private final boolean cache;
    
    public TestGetXMLStreamReaderWithPushOMDataSource(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestProperty("cache", String.valueOf(cache));
    }

    protected void runTest() throws Throwable {
        PushOMDataSource ds = new PushOMDataSource();
        OMSourcedElement element = metaFactory.getOMFactory().createOMElement(ds);
        XMLStreamReader reader = element.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(ds.getLocalName(), reader.getLocalName());
        assertEquals(ds.getNamespaceURI(), reader.getNamespaceURI());
        assertEquals(ds.getPrefix(), reader.getPrefix());
        // Since the OMDataSource is push-only, getXMLStreamReader will cause expansion regardless
        // of the value of the cache flag
        assertTrue(element.isExpanded());
        // In addition, if the element is expanded, it will always be complete
        assertTrue(element.isComplete());
    }
}
