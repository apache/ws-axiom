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
package org.apache.axiom.ts.om.text;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.util.xml.stream.XMLEventUtils;

public class TestSerialize extends AxiomTestCase {
    private final int type;

    public TestSerialize(OMMetaFactory metaFactory, int type) {
        super(metaFactory);
        this.type = type;
        addTestParameter("type", XMLEventUtils.getEventTypeString(type));
    }

    @Override
    protected void runTest() throws Throwable {
        OMText text = metaFactory.getOMFactory().createOMText("test", type);
        XMLStreamWriter writer = mock(XMLStreamWriter.class);
        text.serialize(writer);
        if (type == OMNode.CDATA_SECTION_NODE) {
            verify(writer).writeCData(text.getText());
        } else {
            verify(writer).writeCharacters(text.getText());
        }
        verify(writer, atMost(1)).flush();
        verifyNoMoreInteractions(writer);
    }
}
