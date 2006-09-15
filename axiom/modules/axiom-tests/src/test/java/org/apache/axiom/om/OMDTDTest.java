package org.apache.axiom.om;

import junit.framework.TestCase;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class OMDTDTest extends TestCase {

    private OMDocument document;

    protected void setUp() throws Exception {
        try {
            StAXOMBuilder stAXOMBuilder = new StAXOMBuilder("test-resources/xml/dtd.xml");
            stAXOMBuilder.setDoDebug(true);
            document = this.document = stAXOMBuilder.getDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testDTDSerialization() {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.serialize(baos);
            String serializedString = new String(baos.toByteArray());

            assertTrue(serializedString.indexOf("<!ENTITY foo \"bar\">" ) > -1);
            assertTrue(serializedString.indexOf("<!ENTITY bar \"foo\">" ) > -1);
            assertTrue(serializedString.indexOf("<feed xmlns=\"http://www.w3.org/2005/Atom\">" ) > -1);
        } catch (XMLStreamException e) {
            fail("Bug in serializing OMDocuments which have DTDs, text and a document element");
        }
    }
}
