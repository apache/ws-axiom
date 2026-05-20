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
import java.io.OutputStream;
import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.testutils.RandomUtils;
import org.apache.axiom.testutils.blob.RandomBlob;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that the content of the root part of an XOP message is buffered, i.e. that an attachment
 * part can be accessed before the object model for the root part has been built completely.
 *
 * <p>Note:
 *
 * <ul>
 *   <li>Axiom &lt;= 1.2.12 reads the content of the root part into a buffer before creating the
 *       parser.
 *   <li>In Axiom 1.2.13 the root part is buffered on-demand (as described in <a
 *       href="https://issues.apache.org/jira/browse/AXIOM-403">AXIOM-403</a>) and this unit test
 *       checks that this feature is working as expected.
 * </ul>
 */
public class TestReadAttachmentBeforeRootPartComplete implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {

        // Programmatically create the message
        OMElement orgRoot = factory.createOMElement("root", null);
        OMElement orgChild1 = factory.createOMElement("child1", null, orgRoot);
        Blob orgBlob = new RandomBlob(54321, 4096);
        orgChild1.addChild(factory.createOMText(orgBlob, true));
        // Create a child with a large text content and insert it after the binary node.
        // If we don't do this, then the root part may be buffered entirely by the parser,
        // and the test would not be effective.
        OMElement orgChild2 = factory.createOMElement("child2", null, orgRoot);
        String s = RandomUtils.randomString(128 * 1024);
        orgChild2.setText(s);

        // Serialize the message
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        MemoryBlob xop = Blobs.createMemoryBlob();
        OutputStream out = xop.getOutputStream();
        orgRoot.serialize(out, format);
        out.close();

        // Parse the message
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(
                factory,
                StAXParserConfiguration.NON_COALESCING,
                MultipartBody.builder()
                        .setInputStream(xop.getInputStream())
                        .setContentType(format.getContentType())
                        .build());
        OMElement root = builder.getDocumentElement();
        OMElement child1 = (OMElement) root.getFirstOMChild();
        OMText text = (OMText) child1.getFirstOMChild();
        assertThat(text.isBinary()).isTrue();
        // Access the Blob
        Blob blob = text.getBlob();
        IOTestUtils.compareStreams(orgBlob.getInputStream(), blob.getInputStream());
        OMElement child2 = (OMElement) child1.getNextOMSibling();
        assertThat(child2.isComplete()).isFalse();
        assertThat(child2.getText()).isEqualTo(s);
    }
}
