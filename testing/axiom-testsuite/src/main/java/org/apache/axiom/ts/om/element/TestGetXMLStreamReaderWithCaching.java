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
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import junit.framework.TestCase;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

/**
 * Tests that the object model can still be accessed after using {@link
 * OMContainer#getXMLStreamReader()}.
 */
public class TestGetXMLStreamReaderWithCaching extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(
                factory, TestGetChildElementsConsumed.class.getResourceAsStream("purchase-order.xml"));

        OMElement documentElement = builder.getDocumentElement();
        XMLStreamReader reader = documentElement.getXMLStreamReader();

        // consume the parser. this should force the xml stream to be exhausted but the
        // tree to be fully built
        while (reader.hasNext()) {
            reader.next();
        }

        // try to find the children of the document element. This should *NOT* produce an
        // error even when the underlying stream is fully consumed , the object tree is already
        // complete
        Iterator<OMElement> childElements = documentElement.getChildElements();
        int count = 0;
        while (childElements.hasNext()) {
            childElements.next();
            count++;
        }

        assertThat(count).isEqualTo(2);

        documentElement.close(false);
    }
}
