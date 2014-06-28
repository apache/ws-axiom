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
package org.apache.axiom.ts.w3c.dom;

import junit.framework.TestSuite;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.JAXPDOMTestDocumentBuilderFactory;
import org.w3c.domts.JUnitTestSuiteAdapter;
import org.w3c.domts.level3.core.alltests;
import org.w3c.domts.level3.core.documentgetinputencoding03;
import org.w3c.domts.level3.core.documentnormalizedocument07;
import org.w3c.domts.level3.core.documentnormalizedocument10;
import org.w3c.domts.level3.core.documentnormalizedocument11;
import org.w3c.domts.level3.core.domconfigurationcansetparameter06;
import org.w3c.domts.level3.core.elementgetschematypeinfo02;
import org.w3c.domts.level3.core.elementgetschematypeinfo07;
import org.w3c.domts.level3.core.entities02;
import org.w3c.domts.level3.core.entities03;
import org.w3c.domts.level3.core.infoset01;
import org.w3c.domts.level3.core.infoset02;
import org.w3c.domts.level3.core.infoset03;
import org.w3c.domts.level3.core.infoset07;
import org.w3c.domts.level3.core.nodegetbaseuri16;
import org.w3c.domts.level3.core.nodegettextcontent18;
import org.w3c.domts.level3.core.nodeisequalnode03;
import org.w3c.domts.level3.core.noderemovechild13;
import org.w3c.domts.level3.core.noderemovechild24;
import org.w3c.domts.level3.core.nodereplacechild38;
import org.w3c.domts.level3.core.textiselementcontentwhitespace05;
import org.w3c.domts.level3.core.typeinfogettypename04;
import org.w3c.domts.level3.core.typeinfoisderivedfrom15;
import org.w3c.domts.level3.core.typeinfoisderivedfrom16;
import org.w3c.domts.level3.core.typeinfoisderivedfrom17;
import org.w3c.domts.level3.core.typeinfoisderivedfrom18;
import org.w3c.domts.level3.core.typeinfoisderivedfrom19;
import org.w3c.domts.level3.core.typeinfoisderivedfrom21;
import org.w3c.domts.level3.core.typeinfoisderivedfrom40;
import org.w3c.domts.level3.core.typeinfoisderivedfrom41;
import org.w3c.domts.level3.core.typeinfoisderivedfrom58;
import org.w3c.domts.level3.core.typeinfoisderivedfrom59;
import org.w3c.domts.level3.core.typeinfoisderivedfrom66;
import org.w3c.domts.level3.core.typeinfoisderivedfrom67;
import org.w3c.domts.level3.core.typeinfoisderivedfrom68;
import org.w3c.domts.level3.core.typeinfoisderivedfrom73;
import org.w3c.domts.level3.core.wellformed03;

public class XercesDOM3Test extends TestSuite {
    public static TestSuite suite() throws Exception {
        DOMTestDocumentBuilderFactory factory = new JAXPDOMTestDocumentBuilderFactory(
                new DocumentBuilderFactoryImpl(),
                JAXPDOMTestDocumentBuilderFactory.getConfiguration1());

        FilteredDOMTestSuite suite = new FilteredDOMTestSuite(factory, new alltests(factory));
        suite.addExclude(documentgetinputencoding03.class);
        suite.addExclude(documentnormalizedocument07.class);
        suite.addExclude(documentnormalizedocument10.class);
        suite.addExclude(documentnormalizedocument11.class);
        suite.addExclude(domconfigurationcansetparameter06.class);
        suite.addExclude(elementgetschematypeinfo02.class);
        suite.addExclude(elementgetschematypeinfo07.class);
        suite.addExclude(entities02.class);
        suite.addExclude(entities03.class);
        suite.addExclude(infoset01.class);
        suite.addExclude(infoset02.class);
        suite.addExclude(infoset03.class);
        suite.addExclude(infoset07.class);
        suite.addExclude(nodegetbaseuri16.class);
        suite.addExclude(nodegettextcontent18.class);
        suite.addExclude(nodeisequalnode03.class);
        suite.addExclude(noderemovechild13.class);
        suite.addExclude(noderemovechild24.class);
        suite.addExclude(nodereplacechild38.class);
        suite.addExclude(textiselementcontentwhitespace05.class);
        suite.addExclude(typeinfogettypename04.class);
        suite.addExclude(typeinfoisderivedfrom15.class);
        suite.addExclude(typeinfoisderivedfrom16.class);
        suite.addExclude(typeinfoisderivedfrom17.class);
        suite.addExclude(typeinfoisderivedfrom18.class);
        suite.addExclude(typeinfoisderivedfrom19.class);
        suite.addExclude(typeinfoisderivedfrom21.class);
        suite.addExclude(typeinfoisderivedfrom40.class);
        suite.addExclude(typeinfoisderivedfrom41.class);
        suite.addExclude(typeinfoisderivedfrom58.class);
        suite.addExclude(typeinfoisderivedfrom59.class);
        suite.addExclude(typeinfoisderivedfrom66.class);
        suite.addExclude(typeinfoisderivedfrom67.class);
        suite.addExclude(typeinfoisderivedfrom68.class);
        suite.addExclude(typeinfoisderivedfrom73.class);
        suite.addExclude(wellformed03.class);
        return new JUnitTestSuiteAdapter(suite);
    }
}
