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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.blob.RandomBlob;
import org.junit.jupiter.api.function.Executable;

public class TestCloneBinary implements Executable {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("fetch")
    private boolean fetch;

    @Override
    public void execute() throws Throwable {
        Blob blob = new RandomBlob(600613L, 4096);
        StringReader rootPart = new StringReader(
                "<root><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:123456@example.org'/></root>");
        DummyAttachmentAccessor attachmentAccessor = new DummyAttachmentAccessor("123456@example.org", blob);
        OMElement root = OMXMLBuilderFactory.createOMBuilder(factory, new StreamSource(rootPart), attachmentAccessor)
                .getDocumentElement();
        OMText text = (OMText) root.getFirstOMChild();
        OMCloneOptions options = new OMCloneOptions();
        options.setFetchBlobs(fetch);
        OMText clone = (OMText) text.clone(options);
        assertThat(clone.isBinary()).isTrue();
        assertThat(attachmentAccessor.isLoaded()).isEqualTo(fetch);
        assertThat(clone.getBlob()).isSameAs(blob);
    }
}
