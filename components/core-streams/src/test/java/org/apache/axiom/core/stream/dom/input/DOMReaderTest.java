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
package org.apache.axiom.core.stream.dom.input;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMReaderTest {
    @Test
    public void testSimpleDocument() throws Exception {
        Document document = DOMImplementation.XERCES.newDocument();
        Element root = document.createElementNS("urn:test", "p:root");
        document.appendChild(root);
        root.setTextContent("test");
        XmlHandler handler = mock(XmlHandler.class);
        DOMReader reader = new DOMReader(handler, document, false);

        assertThat(reader.proceed()).isFalse();
        verify(handler).startDocument(null, "1.0", null, false);
        verifyNoMoreInteractions(handler);

        assertThat(reader.proceed()).isFalse();
        verify(handler).startElement("urn:test", "root", "p");
        verify(handler).attributesCompleted();
        verifyNoMoreInteractions(handler);

        assertThat(reader.proceed()).isFalse();
        verify(handler).processCharacterData("test", false);
        verifyNoMoreInteractions(handler);

        assertThat(reader.proceed()).isFalse();
        verify(handler).endElement();
        verifyNoMoreInteractions(handler);

        assertThat(reader.proceed()).isTrue();
        verify(handler).completed();
        verifyNoMoreInteractions(handler);
    }
}
