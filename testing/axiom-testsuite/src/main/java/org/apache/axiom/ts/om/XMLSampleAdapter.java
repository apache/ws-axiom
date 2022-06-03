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
package org.apache.axiom.ts.om;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactorySPI;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testing.multiton.AdapterType;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.xml.XMLSample;
import org.xml.sax.InputSource;

@AdapterType
public final class XMLSampleAdapter {
    private final XMLSample sample;

    XMLSampleAdapter(XMLSample sample) {
        this.sample = sample;
    }

    public OMXMLParserWrapper getBuilder(OMMetaFactory metaFactory) {
        return ((OMMetaFactorySPI) metaFactory)
                .createOMBuilder(
                        AxiomTestCase.TEST_PARSER_CONFIGURATION,
                        new InputSource(sample.getUrl().toString()));
    }

    public OMDocument getDocument(OMMetaFactory metaFactory) {
        return getBuilder(metaFactory).getDocument();
    }

    public OMElement getDocumentElement(OMMetaFactory metaFactory) {
        return getBuilder(metaFactory).getDocumentElement();
    }
}
