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

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.XMLSampleAdapter;
import org.apache.axiom.ts.xml.XMLSample;

public class TestClose extends AxiomTestCase {
    public TestClose(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement rootElement =
                XMLSample.SIMPLE.getAdapter(XMLSampleAdapter.class).getDocumentElement(metaFactory);

        // get the first OMElement child
        OMNode omnode = rootElement.getFirstOMChild();
        while (!(omnode instanceof OMElement)) {
            omnode = omnode.getNextOMSibling();
        }
        // Close the element after building the element
        OMElement omElement = (OMElement) omnode;
        omElement.close(true);

        Iterator<OMNode> children = ((OMElement) omnode).getChildren();
        int childrenCount = 0;
        while (children.hasNext()) {
            if (children.next() instanceof OMElement) {
                childrenCount++;
            }
        }

        assertThat(childrenCount).isEqualTo(2);
    }
}
