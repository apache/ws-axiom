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

import javax.activation.DataHandler;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;

public class OMTextTestBase extends AbstractTestCase {
    static class TestDataHandlerProvider implements DataHandlerProvider {
        private DataHandler dh;
        
        public DataHandler getDataHandler() {
            if (dh == null) {
                dh = new DataHandler("Data", "text/plain");
            }
            return dh;
        }
        
        public boolean isDataHandlerCreated() {
            return dh != null;
        }
    }
    
    protected final OMMetaFactory omMetaFactory;

    public OMTextTestBase(OMMetaFactory omMetaFactory) {
        this.omMetaFactory = omMetaFactory;
    }

    public void testCreateText() {
        OMFactory factory = omMetaFactory.getOMFactory();
        OMNamespace namespace =
                factory.createOMNamespace("http://www.apache.org/~chinthaka", "myhome");
        OMElement omElement = factory.createOMElement("chinthaka",
                                                        namespace);
        String text = "sampleText";
        OMText omText = factory.createOMText(omElement, text);
        assertTrue("Programatically created OMText should have done = true ",
                   omText.isComplete());
        assertTrue(
                "Programatically created OMText should have correct text value ",
                text.equals(omText.getText()));

    }
    
    public void testCreateFromDataHandlerProvider() throws Exception {
        TestDataHandlerProvider prov = new TestDataHandlerProvider();
        OMFactory factory = omMetaFactory.getOMFactory();
        OMText text = factory.createOMText(null, prov, true);
        assertFalse(prov.isDataHandlerCreated());
        assertEquals(((DataHandler)text.getDataHandler()).getContent(), "Data");
        assertTrue(prov.isDataHandlerCreated());
    }

    public void testSetText() {
        OMFactory factory = omMetaFactory.getOMFactory();
        String localName = "TestLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        String tempText = "The quick brown fox jumps over the lazy dog";

        OMElement elem = factory.createOMElement(localName, namespace, prefix);
        OMText textNode = factory.createOMText(elem, tempText);

        assertEquals("Text value mismatch", tempText, textNode.getText());
    }
}
