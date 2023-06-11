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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;

public class FragmentsSample extends TestCase {
    // START SNIPPET: main
    public void processFragments(InputStream in) throws XMLStreamException {
        // Create an XMLStreamReader without building the object model
        XMLStreamReader reader =
                OMXMLBuilderFactory.createOMBuilder(in).getDocument().getXMLStreamReader(false);
        while (reader.hasNext()) {
            if (reader.getEventType() == XMLStreamReader.START_ELEMENT
                    && reader.getName().equals(new QName("tag"))) {
                // A matching START_ELEMENT event was found. Build a corresponding OMElement.
                OMElement element =
                        OMXMLBuilderFactory.createStAXOMBuilder(reader).getDocumentElement();
                // Make sure that all events belonging to the element are consumed so
                // that the XMLStreamReader points to a well defined location (namely the
                // event immediately following the END_ELEMENT event).
                element.build();
                // Now process the element.
                processFragment(element);
            } else {
                reader.next();
            }
        }
    }
    // END SNIPPET: main

    public void processFragment(OMElement element) {
        System.out.println(element.toString());
    }

    public void test() throws XMLStreamException {
        processFragments(FragmentsSample.class.getResourceAsStream("fragments.xml"));
    }
}
