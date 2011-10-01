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
package org.apache.axiom.om.impl.dom;

import java.io.ByteArrayOutputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMElementTestBase;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.dom.factory.OMDOMFactory;
import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.w3c.dom.Text;

public class ElementImplTest extends OMElementTestBase {
    public ElementImplTest() {
        super(new OMDOMMetaFactory());
    }

    public void testSerialize() throws Exception {
        OMDOMFactory factory = new OMDOMFactory();
        String localName = "TestLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        String tempText = "The quick brown fox jumps over the lazy dog";
        String textToAppend = " followed by another";

        OMElement elem = factory.createOMElement(localName, namespace, prefix);
        OMText textNode = factory.createOMText(elem, tempText);

        ((Text) textNode).appendData(textToAppend);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        elem.serialize(baos);
        String xml = new String(baos.toByteArray());
        assertEquals("Incorrect serialized xml", 0, xml.indexOf("<axis2:TestLocalName"));
    }
}
