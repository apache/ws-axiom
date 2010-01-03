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

import org.apache.axiom.om.util.AXIOMUtil;

public class OMNodeTestBase extends AbstractTestCase {
    protected final OMMetaFactory omMetaFactory;

    public OMNodeTestBase(OMMetaFactory omMetaFactory) {
        this.omMetaFactory = omMetaFactory;
    }

    public void testInsertSiblingAfter() {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMElement parent = fac.createOMElement("test", null);
        OMText text1 = fac.createOMText("text1");
        OMText text2 = fac.createOMText("text2");
        parent.addChild(text1);
        text1.insertSiblingAfter(text2);
        assertSame(parent, text2.getParent());
    }
    
    public void testInsertSiblingBefore() {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMElement parent = fac.createOMElement("test", null);
        OMText text1 = fac.createOMText("text1");
        OMText text2 = fac.createOMText("text2");
        parent.addChild(text1);
        text1.insertSiblingBefore(text2);
        assertSame(parent, text2.getParent());
        assertSame(text2, parent.getFirstOMChild());
    }
    
    // Regression test for WSCOMMONS-337
    public void testInsertSiblingAfterLastChild() throws Exception {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("http://www.testuri.com","ns");
        OMElement parent = fac.createOMElement("parent", ns);
        
        // Create three OMElements
        OMElement c1 = fac.createOMElement("c1", ns);
        OMElement c2 = fac.createOMElement("c2", ns);
        OMElement c3 = fac.createOMElement("c3", ns);

        // Add c1 to parent using parent.addChild()
        parent.addChild(c1);
        // Add c2 to c1 as a sibling after
        c1.insertSiblingAfter(c2);
        // Now add c3 to parent using parent.addChild()
        parent.addChild(c3);
        assertXMLEqual("<ns:parent xmlns:ns=\"http://www.testuri.com\">" +
                "<ns:c1 /><ns:c2 /><ns:c3 /></ns:parent>", parent.toString());
    }

    public void testInsertSiblingAfterOnOrphan() {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMText text1 = fac.createOMText("text1");
        OMText text2 = fac.createOMText("text2");
        try {
            text1.insertSiblingAfter(text2);
            fail("Expected OMException because node has no parent");
        } catch (OMException ex) {
            // Expected
        }
    }
    
    public void testInsertSiblingBeforeOnOrphan() {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMText text1 = fac.createOMText("text1");
        OMText text2 = fac.createOMText("text2");
        try {
            text1.insertSiblingBefore(text2);
            fail("Expected OMException because node has no parent");
        } catch (OMException ex) {
            // Expected
        }
    }
    
    private void testDetach(boolean build) throws Exception {
        OMElement root = AXIOMUtil.stringToOM(omMetaFactory.getOMFactory(), "<root><a/><b/><c/></root>");
        if (build) {
            root.build();
        } else {
            assertFalse(root.isComplete());
        }
        OMElement a = (OMElement)root.getFirstOMChild();
        assertEquals("a", a.getLocalName());
        OMElement b = (OMElement)a.getNextOMSibling();
        assertEquals("b", b.getLocalName());
        b.detach();
        assertNull(b.getParent());
        OMElement c = (OMElement)a.getNextOMSibling();
        assertEquals("c", c.getLocalName());
        assertSame(c, a.getNextOMSibling());
        assertSame(a, c.getPreviousOMSibling());
        root.close(false);
    }
    
    public void testDetachWithBuild() throws Exception {
        testDetach(true);
    }
    
    public void testDetachWithoutBuild() throws Exception {
        testDetach(false);
    }


}
