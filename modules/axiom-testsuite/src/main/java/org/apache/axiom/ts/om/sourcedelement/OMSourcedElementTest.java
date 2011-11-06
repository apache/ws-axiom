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
package org.apache.axiom.ts.om.sourcedelement;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests the characteristics of OMSourcedElementImpl. */
public abstract class OMSourcedElementTest extends AxiomTestCase {
    protected static String testDocument =
            "<library xmlns=\"http://www.sosnoski.com/uwjws/library\" books=\"1\">" +
                    "<type id=\"java\" category=\"professional\" deductable=\"true\">" +
                    "<name>Java Reference</name></type><type id=\"xml\" " +
                    "category=\"professional\" deductable=\"true\"><name>XML Reference</name>" +
                    "</type><book isbn=\"1930110111\" type=\"xml\"><title>XSLT Quickly</title>" +
                    "<author>DuCharme, Bob</author><publisher>Manning</publisher>" +
                    "<price>29.95</price></book></library>";

    // Same as testDocument except that an non-default prefix is used
    protected static String testDocument2 =
            "<pre:library xmlns:pre=\"http://www.sosnoski.com/uwjws/library\" books=\"1\">" +
                    "<pre:type id=\"java\" category=\"professional\" deductable=\"true\">" +
                    "<pre:name>Java Reference</pre:name></pre:type><pre:type id=\"xml\" " +
                    "category=\"professional\" deductable=\"true\"><pre:name>XML Reference</pre:name>" +
                    "</pre:type><pre:book isbn=\"1930110111\" type=\"xml\"><pre:title>XSLT Quickly</pre:title>" +
                    "<pre:author>DuCharme, Bob</pre:author><pre:publisher>Manning</pre:publisher>" +
                    "<pre:price>29.95</pre:price></pre:book></pre:library>";

    // Same as testDocument exception that the elements are unqualified
    protected static String testDocument3 =
            "<library books=\"1\">" +
                    "<type id=\"java\" category=\"professional\" deductable=\"true\">" +
                    "<name>Java Reference</name></type><type id=\"xml\" " +
                    "category=\"professional\" deductable=\"true\"><name>XML Reference</name>" +
                    "</type><book isbn=\"1930110111\" type=\"xml\"><title>XSLT Quickly</title>" +
                    "<author>DuCharme, Bob</author><publisher>Manning</publisher>" +
                    "<price>29.95</price></book></library>";

    protected OMSourcedElement element;
    protected OMElement root;

    public OMSourcedElementTest(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void setUp() throws Exception {
        OMFactory f = metaFactory.getOMFactory();
        OMNamespace ns = f.createOMNamespace("http://www.sosnoski.com/uwjws/library", "");
        OMNamespace rootNS = f.createOMNamespace("http://sampleroot", "rootPrefix");
        element = f.createOMElement(new TestDataSource(testDocument), "library", ns);
        root = f.createOMElement("root", rootNS);
        root.addChild(element);
    }
}