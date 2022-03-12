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
package org.apache.axiom.ts.dom;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.ts.dom.document.TestLookupNamespaceURIWithEmptyDocument;
import org.apache.axiom.ts.dom.document.TestLookupPrefixWithEmptyDocument;
import org.apache.axiom.ts.dom.element.TestImportNode;
import org.apache.axiom.ts.dom.element.TestLookupNamespaceURIXercesJ1586;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

public class XercesTest extends TestCase {
    public static TestSuite suite() {
        DOMTestSuiteBuilder builder = new DOMTestSuiteBuilder(new DocumentBuilderFactoryFactory() {
            @Override
            public DocumentBuilderFactory newInstance() {
                return new DocumentBuilderFactoryImpl();
            }
        });
        
        // XERCESJ-1582
        builder.exclude(TestLookupNamespaceURIWithEmptyDocument.class);
        builder.exclude(TestLookupPrefixWithEmptyDocument.class);
        
        // XERCESJ-1586
        builder.exclude(TestLookupNamespaceURIXercesJ1586.class);
        
        // Xerces has difficulties importing nodes from Crimson
        builder.exclude(TestImportNode.class, "(from=crimson)");
        
        return builder.build();
    }
}
