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

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.ts.ConformanceTestCase;

/**
 * Test comparing the output of {@link OMContainer#getXMLStreamReader(boolean)} with that of a
 * native StAX parser.
 */
public abstract class GetXMLStreamReaderTestCase extends ConformanceTestCase {
    private final boolean cache;
    
    public GetXMLStreamReaderTestCase(OMMetaFactory metaFactory, String file, boolean cache) {
        super(metaFactory, file);
        this.cache = cache;
        setName(getName() + " [cache=" + cache + "]");
    }
    
    protected final void runTest() throws Throwable {
        InputStream in1 = getFileAsStream();
        InputStream in2 = getFileAsStream();
        try {
            XMLStreamReader expected = StAXUtils.createXMLStreamReader(in1);
            try {
                OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in2);
                try {
                    XMLStreamReader actual = getContainer(builder).getXMLStreamReader(cache);
                    new XMLStreamReaderComparator(filter(expected), filter(actual)).compare();
                } finally {
                    builder.close();
                }
            } finally {
                expected.close();
            }
        } finally {
            in1.close();
            in2.close();
        }
    }
    
    protected abstract OMContainer getContainer(OMXMLParserWrapper builder);
    protected abstract XMLStreamReader filter(XMLStreamReader reader);
}
