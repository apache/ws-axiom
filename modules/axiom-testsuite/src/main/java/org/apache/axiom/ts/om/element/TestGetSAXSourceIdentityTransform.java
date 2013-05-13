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

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.jaxp.OMResult;
import org.apache.axiom.testutils.suite.XSLTImplementation;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetSAXSourceIdentityTransform extends AxiomTestCase {
    private final XSLTImplementation xsltImplementation;
    private final boolean cache;

    public TestGetSAXSourceIdentityTransform(OMMetaFactory metaFactory, XSLTImplementation xsltImplementation, boolean cache) {
        super(metaFactory);
        this.xsltImplementation = xsltImplementation;
        this.cache = cache;
        xsltImplementation.addTestParameters(this);
        addTestParameter("cache", cache);
    }

    private InputStream getInput() {
        return TestGetSAXSourceIdentityTransform.class.getResourceAsStream("test.xml");
    }
    
    protected void runTest() throws Throwable {
        Transformer transformer = xsltImplementation.newTransformerFactory().newTransformer();
        
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = OMXMLBuilderFactory.createOMBuilder(factory, getInput()).getDocumentElement();
        OMResult omResult = new OMResult(factory);
        transformer.transform(element.getSAXSource(cache), omResult);
        
        StreamSource streamSource = new StreamSource(getInput());
        StringWriter out = new StringWriter();
        StreamResult streamResult = new StreamResult(out);
        transformer.transform(streamSource, streamResult);
        
        XMLAssert.assertXMLIdentical(XMLUnit.compareXML(out.toString(), omResult.getRootElement().toString()), true);
        
        element.close(false);
    }
}
