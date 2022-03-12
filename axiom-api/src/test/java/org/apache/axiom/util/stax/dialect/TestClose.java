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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.testutils.io.InstrumentedStream;
import org.apache.axiom.ts.xml.StreamType;
import org.apache.axiom.ts.xml.XMLSample;

/**
 * Tests that {@link XMLStreamReader#close()} doesn't close the underlying stream.
 */
public class TestClose extends DialectTestCase {
    private final StreamType streamType;
    
    public TestClose(StAXImplementationAdapter staxImpl, StreamType streamType) {
        super(staxImpl);
        this.streamType = streamType;
        addTestParameter("type", streamType.getType().getSimpleName());
    }

    @Override
    protected void runTest() throws Throwable {
        InstrumentedStream in = streamType.instrumentStream(streamType.getStream(XMLSample.SIMPLE));
        XMLStreamReader reader = streamType.createXMLStreamReader(staxImpl.newNormalizedXMLInputFactory(), in);
        reader.close();
        assertFalse(in.isClosed());
    }
}
