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
package org.apache.axiom.ts.om.sourcedelement;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.nio.charset.StandardCharsets;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.WrappedTextNodeOMDataSourceFromBlob;
import org.apache.axiom.testutils.blob.TextBlob;
import org.apache.axiom.testutils.suite.MatrixTestCase;

public class TestCloneNonDestructive implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("copyOMDataSources")
    private boolean copyOMDataSources;

    @Override
    public void runTest() throws Throwable {
        OMDataSource ds = new WrappedTextNodeOMDataSourceFromBlob(
                new QName("wrapper"), new TextBlob("test", StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        OMSourcedElement element = factory.createOMElement(ds);
        OMCloneOptions options = new OMCloneOptions();
        options.setCopyOMDataSources(copyOMDataSources);
        OMElement clone = (OMElement) element.clone(options);
        if (copyOMDataSources) {
            assertThat(clone).isInstanceOf(OMSourcedElement.class);
            assertThat(element.isExpanded()).isFalse();
        } else {
            assertThat(clone).isNotInstanceOf(OMSourcedElement.class);
            assertThat(clone.isComplete()).isTrue();
        }
        assertThat(clone.getText()).isEqualTo("test");
        assertThat(clone.getLocalName()).isEqualTo("wrapper");
    }
}
