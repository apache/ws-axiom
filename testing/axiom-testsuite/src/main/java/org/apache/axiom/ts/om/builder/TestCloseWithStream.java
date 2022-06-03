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
package org.apache.axiom.ts.om.builder;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.io.InstrumentedStream;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.StreamTypeAdapter;
import org.apache.axiom.ts.xml.StreamType;
import org.apache.axiom.ts.xml.XMLSample;

public class TestCloseWithStream extends AxiomTestCase {
    private final StreamType streamType;

    public TestCloseWithStream(OMMetaFactory metaFactory, StreamType streamType) {
        super(metaFactory);
        this.streamType = streamType;
        addTestParameter("type", streamType.getType().getSimpleName());
    }

    @Override
    protected void runTest() throws Throwable {
        InstrumentedStream in = streamType.instrumentStream(streamType.getStream(XMLSample.SIMPLE));
        try {
            OMXMLParserWrapper builder =
                    streamType
                            .getAdapter(StreamTypeAdapter.class)
                            .createOMBuilder(metaFactory.getOMFactory(), in);
            builder.getDocument().build();
            builder.close();
            // OMXMLParserWrapper#close() does _not_ close the underlying input stream
            assertFalse(in.isClosed());
        } finally {
            in.close();
        }
    }
}
