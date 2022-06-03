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

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMContainer#getXMLStreamReaderWithoutCaching()} correctly generated events for
 * an element that has been partially built. This is a regression test for AXIOM-393.
 */
public class TestGetXMLStreamReaderWithoutCachingPartiallyBuilt extends AxiomTestCase {
    public TestGetXMLStreamReaderWithoutCachingPartiallyBuilt(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        // Note: the problem described in AXIOM-393 specifically occurred with descendants
        //       having the same name as the root element
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(),
                                new StringReader(
                                        "<element><element><element/><element/></element></element>"))
                        .getDocumentElement();

        // Partially build the tree
        root.getFirstElement().getFirstElement();

        XMLStreamReader reader = root.getXMLStreamReaderWithoutCaching();
        int depth = 0;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamReader.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamReader.END_ELEMENT) {
                depth--;
            }
        }
        assertEquals(0, depth);
    }
}
