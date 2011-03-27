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

public class TestGetXMLStreamReaderCommentEvent extends AxiomTestCase {
    public TestGetXMLStreamReaderCommentEvent(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMElement element = AXIOMUtil.stringToOM(metaFactory.getOMFactory(),
                "<a><!--comment text--></a>");
        XMLStreamReader reader = element.getXMLStreamReader();
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(XMLStreamReader.COMMENT, reader.next());
        assertEquals("comment text", reader.getText());
        assertEquals("comment text", new String(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength()));
        StringBuffer text = new StringBuffer();
        char[] buf = new char[5];
        for (int sourceStart = 0; ; sourceStart += buf.length) {
            int nCopied = reader.getTextCharacters(sourceStart, buf, 0, buf.length);
            text.append(buf, 0, nCopied);
            if (nCopied < buf.length) {
                break;
            }
        }
        assertEquals("comment text", text.toString());
        element.close(false);
    }
}
