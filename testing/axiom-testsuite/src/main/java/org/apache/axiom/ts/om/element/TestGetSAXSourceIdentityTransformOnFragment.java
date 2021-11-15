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

import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.xml.sax.ContentHandler;

/**
 * Tests that the {@link SAXSource} returned by {@link OMContainer#getSAXSource(boolean)} generates
 * {@link ContentHandler#startPrefixMapping(String, String)} events for namespace mappings in scope
 * on the source element.
 * This checks for an issue that may arise under the following circumstances:
 * <ol>
 *   <li>The element on which {@link OMContainer#getSAXSource(boolean)} is called is not the root
 *   element of the document.</li>
 *   <li>One of the ancestors declares a namespace mapping.</li>
 *   <li>The namespace mapping is not used in the name of the source element or any of its
 *   descendant elements or attributes (but may be used in the value of an attribute).</li>   
 * </ol>
 * Example:
 * <pre>&lt;root xmlns:ns="urn:ns"&gt;&lt;element attr="ns:someThing"/&gt;&lt;root&gt;</pre>
 * In that case, when constructing an {@link SAXSource} from the child element, the namespace
 * mapping for the {@code ns} prefix should be visible to the consumer. Otherwise it would not
 * be able to interpret the attribute value correctly. This is relevant e.g. when validating
 * a part of a document against an XML schema (see
 * <a href="https://issues.apache.org/jira/browse/SYNAPSE-501">SYNAPSE-501</a>).
 */
public class TestGetSAXSourceIdentityTransformOnFragment extends AxiomTestCase {
    private final XSLTImplementation xsltImplementation;
    private final boolean cache;

    public TestGetSAXSourceIdentityTransformOnFragment(OMMetaFactory metaFactory, XSLTImplementation xsltImplementation, boolean cache) {
        super(metaFactory);
        this.xsltImplementation = xsltImplementation;
        this.cache = cache;
        addTestParameter("xslt", xsltImplementation.getName());
        addTestParameter("cache", cache);
    }

    private InputStream getInput() {
        return TestGetSAXSourceIdentityTransformOnFragment.class.getResourceAsStream("test.xml");
    }
    
    @Override
    protected void runTest() throws Throwable {
        Transformer transformer = xsltImplementation.newTransformerFactory().newTransformer();
        
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = OMXMLBuilderFactory.createOMBuilder(factory, getInput()).getDocumentElement().getFirstElement();
        OMDocument outputDocument = factory.createOMDocument();
        transformer.transform(element.getSAXSource(cache), outputDocument.getSAXResult());
        
        OMNamespace ns = outputDocument.getOMDocumentElement().findNamespaceURI("p");
        assertNotNull(ns);
        assertEquals("urn:some:namespace", ns.getNamespaceURI());
        
        element.close(false);
    }
}
