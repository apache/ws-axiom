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
package org.apache.axiom.om.impl.dom;

import junit.framework.TestSuite;

import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.apache.axiom.ts.w3c.dom.FilteredDOMTestSuite;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.JAXPDOMTestDocumentBuilderFactory;
import org.w3c.domts.JUnitTestSuiteAdapter;
import org.w3c.domts.level2.core.*;

public class W3CDOM2Test extends TestSuite {
    public static TestSuite suite() throws Exception {
        DOMTestDocumentBuilderFactory factory = new JAXPDOMTestDocumentBuilderFactory(
                new OMDOMMetaFactory().newDocumentBuilderFactory(),
                JAXPDOMTestDocumentBuilderFactory.getConfiguration1());

        FilteredDOMTestSuite suite = new FilteredDOMTestSuite(factory, new alltests(factory));
        suite.addExclude(createAttributeNS01.class);
        suite.addExclude(createAttributeNS02.class);
        suite.addExclude(createAttributeNS04.class);
        suite.addExclude(createAttributeNS06.class);
        suite.addExclude(createDocument01.class);
        suite.addExclude(createDocument02.class);
        suite.addExclude(createDocument03.class);
        suite.addExclude(createDocument04.class);
        suite.addExclude(createDocument05.class);
        suite.addExclude(createDocument06.class);
        suite.addExclude(createDocument08.class);
        suite.addExclude(createDocumentType01.class);
        suite.addExclude(createDocumentType02.class);
        suite.addExclude(createDocumentType03.class);
        suite.addExclude(createDocumentType04.class);
        suite.addExclude(createElementNS01.class);
        suite.addExclude(createElementNS02.class);
        suite.addExclude(createElementNS04.class);
        suite.addExclude(documentcreateattributeNS03.class);
        suite.addExclude(documentcreateattributeNS04.class);
        suite.addExclude(documentcreateattributeNS05.class);
        suite.addExclude(documentcreateattributeNS06.class);
        suite.addExclude(documentcreateattributeNS07.class);
        suite.addExclude(documentcreateelementNS05.class);
        suite.addExclude(documentcreateelementNS06.class);
        suite.addExclude(documentgetelementsbytagnameNS01.class);
        suite.addExclude(documentgetelementsbytagnameNS03.class);
        suite.addExclude(documentgetelementsbytagnameNS05.class);
        suite.addExclude(documenttypeinternalSubset01.class);
        suite.addExclude(documenttypepublicid01.class);
        suite.addExclude(documenttypesystemid01.class);
        suite.addExclude(domimplementationcreatedocument04.class);
        suite.addExclude(domimplementationcreatedocument05.class);
        suite.addExclude(domimplementationcreatedocument07.class);
        suite.addExclude(domimplementationcreatedocumenttype01.class);
        suite.addExclude(domimplementationcreatedocumenttype02.class);
        suite.addExclude(domimplementationcreatedocumenttype04.class);
        suite.addExclude(elementsetattributens04.class);
        suite.addExclude(elementsetattributens05.class);
        suite.addExclude(elementsetattributens08.class);
        suite.addExclude(elementsetattributensurinull.class);
        suite.addExclude(getAttributeNS02.class);
        suite.addExclude(getAttributeNS03.class);
        suite.addExclude(getAttributeNS04.class);
        suite.addExclude(getAttributeNodeNS01.class);
        suite.addExclude(getElementsByTagNameNS01.class);
        suite.addExclude(getElementsByTagNameNS05.class);
        suite.addExclude(getElementsByTagNameNS08.class);
        suite.addExclude(getNamedItemNS02.class);
        suite.addExclude(getNamedItemNS03.class);
        suite.addExclude(getNamedItemNS04.class);
        suite.addExclude(hasAttribute01.class);
        suite.addExclude(hasAttribute03.class);
        suite.addExclude(hasAttributeNS01.class);
        suite.addExclude(hasAttributeNS02.class);
        suite.addExclude(hasAttributeNS03.class);
        suite.addExclude(hasAttributes01.class);
        suite.addExclude(hasAttributes02.class);
        suite.addExclude(hc_entitiesremovenameditemns1.class);
        suite.addExclude(hc_entitiessetnameditemns1.class);
        suite.addExclude(hc_nodedocumentfragmentnormalize1.class);
        suite.addExclude(hc_nodedocumentfragmentnormalize2.class);
        suite.addExclude(hc_notationsremovenameditemns1.class);
        suite.addExclude(hc_notationssetnameditemns1.class);
        suite.addExclude(importNode03.class);
        suite.addExclude(importNode04.class);
        suite.addExclude(importNode05.class);
        suite.addExclude(importNode06.class);
        suite.addExclude(importNode09.class);
        suite.addExclude(importNode10.class);
        suite.addExclude(importNode11.class);
        suite.addExclude(importNode12.class);
        suite.addExclude(importNode13.class);
        suite.addExclude(importNode14.class);
        suite.addExclude(importNode16.class);
        suite.addExclude(isSupported01.class);
        suite.addExclude(isSupported02.class);
        suite.addExclude(isSupported04.class);
        suite.addExclude(isSupported05.class);
        suite.addExclude(isSupported06.class);
        suite.addExclude(isSupported07.class);
        suite.addExclude(isSupported09.class);
        suite.addExclude(isSupported10.class);
        suite.addExclude(isSupported11.class);
        suite.addExclude(isSupported12.class);
        suite.addExclude(isSupported13.class);
        suite.addExclude(isSupported14.class);
        suite.addExclude(localName02.class);
        suite.addExclude(localName03.class);
        suite.addExclude(namednodemapgetnameditemns01.class);
        suite.addExclude(namespaceURI04.class);
        suite.addExclude(nodehasattributes01.class);
        suite.addExclude(nodehasattributes03.class);
        suite.addExclude(nodeissupported01.class);
        suite.addExclude(nodeissupported02.class);
        suite.addExclude(nodeissupported03.class);
        suite.addExclude(nodeissupported04.class);
        suite.addExclude(nodeissupported05.class);
        suite.addExclude(nodenormalize01.class);
        suite.addExclude(nodesetprefix06.class);
        suite.addExclude(nodesetprefix07.class);
        suite.addExclude(nodesetprefix08.class);
        suite.addExclude(nodesetprefix09.class);
        suite.addExclude(ownerElement01.class);
        suite.addExclude(prefix02.class);
        suite.addExclude(prefix04.class);
        suite.addExclude(prefix05.class);
        suite.addExclude(prefix07.class);
        suite.addExclude(prefix10.class);
        suite.addExclude(prefix11.class);
        suite.addExclude(removeNamedItemNS02.class);
        suite.addExclude(setAttributeNS01.class);
        suite.addExclude(setAttributeNS02.class);
        suite.addExclude(setAttributeNS03.class);
        suite.addExclude(setAttributeNS05.class);
        suite.addExclude(setAttributeNS06.class);
        suite.addExclude(setAttributeNS07.class);
        suite.addExclude(setAttributeNS09.class);
        suite.addExclude(setAttributeNS10.class);
        suite.addExclude(setAttributeNodeNS01.class);
        suite.addExclude(setAttributeNodeNS03.class);
        suite.addExclude(setAttributeNodeNS05.class);
        suite.addExclude(setNamedItemNS02.class);
        suite.addExclude(setNamedItemNS03.class);
        suite.addExclude(setNamedItemNS05.class);
        return new JUnitTestSuiteAdapter(suite);
    }
}
