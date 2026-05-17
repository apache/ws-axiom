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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import junit.framework.TestCase;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.io.InstrumentedStream;
import org.apache.axiom.ts.StreamTypeAdapter;
import org.apache.axiom.ts.xml.StreamType;
import org.apache.axiom.ts.xml.XMLSample;

public class TestDetachWithStream extends TestCase {
    @Inject
    private OMFactory factory;

    @Inject
    private StreamType streamType;

    @Inject
    @Named("useStreamSource")
    private boolean useStreamSource;

    @Override
    protected final void runTest() throws Throwable {
        InstrumentedStream stream = streamType.instrumentStream(streamType.getStream(XMLSample.LARGE));
        OMXMLParserWrapper builder;
        if (useStreamSource) {
            builder = OMXMLBuilderFactory.createOMBuilder(factory, streamType.createStreamSource(stream));
        } else {
            builder = streamType.getAdapter(StreamTypeAdapter.class).createOMBuilder(factory, stream);
        }
        long countBeforeDetach = stream.getCount();
        builder.detach();
        assertThat(stream.getCount()).isGreaterThan(countBeforeDetach);
        assertThat(stream.isClosed()).isFalse();
        stream.close();
        builder.getDocument().build();
    }
}
