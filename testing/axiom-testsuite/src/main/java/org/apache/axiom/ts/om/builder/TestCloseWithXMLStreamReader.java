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

import java.io.InputStream;
import java.lang.ref.WeakReference;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.xml.XMLSample;

public class TestCloseWithXMLStreamReader extends AxiomTestCase {
    public TestCloseWithXMLStreamReader(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream in = XMLSample.SIMPLE.getInputStream();
        try {
            XMLStreamReader reader = StAXUtils.createXMLStreamReader(in);
            OMXMLParserWrapper builder =
                    OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader);
            WeakReference<XMLStreamReader> readerWeakRef =
                    new WeakReference<XMLStreamReader>(reader);
            reader = null;
            builder.getDocument().build();
            builder.close();
            for (int i = 0; i < 10; i++) {
                Thread.sleep(500);
                if (readerWeakRef.get() == null) {
                    return;
                }
                System.gc();
            }
            fail("Builder didn't release reference to the underlying parser");
        } finally {
            in.close();
        }
    }
}
