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
import org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOM;
import org.apache.axiom.ts.om.container.TestGetXMLStreamReader;
import org.apache.axiom.ts.om.container.TestSerialize;
import org.apache.axiom.ts.om.document.TestClone;
import org.apache.axiom.ts.om.document.TestDigest;
import org.apache.axiom.ts.om.element.TestGetChildrenWithName4;
import org.apache.axiom.ts.om.element.TestSerializationWithTwoNonBuiltOMElements;
import org.apache.axiom.ts.om.element.sr.TestClose;
import org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnChild;
import org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnChild;
import org.apache.axiom.ts.om.sourcedelement.TestGetSAXSourceWithPushOMDataSource;

public class OMImplementationTest extends TestCase {
    public static TestSuite suite() {
        OMTestSuiteBuilder builder = new OMTestSuiteBuilder(new OMDOMMetaFactory(), false);
        
        // TODO: Axiom should throw an exception if an attempt is made to create a cyclic parent-child relationship
        builder.exclude(TestInsertSiblingAfterOnChild.class);
        builder.exclude(TestInsertSiblingBeforeOnChild.class);
        
        // TODO: this case is not working because Axiom generates an XML declaration
        //       but uses another charset encoding to serialize the document
        builder.exclude(TestSerialize.class, "(&(file=iso-8859-1.xml)(container=document))");
        builder.exclude(TestCreateOMBuilderFromDOM.class, "(file=iso-8859-1.xml)");
        builder.exclude(TestClone.class, "(file=iso-8859-1.xml)");
        
        // TODO: investigate why this is not working with DOOM
        builder.exclude(TestGetChildrenWithName4.class);

        // TODO: if there is a comment node surrounded by text, then these text nodes need to be merged
        builder.exclude(TestDigest.class, "(|(file=digest3.xml)(file=digest4.xml))");
        
        // TODO
        builder.exclude(TestSerializationWithTwoNonBuiltOMElements.class);
        
        // TODO: need to evaluate if the test case is correct
        builder.exclude(TestGetSAXSourceWithPushOMDataSource.class, "(&(scenario=getNamespaceContext)(serializeParent=false))");
        
        // TODO: test case needing review
        builder.exclude(TestClose.class);
        
        // TODO: test issue: DOOM doesn't preserve attribute order
        builder.exclude(TestGetXMLStreamReader.class, "(&(file=large.xml))");
        
        return builder.build();
    }
}
