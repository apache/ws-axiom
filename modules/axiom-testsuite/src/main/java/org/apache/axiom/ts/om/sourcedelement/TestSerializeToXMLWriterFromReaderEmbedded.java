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

import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests OMSourcedElement processing when the getReader() of the parent is accessed.
 */
public class TestSerializeToXMLWriterFromReaderEmbedded extends AxiomTestCase {
    public TestSerializeToXMLWriterFromReaderEmbedded(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory f = metaFactory.getOMFactory();
        OMSourcedElement element = TestDocument.DOCUMENT1.createOMSourcedElement(f, true);
        OMElement root = f.createOMElement("root", f.createOMNamespace("http://sampleroot", "rootPrefix"));
        root.addChild(element);
        
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = StAXUtils.createXMLStreamWriter(writer);

        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(
                metaFactory.getOMFactory(), root.getXMLStreamReader());
        OMDocument omDocument = builder.getDocument();
        Iterator it = omDocument.getChildren();
        while (it.hasNext()) {
            OMNode omNode = (OMNode) it.next();
            // TODO: quick fix required because OMChildrenIterator#next() no longer builds the node
            omNode.getNextOMSibling();
            omNode.serializeAndConsume(xmlwriter);
        }
        xmlwriter.flush();
        String result = writer.toString();
        // We can't test for equivalence because the underlying OMSourceElement is 
        // changed as it is serialized.  So I am testing for an internal value.
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
        // The implementation uses OMNavigator to walk the tree.  Currently OMNavigator must 
        // expand the OMSourcedElement to correctly walk the elements. (See OMNavigator._getFirstChild)
        //assertFalse("Element expansion when serializing", element.isExpanded());
    }
}
