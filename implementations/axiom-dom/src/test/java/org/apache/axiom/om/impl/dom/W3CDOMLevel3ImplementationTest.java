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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.ts.dom.w3c.W3CTestCase;
import org.apache.axiom.ts.dom.w3c.level3.W3CDOMLevel3TestSuiteBuilder;

public class W3CDOMLevel3ImplementationTest extends TestCase {
    public static TestSuite suite() {
        W3CDOMLevel3TestSuiteBuilder builder = new W3CDOMLevel3TestSuiteBuilder(DOMTests.FACTORY, DOMTests.UNSUPPORTED_FEATURES);
        
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/canonicalform06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/checkcharacternormalization01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode12)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentadoptnode14)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentgetdoctype01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentgetdocumenturi01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentgetxmlstandalone05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentgetxmlversion03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument12)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument13)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentsetstricterrorchecking01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentsetstricterrorchecking02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentsetxmlversion01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentsetxmlversion05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigcanonicalform1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigcdatasections1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigcheckcharacternormalization1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigcomments1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigdatatypenormalization1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigelementcontentwhitespace1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigentities1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigerrorhandler1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigerrorhandler2)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfiginfoset1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfignamespacedeclarations1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfignamespaces1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfignormalizecharacters1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigparameternames01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigschemalocation1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigschematype1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigsplitcdatasections1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationcansetparameter01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationcansetparameter02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationcansetparameter04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationcansetparameter06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationgetparameter01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationgetparameter02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigvalidate1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigvalidateifschema1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigwellformed1)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domimplementationgetfeature01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domimplementationgetfeature02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domimplementationgetfeature03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domimplementationgetfeature05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domimplementationgetfeature06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domstringlistcontains01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domstringlistcontains02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domstringlistgetlength01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domstringlistitem01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domstringlistitem02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementcontentwhitespace01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributenode01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributenode04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributenode08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributenode09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens13)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementsetidattributens14)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/entities03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/handleerror01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/handleerror02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/hasFeature03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/hasFeature04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/namespacedeclarations01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/namespacedeclarations02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeappendchild01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition12)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition13)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition14)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition15)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition16)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition17)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition18)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition19)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition20)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition21)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition25)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition30)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition31)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition32)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition33)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition34)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition35)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition36)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition37)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition38)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition39)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodecomparedocumentposition40)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature09)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetfeature11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegettextcontent04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegettextcontent15)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegettextcontent16)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegettextcontent19)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeinsertbefore02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeinsertbefore05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeinsertbefore14)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeinsertbefore25)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeisequalnode05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeisequalnode10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeisequalnode11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/noderemovechild19)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodereplacechild13)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodereplacechild28)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodereplacechild31)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodereplacechild40)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodesettextcontent12)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodesetuserdata01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodesetuserdata02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/normalizecharacters01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/normalizecharacters03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/normalizecharacters05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/textreplacewholetext06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/textreplacewholetext08)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/userdatahandler01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/userdatahandler02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/userdatahandler03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/userdatahandler04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/wellformed01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/wellformed02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/wellformed03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/wellformed04)");
        
        return builder.build();
    }
}
