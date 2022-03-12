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
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.output.NullOutputStream;

/**
 * Tests that {@link XMLStreamWriter#writeDefaultNamespace(String)} updates the namespace context
 * (i.e. behaves as if it implicitly calls {@link XMLStreamWriter#setDefaultNamespace(String)}).
 */
public class TestGetPrefixAfterWriteDefaultNamespace extends DialectTestCase {
    public TestGetPrefixAfterWriteDefaultNamespace(StAXImplementationAdapter staxImpl) {
        super(staxImpl);
    }

    @Override
    protected void runTest() throws Throwable {
        XMLStreamWriter writer = staxImpl.newNormalizedXMLOutputFactory().createXMLStreamWriter(NullOutputStream.NULL_OUTPUT_STREAM);
        writer.writeStartElement("ns1", "root", "urn:ns1");
        writer.writeDefaultNamespace("urn:ns2");
        assertEquals("", writer.getPrefix("urn:ns2"));
    }
}
