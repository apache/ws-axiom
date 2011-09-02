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
package org.apache.axiom.ts.om.navigator;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.ts.CommonImplTestCase;

public class OMNavigatorTestCase extends CommonImplTestCase {
    protected SOAPEnvelope envelope;
    protected StAXSOAPModelBuilder builder;

    public OMNavigatorTestCase(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void setUp() throws Exception {
        XMLStreamReader xmlStreamReader = StAXUtils.
                createXMLStreamReader(AbstractTestCase.getTestResource(TestConstants.SOAP_SOAPMESSAGE1));
        builder = new StAXSOAPModelBuilder(metaFactory, xmlStreamReader, null);
        envelope = (SOAPEnvelope) builder.getDocumentElement();
    }

    protected void tearDown() throws Exception {
        builder.close();
    }
}
