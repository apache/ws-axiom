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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.testutils.suite.TestParameterTarget;
import org.xml.sax.XMLReader;

/**
 * Serializes an {@link OMContainer} by processing the result of {@link
 * OMContainer#getSAXSource(boolean)}.
 */
public class SerializeFromSAXSource extends SerializationStrategy {
    private final boolean cache;

    SerializeFromSAXSource(boolean cache) {
        this.cache = cache;
    }

    @Override
    public void addTestParameters(TestParameterTarget testCase) {
        testCase.addTestParameter("serializationStrategy", "SAXSource");
        testCase.addTestParameter("cache", cache);
    }

    @Override
    public XML serialize(OMContainer container) throws Exception {
        SAXSource source = container.getSAXSource(cache);
        XMLReader xmlReader = source.getXMLReader();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SAXSerializer serializer = new SAXSerializer();
        // A SAXSource has no way to tell its consumer about the encoding of the document.
        // Just set it to UTF-8 to have a well defined encoding.
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputStream(out);
        xmlReader.setContentHandler(serializer);
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", serializer);
        xmlReader.parse(source.getInputSource());
        return new XMLAsByteArray(out.toByteArray());
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
        return false;
    }
}
