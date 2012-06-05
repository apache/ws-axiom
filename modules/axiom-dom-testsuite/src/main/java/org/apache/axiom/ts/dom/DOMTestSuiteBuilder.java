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

import org.apache.axiom.testutils.suite.TestSuiteBuilder;

public class DOMTestSuiteBuilder extends TestSuiteBuilder {
    private final DocumentBuilderFactory dbf;
    
    public DOMTestSuiteBuilder(DocumentBuilderFactory dbf) {
        this.dbf = dbf;
    }
    
    protected void addTests() {
        addTest(new org.apache.axiom.ts.dom.attr.TestCloneNode(dbf, true));
        addTest(new org.apache.axiom.ts.dom.attr.TestCloneNode(dbf, false));
        addTest(new org.apache.axiom.ts.dom.attr.TestGetChildNodes(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestGetFirstChild(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestGetValueWithMultipleChildren(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestSetPrefixNotNullWithNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.attr.TestSetPrefixNotNullWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.builder.TestWhitespaceAroundDocumentElement(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAdoptNode(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAdoptNodeToSameDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAdoptNodeWithParent(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAllowedChildren(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestAppendChildWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateAttribute(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateAttributeNS(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateAttributeNSWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateCDATASection(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElement(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElementNS(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElementNSWithInvalidName(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateElementNSWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestCreateText(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestDocumentSiblings(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestGetDomConfigDefaults(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestGetOwnerDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestNormalizeDocumentNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.document.TestTransformerWithIdentityStylesheet(dbf, org.apache.xalan.processor.TransformerFactoryImpl.class));
        addTest(new org.apache.axiom.ts.dom.document.TestTransformerWithIdentityStylesheet(dbf, net.sf.saxon.TransformerFactoryImpl.class));
        addTest(new org.apache.axiom.ts.dom.document.TestTransformerWithStylesheet(dbf, org.apache.xalan.processor.TransformerFactoryImpl.class));
        addTest(new org.apache.axiom.ts.dom.document.TestTransformerWithStylesheet(dbf, net.sf.saxon.TransformerFactoryImpl.class));
        addTest(new org.apache.axiom.ts.dom.document.TestValidator(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChildCyclic(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChildSelf(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAppendChildWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes2(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes3(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestAttributes4(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNode(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNodeWithAttributeHavingMultipleChildren(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNodeWithAttributes(dbf, true));
        addTest(new org.apache.axiom.ts.dom.element.TestCloneNodeWithAttributes(dbf, false));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagName(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameNS(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameRecursive(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameWithNamespaces(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetElementsByTagNameWithWildcard(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetNamespaceURIWithNoNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetPrefixWithDefaultNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestGetTextContent(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestInsertBefore(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestInsertBeforeWithDocumentFragment(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveAttributeNotOwner(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveFirstChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveLastChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestRemoveSingleChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChild(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestReplaceChildWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSReplace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetAttributeNodeWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetPrefixNotNullWithNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetPrefixNotNullWithoutNamespace(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetPrefixNull(dbf));
        addTest(new org.apache.axiom.ts.dom.element.TestSetTextContent(dbf));
        addTest(new org.apache.axiom.ts.dom.element.attributes.TestSetNamedItemNSWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.element.attributes.TestSetNamedItemWrongDocument(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestAppendData(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetChildNodes(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetLength(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetWholeText(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetWholeTextWithCDATASection(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestGetWholeTextWithComment(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestSetPrefix(dbf));
        addTest(new org.apache.axiom.ts.dom.text.TestSplitText(dbf));
    }
}
