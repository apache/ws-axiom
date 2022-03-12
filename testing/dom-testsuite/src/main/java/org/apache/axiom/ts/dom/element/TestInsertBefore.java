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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestInsertBefore extends DOMTestCase {
    public TestInsertBefore(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        
        Element parent1 = document.createElementNS(null, "parent1");
        Element a = document.createElementNS(null, "a");
        Element b = document.createElementNS(null, "b");
        Element c = document.createElementNS(null, "c");
        parent1.appendChild(a);
        parent1.appendChild(b);
        parent1.appendChild(c);
        
        Element parent2 = document.createElementNS(null, "parent1");
        Element d = document.createElementNS(null, "d");
        Element e = document.createElementNS(null, "e");
        parent2.appendChild(d);
        parent2.appendChild(e);
        
        parent2.insertBefore(b, e);
        
        NodeList children = parent1.getChildNodes();
        assertEquals(2, children.getLength());
        assertSame(a, children.item(0));
        assertSame(c, children.item(1));
        
        children = parent2.getChildNodes();
        assertEquals(3, children.getLength());
        assertSame(d, children.item(0));
        assertSame(b, children.item(1));
        assertSame(e, children.item(2));
    }
}
