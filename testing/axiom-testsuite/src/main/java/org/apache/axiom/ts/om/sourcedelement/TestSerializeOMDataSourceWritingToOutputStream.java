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
package org.apache.axiom.ts.om.sourcedelement;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.dimension.serialization.SerializeToOutputStream;

/**
 * Tests serialization with an {@link OMDataSource} that uses {@link
 * MTOMXMLStreamWriter#getOutputStream()}.
 */
public class TestSerializeOMDataSourceWritingToOutputStream extends AxiomTestCase {
    private static final class OMDataSourceImpl extends AbstractPushOMDataSource {
        private boolean outputStreamUsed;

        @Override
        public void serialize(XMLStreamWriter writer) throws XMLStreamException {
            OutputStream out;
            String encoding;
            if (writer instanceof MTOMXMLStreamWriter) {
                MTOMXMLStreamWriter writer2 = (MTOMXMLStreamWriter) writer;
                out = writer2.getOutputStream();
                encoding = writer2.getCharSetEncoding();
            } else {
                out = null;
                encoding = null;
            }
            if (out != null) {
                try {
                    out.write("<test xmlns=\"urn:test\"/>".getBytes(encoding));
                } catch (IOException ex) {
                    throw new XMLStreamException(ex);
                }
                outputStreamUsed = true;
            } else {
                writer.writeStartElement("", "test", "urn:test");
                writer.writeNamespace("", "urn:test");
                writer.writeEndElement();
            }
        }

        @Override
        public boolean isDestructiveWrite() {
            return false;
        }

        boolean isOutputStreamUsed() {
            return outputStreamUsed;
        }
    }

    private final SerializationStrategy serializationStrategy;
    private final boolean serializeParent;

    public TestSerializeOMDataSourceWritingToOutputStream(
            OMMetaFactory metaFactory,
            SerializationStrategy serializationStrategy,
            boolean serializeParent) {
        super(metaFactory);
        this.serializationStrategy = serializationStrategy;
        this.serializeParent = serializeParent;
        serializationStrategy.addTestParameters(this);
        addTestParameter("serializeParent", serializeParent);
    }

    @Override
    protected void runTest() throws Throwable {
        OMDataSourceImpl ds = new OMDataSourceImpl();
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element = factory.createOMElement(ds);
        OMElement elementToSerialize;
        if (serializeParent) {
            OMElement parent = factory.createOMElement("root", null);
            parent.addChild(element);
            elementToSerialize = parent;
        } else {
            elementToSerialize = element;
        }
        assertAbout(xml())
                .that(serializationStrategy.serialize(elementToSerialize).getInputSource())
                .hasSameContentAs(
                        serializeParent
                                ? "<root><test xmlns='urn:test'/></root>"
                                : "<test xmlns='urn:test'/>");
        assertThat(ds.isOutputStreamUsed())
                .isEqualTo(serializationStrategy instanceof SerializeToOutputStream);
    }
}
