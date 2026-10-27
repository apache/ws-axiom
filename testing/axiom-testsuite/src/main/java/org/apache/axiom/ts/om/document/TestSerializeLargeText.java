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
package org.apache.axiom.ts.om.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that serializing an {@link OMDocument} with a large text node to an {@link
 * java.io.OutputStream} works. The serializer splits text into runs of at most 4096 clean
 * characters; if such a run is written while the internal buffer of the writer already contains
 * data (e.g. the replacement for a character such as {@code &}, or a preceding text node), then
 * the writer must flush the buffer correctly. UTF-8 and ISO-8859-1 are handled by dedicated
 * writers; the other encodings use a generic {@code CharsetEncoder} based writer, which is where
 * the original bug was.
 */
public class TestSerializeLargeText implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {
        for (String encoding : new String[] {null, "iso-8859-1", "UTF-16", "windows-1252"}) {
            for (int length : new int[] {4093, 4096, 5000, 12000}) {
                for (String lead : new String[] {"", "&", "<>&"}) {
                    for (boolean separateNode : new boolean[] {false, true}) {
                        for (boolean nested : new boolean[] {false, true}) {
                            check(encoding, length, lead, separateNode, nested);
                        }
                    }
                }
            }
        }
    }

    private void check(String encoding, int length, String lead, boolean separateNode, boolean nested)
            throws Exception {
        StringBuilder clean = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            clean.append((char) ('a' + i % 26));
        }

        OMDocument document = factory.createOMDocument();
        OMElement root = factory.createOMElement("root", null, document);
        OMElement parent = root;
        if (nested) {
            parent = factory.createOMElement("child", null, root);
        }
        String expected;
        if (separateNode) {
            // Two adjacent text nodes; no flush occurs between them.
            factory.createOMText(parent, lead + "some text");
            factory.createOMText(parent, clean.toString());
            expected = lead + "some text" + clean;
        } else {
            factory.createOMText(parent, lead + clean);
            expected = lead + clean;
        }

        OMOutputFormat format = new OMOutputFormat();
        if (encoding != null) {
            format.setCharSetEncoding(encoding);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.serialize(baos, format);

        OMElement result = OMXMLBuilderFactory.createOMBuilder(factory, new ByteArrayInputStream(baos.toByteArray()))
                .getDocumentElement();
        OMElement textParent = nested ? (OMElement) result.getFirstOMChild() : result;
        assertThat(textParent.getText()).isEqualTo(expected);
    }
}
