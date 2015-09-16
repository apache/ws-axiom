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

package org.apache.axiom.om.util;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPSampleSet;

public class OMElementHelperTest extends TestCase {
    public void testImportOMElement() throws Exception {
        OMElement documentElement =
                OMXMLBuilderFactory.createOMBuilder(OMAbstractFactory.getOMFactory(), SOAPSampleSet.WSA.getMessage(SOAPSpec.SOAP11).getInputStream())
                        .getDocumentElement();

        // first lets try to import an element created from llom in to llom factory. This should return the same element
        assertTrue(ElementHelper
                .importOMElement(documentElement, OMAbstractFactory.getOMFactory()) ==
                documentElement);

        // then lets pass in an OMElement created using llom and pass DOOMFactory
        OMElement importedElement = ElementHelper
                .importOMElement(documentElement, OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM).getOMFactory());
        assertTrue(importedElement != documentElement);
        assertTrue(importedElement.getOMFactory() ==
                OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM).getOMFactory());
        
        documentElement.close(false);
    }
}
