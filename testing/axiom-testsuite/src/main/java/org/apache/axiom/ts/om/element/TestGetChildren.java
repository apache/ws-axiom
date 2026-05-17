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
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/** Test the plain iterator which includes all the children (including the texts) */
public class TestGetChildren implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMElement elt = OMXMLBuilderFactory.createOMBuilder(
                        factory, new StringReader("<root>a<b/><!--c--><d/>e</root>"))
                .getDocumentElement();
        Iterator<OMNode> iter = elt.getChildren();
        int counter = 0;
        while (iter.hasNext()) {
            counter++;
            assertThat(iter.next()).isNotNull();
        }
        assertThat(counter).isEqualTo(5);
        elt.close(false);
    }
}
