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
package org.apache.axiom.ts.soap.body;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.AssertionFailedError;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public abstract class FirstElementNameWithParserTestCase extends SOAPTestCase {
    protected final QName qname;
    private final boolean supportsOptimization;
    
    public FirstElementNameWithParserTestCase(OMMetaFactory metaFactory,
            SOAPSpec spec, QName qname, boolean supportsOptimization) {
        super(metaFactory, spec);
        this.qname = qname;
        addTestProperty("prefix", qname.getPrefix());
        addTestProperty("uri", qname.getNamespaceURI());
        this.supportsOptimization = supportsOptimization;
    }

    protected final void runTest() throws Throwable {
        SOAPEnvelope orgEnvelope = soapFactory.getDefaultEnvelope();
        orgEnvelope.getBody().addChild(soapFactory.createOMElement(
                qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix()));
        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory,
                new StringReader(orgEnvelope.toString()));
        if (supportsOptimization) {
            // To detect if the child element is instantiated or not, we register a custom
            // builder that throws an exception.
            ((StAXSOAPModelBuilder)builder).registerCustomBuilderForPayload(new CustomBuilder() {
                public OMElement create(String namespace, String localPart,
                        OMContainer parent, XMLStreamReader reader, OMFactory factory)
                        throws OMException {
                    throw new AssertionFailedError("Custom builder called.");
                }
            });
        }
        runTest(builder.getSOAPEnvelope().getBody());
    }

    protected abstract void runTest(SOAPBody body) throws Throwable;
}
