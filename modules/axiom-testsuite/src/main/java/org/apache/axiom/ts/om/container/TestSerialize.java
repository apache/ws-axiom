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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;

public class TestSerialize extends ConformanceTestCase {
    private final OMContainerFactory containerFactory;
    private final SerializationMethod serializationMethod;
    
    public TestSerialize(OMMetaFactory metaFactory, ConformanceTestFile file,
            OMContainerFactory containerFactory, SerializationMethod serializationMethod) {
        super(metaFactory, file);
        this.containerFactory = containerFactory;
        this.serializationMethod = serializationMethod;
        containerFactory.addTestProperties(this);
        serializationMethod.addTestProperties(this);
    }

    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder = metaFactory.createOMBuilder(metaFactory.getOMFactory(),
                TEST_PARSER_CONFIGURATION, new InputSource(file.getUrl().toString()));
        try {
            OMContainer container = containerFactory.getContainer(builder);
            // We need to clone the InputSource objects so that we can dump their contents
            // if the test fails
            InputSource control[] = duplicateInputSource(containerFactory.getControl(file.getAsStream()));
            InputSource actual[] = duplicateInputSource(serializationMethod.serialize(container));
            try {
                // Configure the InputSources such that external entities can be resolved
                String systemId = new URL(file.getUrl(), "dummy.xml").toString();
                control[0].setSystemId(systemId);
                actual[0].setSystemId(systemId);
                XMLAssert.assertXMLIdentical(XMLUnit.compareXML(control[0], actual[0]), true);
            } catch (Throwable ex) {
                System.out.println("Control:");
                dumpInputSource(control[1]);
                System.out.println("Actual:");
                dumpInputSource(actual[1]);
                throw ex;
            }
            if (serializationMethod.isCaching()) {
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
        return new InputSource[] { new InputSource(new ByteArrayInputStream(content)),
                new InputSource(new ByteArrayInputStream(content)) };
    }
    
    private void dumpInputSource(InputSource is) throws IOException {
        // TODO: also handle character streams
        IOUtils.copy(is.getByteStream(), System.out);
        System.out.println();
    }
}
