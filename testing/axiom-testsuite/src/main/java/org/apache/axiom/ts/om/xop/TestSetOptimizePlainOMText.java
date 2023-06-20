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
package org.apache.axiom.ts.om.xop;

import static com.google.common.truth.Truth.assertThat;

import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.xerces.impl.dv.util.Base64;

/**
 * Tests {@link OMText#setOptimize(boolean)} on a plain {@link OMText} node with valid base64
 * encoded data. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-519">AXIOM-519</a>.
 */
public class TestSetOptimizePlainOMText extends AxiomTestCase {
    public TestSetOptimizePlainOMText(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement("element", null);
        OMText text =
                factory.createOMText(
                        element, Base64.encode("foobar".getBytes(StandardCharsets.UTF_8)));
        text.setOptimize(true);
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        StringWriter sw = new StringWriter();
        OutputStream out = new WriterOutputStream(sw, StandardCharsets.UTF_8);
        element.serialize(out, format, true);
        out.close();
        assertThat(sw.toString()).contains("foobar");
    }
}
