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
package org.apache.axiom.om.impl;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestSuite;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;

/**
 * Test comparing the output of {@link OMStAXWrapper} with that of a native StAX parser.
 * {@link #suite(OMMetaFactory)} can be used to build a complete test suite from the
 * XML files returned by {@link AbstractTestCase#getConformanceTestFiles()}.
 */
public class OMStAXWrapperConformanceTestCase extends AbstractTestCase {
    private final OMMetaFactory omMetaFactory;
    private final String file;
    
    private OMStAXWrapperConformanceTestCase(OMMetaFactory omMetaFactory, String name, String file) {
        super(name);
        this.omMetaFactory = omMetaFactory;
        this.file = file;
    }
    
    public void runTest() throws Throwable {
        InputStream in1 = getTestResource(file);
        InputStream in2 = getTestResource(file);
        try {
            XMLStreamReader expected = StAXUtils.createXMLStreamReader(in1);
            try {
                StAXOMBuilder builder = new StAXOMBuilder(omMetaFactory.getOMFactory(),
                        StAXUtils.createXMLStreamReader(in2));
                try {
                    XMLStreamReader actual = builder.getDocument().getXMLStreamReader();
                    new XMLStreamReaderComparator(new RootWhitespaceFilter(expected),
                            new RootWhitespaceFilter(actual)).compare();
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

    public static TestSuite suite(OMMetaFactory omMetaFactory) throws Exception {
        TestSuite suite = new TestSuite();
        String[] files = getConformanceTestFiles();
        for (int i=0; i<files.length; i++) {
            String file = files[i];
            int idx = file.lastIndexOf('/');
            String name = file.substring(idx+1);
            suite.addTest(new OMStAXWrapperConformanceTestCase(omMetaFactory, name, file));
        }
        return suite;
    }
}
