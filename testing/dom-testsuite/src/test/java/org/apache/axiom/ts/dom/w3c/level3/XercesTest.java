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
package org.apache.axiom.ts.dom.w3c.level3;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.ts.dom.DocumentBuilderFactoryFactory;
import org.apache.axiom.ts.dom.w3c.W3CTestCase;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

public class XercesTest extends TestCase {
    public static TestSuite suite() {
        W3CDOMLevel3TestSuiteBuilder builder = new W3CDOMLevel3TestSuiteBuilder(new DocumentBuilderFactoryFactory() {
            @Override
            public DocumentBuilderFactory newInstance() {
                return new DocumentBuilderFactoryImpl();
            }
        });
        
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentgetinputencoding03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument10)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/documentnormalizedocument11)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/domconfigurationcansetparameter06)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementgetschematypeinfo02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/elementgetschematypeinfo07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/entities02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/entities03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset01)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset02)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/infoset07)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegetbaseuri16)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodegettextcontent18)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodeisequalnode03)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/noderemovechild13)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/noderemovechild24)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/nodereplacechild38)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/textiselementcontentwhitespace05)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfogettypename04)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom15)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom16)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom17)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom18)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom19)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom21)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom40)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom41)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom58)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom59)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom66)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom67)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom68)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/typeinfoisderivedfrom73)");
        builder.exclude(W3CTestCase.class, "(id=http://www.w3.org/2001/DOM-Test-Suite/level3/core/wellformed03)");
        
        return builder.build();
    }
}
