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
package org.apache.axiom.ts.om.element.sr;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.ts.AxiomTestCase;

import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

/**
 * Tests the behavior of {@link XMLStreamReader#close()} on the {@link XMLStreamReader} returned by
 * {@link OMContainer#getXMLStreamReader(boolean)} on an {@link OMElement}. Regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-2">AXIOM-2</a>.
 */
public class TestClose extends AxiomTestCase {
    private final boolean cache;
    
    public TestClose(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    protected void runTest() throws Throwable {
        OMXMLParserWrapper b = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(),
                new StringReader("<test>test</test>"));
        
        OMElement element = b.getDocumentElement();
        
        XMLStreamReader reader = element.getXMLStreamReader(cache);
        
        while (reader.hasNext()) {
            reader.next();
        }

        // Make sure that the wrapper can be closed without failing
        reader.close();
        reader.close();  // This should be a noop since the parser is closed.
        
        // Closing the parser should also close the parser on the builder (since they are the same)
        assertTrue(((StAXOMBuilder)b).isClosed());
        b.close(); // This should be a noop since the parser is closed
        
        // Calling getProperty after a close should return null, not an exception
        assertTrue(reader.getProperty("dummyProperty") == null);
        
        // Calling builder.getReaderProperty should return null, not an exception
        assertTrue(((StAXOMBuilder)b).getReaderProperty("dummyProperty") == null);
    }


}
