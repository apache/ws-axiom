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

import static com.google.common.truth.Truth.assertThat;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ts.xml.XMLSample;

public class TestDTDReader extends DialectTestCase {
    public TestDTDReader(StAXImplementationAdapter staxImpl) {
        super(staxImpl);
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream in = XMLSample.DTD_FULL.getInputStream();
        try {
            XMLStreamReader reader =
                    staxImpl.newNormalizedXMLInputFactory()
                            .createXMLStreamReader(XMLSample.DTD_FULL.getUrl().toString(), in);
            while (reader.next() != XMLStreamReader.DTD) {
                // Just loop
            }
            DTDReader dtdReader = (DTDReader) reader.getProperty(DTDReader.PROPERTY);
            assertThat(dtdReader).isNotNull();
            assertThat(dtdReader.getRootName()).isEqualTo("root");
            assertThat(dtdReader.getSystemId()).isEqualTo("dtd-full.dtd");
            assertThat(dtdReader.getPublicId()).isEqualTo("-//TEST//Dummy DTD//EN");
            reader.close();
        } finally {
            in.close();
        }
    }
}
