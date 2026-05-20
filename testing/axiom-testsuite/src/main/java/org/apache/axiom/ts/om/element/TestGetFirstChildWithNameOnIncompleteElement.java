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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.StringReader;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the behavior of {@link OMElement#getFirstChildWithName(QName)} on an incomplete element. It
 * checks that the method doesn't build the returned element. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-333">AXIOM-333</a>.
 */
public class TestGetFirstChildWithNameOnIncompleteElement implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {
        OMElement element = OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<root><a/><b/><c/></root>"))
                .getDocumentElement();
        OMElement b = element.getFirstChildWithName(new QName("b"));
        assertThat(b.isComplete()).isFalse();
    }
}
