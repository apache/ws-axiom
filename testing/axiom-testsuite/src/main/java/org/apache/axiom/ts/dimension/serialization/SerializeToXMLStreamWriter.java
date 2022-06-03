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
package org.apache.axiom.ts.dimension.serialization;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.stax.StAX;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Serializes an {@link OMContainer} using {@link OMContainer#serialize(XMLStreamWriter)} or {@link
 * OMContainer#serializeAndConsume(XMLStreamWriter)}.
 */
public class SerializeToXMLStreamWriter extends SerializationStrategy {
    private final boolean cache;

    SerializeToXMLStreamWriter(boolean cache) {
        this.cache = cache;
    }

    @Override
    public void addTestParameters(MatrixTestCase testCase) {
        testCase.addTestParameter("serializationStrategy", "XMLStreamWriter");
        testCase.addTestParameter("cache", cache);
    }

    @Override
    public XML serialize(OMContainer container) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String encoding = null;
        // Since Axiom has no way of knowing the encoding used by the XMLStreamWriter,
        // it will just keep the original one when writing the XML declaration. Adjust
        // the output encoding so that it will match the encoding in the XML declaration.
        if (container instanceof OMDocument) {
            encoding = ((OMDocument) container).getXMLEncoding();
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        XMLStreamWriter writer = StAX.createXMLStreamWriter(baos, encoding);
        if (cache) {
            container.serialize(writer);
        } else {
            container.serializeAndConsume(writer);
        }
        writer.close();
        return new XMLAsByteArray(baos.toByteArray());
    }

    @Override
    public boolean isPush() {
        return true;
    }

    @Override
    public boolean isCaching() {
        return cache;
    }

    @Override
    public boolean supportsInternalSubset() {
        return true;
    }
}
