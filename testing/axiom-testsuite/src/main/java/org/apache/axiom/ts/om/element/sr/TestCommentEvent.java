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
package org.apache.axiom.ts.om.element.sr;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.BuilderFactory;
import org.xml.sax.InputSource;

public class TestCommentEvent extends AxiomTestCase {
    private final BuilderFactory builderFactory;
    private final boolean cache;

    public TestCommentEvent(
            OMMetaFactory metaFactory, BuilderFactory builderFactory, boolean cache) {
        super(metaFactory);
        this.builderFactory = builderFactory;
        this.cache = cache;
        builderFactory.addTestParameters(this);
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder =
                builderFactory.getBuilder(
                        metaFactory,
                        new InputSource(new StringReader("<a><!--comment text--></a>")));
        OMElement element = builder.getDocumentElement();
        XMLStreamReader reader = element.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(XMLStreamReader.COMMENT, reader.next());
        assertEquals("comment text", reader.getText());
        assertEquals(
                "comment text",
                new String(
                        reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength()));
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
