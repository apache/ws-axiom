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
package org.apache.axiom.ts.om.text;

import com.google.inject.Inject;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.testutils.blob.TestBlob;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.commons.io.output.NullOutputStream;

/**
 * Test that when an {@link OMText} node is written to an XMLStreamWriter without MTOM support, the
 * implementation doesn't construct an in-memory base64 representation of the complete binary
 * content, but writes it in chunks (streaming).
 *
 * <p>Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-236">AXIOM-236</a>.
 */
public class TestBase64StreamingWithSerialize implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMElement elem = factory.createOMElement("test", null);
        // Create a blob that would eat up all memory when loaded. If the test doesn't fail with an
        // OutOfMemoryError, we know that the OMText implementation supports streaming.
        OMText text =
                factory.createOMText(new TestBlob('A', Runtime.getRuntime().maxMemory()), false);
        elem.addChild(text);
        elem.serialize(NullOutputStream.INSTANCE);
    }
}
