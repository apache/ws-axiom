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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;
import org.apache.axiom.ts.om.sourcedelement.util.PushOMDataSource;

/**
 * Defines a set of test documents used to create {@link OMDataSource} and {@link OMSourcedElement}
 * instances.
 */
final class TestDocument {
    static final TestDocument DOCUMENT1 =
            new TestDocument(
                    "<library xmlns=\"http://www.sosnoski.com/uwjws/library\" books=\"1\">"
                            + "<type id=\"java\" category=\"professional\" deductable=\"true\">"
                            + "<name>Java Reference</name></type><type id=\"xml\" "
                            + "category=\"professional\" deductable=\"true\"><name>XML Reference</name>"
                            + "</type><book isbn=\"1930110111\" type=\"xml\"><title>XSLT Quickly</title>"
                            + "<author>DuCharme, Bob</author><publisher>Manning</publisher>"
                            + "<price>29.95</price></book></library>",
                    new QName("http://www.sosnoski.com/uwjws/library", "library", ""));

    // Same as DOCUMENT1 except that an non-default prefix is used
    static final TestDocument DOCUMENT2 =
            new TestDocument(
                    "<pre:library xmlns:pre=\"http://www.sosnoski.com/uwjws/library\" books=\"1\">"
                            + "<pre:type id=\"java\" category=\"professional\" deductable=\"true\">"
                            + "<pre:name>Java Reference</pre:name></pre:type><pre:type id=\"xml\" "
                            + "category=\"professional\" deductable=\"true\"><pre:name>XML Reference</pre:name>"
                            + "</pre:type><pre:book isbn=\"1930110111\" type=\"xml\"><pre:title>XSLT Quickly</pre:title>"
                            + "<pre:author>DuCharme, Bob</pre:author><pre:publisher>Manning</pre:publisher>"
                            + "<pre:price>29.95</pre:price></pre:book></pre:library>",
                    new QName("http://www.sosnoski.com/uwjws/library", "library", "pre"));

    // Same as DOCUMENT1 except that the elements are unqualified
    static final TestDocument DOCUMENT3 =
            new TestDocument(
                    "<library books=\"1\">"
                            + "<type id=\"java\" category=\"professional\" deductable=\"true\">"
                            + "<name>Java Reference</name></type><type id=\"xml\" "
                            + "category=\"professional\" deductable=\"true\"><name>XML Reference</name>"
                            + "</type><book isbn=\"1930110111\" type=\"xml\"><title>XSLT Quickly</title>"
                            + "<author>DuCharme, Bob</author><publisher>Manning</publisher>"
                            + "<price>29.95</price></book></library>",
                    new QName("library"));

    private final String content;
    private final QName qname;

    private TestDocument(String content, QName qname) {
        this.content = content;
        this.qname = qname;
    }

    String getContent() {
        return content;
    }

    OMSourcedElement createOMSourcedElement(OMFactory factory, boolean push, boolean destructive) {
        OMNamespace ns = factory.createOMNamespace(qname.getNamespaceURI(), qname.getPrefix());
        OMDataSource ds;
        if (push) {
            ds = new PushOMDataSource(factory, getContent(), destructive);
        } else {
            ds = new PullOMDataSource(getContent(), destructive);
        }
        return factory.createOMElement(ds, qname.getLocalPart(), ns);
    }
}
