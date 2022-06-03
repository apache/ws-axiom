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
package org.apache.axiom.ts.om.element;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.activation.DataSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetTextAsStreamWithoutCaching extends AxiomTestCase {
    public TestGetTextAsStreamWithoutCaching(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        Charset charset = Charset.forName("ascii");
        OMFactory factory = metaFactory.getOMFactory();
        DataSource ds = new RandomDataSource(654321, 64, 128, 20000000);
        Vector<InputStream> v = new Vector<InputStream>();
        v.add(new ByteArrayInputStream("<root><a>".getBytes(charset)));
        v.add(ds.getInputStream());
        v.add(new ByteArrayInputStream("</a><b/></root>".getBytes(charset)));
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                factory,
                                StAXParserConfiguration.NON_COALESCING,
                                new SequenceInputStream(v.elements()),
                                "ascii")
                        .getDocumentElement();
        OMElement child = (OMElement) root.getFirstOMChild();
        Reader in = child.getTextAsStream(false);
        IOTestUtils.compareStreams(
                new InputStreamReader(ds.getInputStream(), charset), "expected", in, "actual");
        in.close();
        // No try to access subsequent nodes
        child = (OMElement) child.getNextOMSibling();
        assertThat(child.getLocalName()).isEqualTo("b");
    }
}
