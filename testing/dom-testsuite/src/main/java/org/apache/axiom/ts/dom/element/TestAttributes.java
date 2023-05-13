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
package org.apache.axiom.ts.dom.element;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class TestAttributes extends DOMTestCase {
    public TestAttributes(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc =
                dbf.newDocumentBuilder()
                        .parse(TestAttributes.class.getResourceAsStream("attributetest.xml"));

        // Check whether body has attributes
        Element bodyElement = doc.getDocumentElement();
        assertTrue(bodyElement.hasAttributes());

        Element directionResponse =
                (Element) bodyElement.getElementsByTagName("GetDirectionsResponse").item(0);
        assertTrue(directionResponse.hasAttributes());

        NamedNodeMap attributes = directionResponse.getAttributes();
        Attr attr = (Attr) attributes.item(0);
        assertEquals("xmlns", attr.getName());
        assertEquals("http://www.example.org/webservices/", attr.getValue());

        Element directionResult =
                (Element) bodyElement.getElementsByTagName("GetDirectionsResult").item(0);
        assertFalse(directionResult.hasAttributes());

        Element drivingDirection =
                (Element) directionResult.getElementsByTagName("drivingdirections").item(0);
        assertTrue(drivingDirection.hasAttributes());

        attributes = drivingDirection.getAttributes();
        attr = (Attr) attributes.item(0);
        assertEquals("xmlns", attr.getName());
        assertEquals("", attr.getValue());

        Element route = (Element) drivingDirection.getElementsByTagName("route").item(0);
        assertTrue(route.hasAttributes());

        attributes = route.getAttributes();
        attr = (Attr) attributes.item(0);
        assertEquals("distanceToTravel", attr.getName());
        assertEquals("500m", attr.getValue());

        attr = (Attr) attributes.item(1);
        assertEquals("finalStep", attr.getName());
        assertEquals("false", attr.getValue());

        attr = (Attr) attributes.item(2);
        assertEquals("id", attr.getName());
        assertEquals("0", attr.getValue());
    }
}
