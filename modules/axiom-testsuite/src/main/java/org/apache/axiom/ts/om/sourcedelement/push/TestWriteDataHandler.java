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
package org.apache.axiom.ts.om.sourcedelement.push;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;

/**
 * Tests that {@link DataHandlerWriter#writeDataHandler(DataHandler, String, boolean)} creates an
 * {@link OMText} backed by a {@link DataHandler}.
 */
public class TestWriteDataHandler extends AxiomTestCase {
    public TestWriteDataHandler(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        final DataHandler dh = new DataHandler("test", "text/plain");
        OMElement element = factory.createOMElement(new AbstractPushOMDataSource() {
            public void serialize(XMLStreamWriter writer) throws XMLStreamException {
                writer.writeStartElement(null, "root", null);
                try {
                    XMLStreamWriterUtils.writeDataHandler(writer, dh, null, true);
                } catch (IOException ex) {
                    throw new XMLStreamException(ex);
                }
                writer.writeEndElement();
            }
            
            public boolean isDestructiveWrite() {
                return false;
            }
        });
        OMText child = (OMText)element.getFirstOMChild();
        assertTrue(child.isBinary());
        assertSame(dh, child.getDataHandler());
    }
}
