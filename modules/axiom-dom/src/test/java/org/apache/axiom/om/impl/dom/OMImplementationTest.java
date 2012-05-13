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

import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.apache.axiom.ts.om.OMTestSuiteBuilder;
import org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMSource;
import org.apache.axiom.ts.om.container.TestSerialize;
import org.apache.axiom.ts.om.document.TestDigest;
import org.apache.axiom.ts.om.element.TestGetChildrenWithName4;
import org.apache.axiom.ts.om.factory.TestCreateOMElementWithGeneratedPrefix;
import org.apache.axiom.ts.om.factory.TestCreateOMElementWithNamespaceInScope1;
import org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnChild;
import org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnChild;

public class OMImplementationTest extends TestCase {
    public static TestSuite suite() {
        OMTestSuiteBuilder builder = new OMTestSuiteBuilder(new OMDOMMetaFactory(), false);
        
        // TODO: Axiom should throw an exception if an attempt is made to create a cyclic parent-child relationship
        builder.exclude(TestInsertSiblingAfterOnChild.class);
        builder.exclude(TestInsertSiblingBeforeOnChild.class);
        
        // TODO: DOOM's behavior differs from LLOM's behavior in this case
        builder.exclude(TestCreateOMElementWithGeneratedPrefix.class, "(variant=QName*)");
        builder.exclude(TestCreateOMElementWithNamespaceInScope1.class, "(variant=QName,OMContainer)");
        builder.exclude(TestCreateOMElementWithNamespaceInScope1.class, "(variant=String,OMNamespace,OMContainer)");
        
        // TODO: this case is not working because Axiom generates an XML declaration
        //       but uses another charset encoding to serialize the document
        builder.exclude(TestSerialize.class, "(&(file=iso-8859-1.xml)(container=document))");
        
        // TODO: this case is not working because Axiom doesn't serialize the DTD
        builder.exclude(TestSerialize.class, "(&(file=spaces.xml)(container=document))");
        
        // TODO: CDATA sections are lost when using createOMBuilder with a DOMSource
        builder.exclude(TestCreateOMBuilderFromDOMSource.class, "(|(file=cdata.xml)(file=test.xml))");
        
        // TODO: suspecting Woodstox bug here
        builder.exclude(TestCreateOMBuilderFromDOMSource.class, "(file=spaces.xml)");
        
        // TODO: investigate why this is not working with DOOM
        builder.exclude(TestGetChildrenWithName4.class);

        // TODO: if there is a comment node surrounded by text, then these text nodes need to be merged
        builder.exclude(TestDigest.class, "(|(file=digest3.xml)(file=digest4.xml))");
        
        return builder.build();
    }
}
