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
package org.apache.axiom.ts.om.node;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.StringReader;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.junit.jupiter.api.function.Executable;

/** Tests the behavior of {@link OMNode#detach()}. */
public class TestDetach implements Executable {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("document")
    private boolean document;

    @Inject
    @Named("build")
    private boolean build;

    @Override
    public void execute() throws Throwable {
        OMContainer root;
        if (document) {
            root = OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<!--a--><b/><!--c-->"))
                    .getDocument();
        } else {
            root = OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<root><!--a--><b/><!--c--></root>"))
                    .getDocumentElement();
        }
        if (build) {
            root.build();
        } else {
            assertThat(root.isComplete()).isFalse();
        }
        OMComment a = (OMComment) root.getFirstOMChild();
        assertThat(a.getValue()).isEqualTo("a");
        OMElement b = (OMElement) a.getNextOMSibling();
        assertThat(b.getLocalName()).isEqualTo("b");
        OMNode returnValue = b.detach();
        assertThat(returnValue).isSameAs(b); // Detach is expected to do a "return this"
        assertThat(b.getParent()).isNull();
        assertThat(b.getPreviousOMSibling()).isNull();
        assertThat(b.getNextOMSibling()).isNull();
        OMComment c = (OMComment) a.getNextOMSibling();
        assertThat(c.getValue()).isEqualTo("c");
        assertThat(a.getNextOMSibling()).isSameAs(c);
        assertThat(c.getPreviousOMSibling()).isSameAs(a);
        root.close(false);
    }
}
