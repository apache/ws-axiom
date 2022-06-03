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
package org.apache.axiom.ts.om.container;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.dimension.serialization.XML;
import org.apache.axiom.ts.om.XMLSampleAdapter;
import org.apache.axiom.ts.xml.XMLSample;
import org.apache.commons.io.IOUtils;
import org.xml.sax.InputSource;

public class TestSerialize extends ConformanceTestCase {
    private final OMContainerExtractor containerExtractor;
    private final SerializationStrategy serializationStrategy;

    public TestSerialize(
            OMMetaFactory metaFactory,
            XMLSample file,
            OMContainerExtractor containerExtractor,
            SerializationStrategy serializationStrategy) {
        super(metaFactory, file);
        this.containerExtractor = containerExtractor;
        this.serializationStrategy = serializationStrategy;
        containerExtractor.addTestParameters(this);
        serializationStrategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder =
                file.getAdapter(XMLSampleAdapter.class).getBuilder(metaFactory);
        try {
            OMContainer container = containerExtractor.getContainer(builder);
            // We need to clone the InputSource objects so that we can dump their contents
            // if the test fails
            InputSource control[] =
                    duplicateInputSource(containerExtractor.getControl(file.getInputStream()));
            XML actual = serializationStrategy.serialize(container);
            try {
                // Configure the InputSources such that external entities can be resolved
                String systemId = new URL(file.getUrl(), "dummy.xml").toString();
                control[0].setSystemId(systemId);
                InputSource actualIS = actual.getInputSource();
                actualIS.setSystemId(systemId);
                assertAbout(xml())
                        .that(actualIS)
                        .ignoringElementContentWhitespace() // TODO: shouldn't be necessary
                        .hasSameContentAs(control[0]);
            } catch (Throwable ex) {
                System.out.println("Control:");
                dumpInputSource(control[1]);
                System.out.println("Actual:");
                actual.dump(System.out);
                throw ex;
            }
            if (serializationStrategy.isCaching()) {
                assertTrue(container.isComplete());
            } else {
                // TODO: need to investigate why assertConsumed is not working here
                assertFalse(container.isComplete());
                //                assertConsumed(element);
            }
        } finally {
            builder.close();
        }
    }

    private InputSource[] duplicateInputSource(InputSource is) throws IOException {
        // TODO: also handle character streams
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is.getByteStream(), baos);
        byte[] content = baos.toByteArray();
        return new InputSource[] {
            new InputSource(new ByteArrayInputStream(content)),
            new InputSource(new ByteArrayInputStream(content))
        };
    }

    private void dumpInputSource(InputSource is) throws IOException {
        // TODO: also handle character streams
        IOUtils.copy(is.getByteStream(), System.out);
        System.out.println();
    }
}
