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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.ParameterBinding;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.apache.axiom.ts.xml.XMLSample;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class DOMTestSuite {
    private static final ImmutableList<QName> VALID_ATTR_QNAMES =
            ImmutableList.of(
                    new QName("urn:ns2", "attr", "q"),
                    new QName("", "attr", ""),
                    new QName(
                            XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                            "ns",
                            XMLConstants.XMLNS_ATTRIBUTE),
                    new QName(
                            XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, ""));

    private static final ImmutableList<QName> INVALID_ATTR_QNAMES =
            ImmutableList.of(
                    new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "attr", ""),
                    new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "attr", "p"),
                    new QName("urn:test", "p", XMLConstants.XMLNS_ATTRIBUTE),
                    new QName("", XMLConstants.XMLNS_ATTRIBUTE, ""));

    private static final ParameterBinding<QName> QNAME_PARAMS =
            (injector, qname, params) -> {
                params.addTestParameter("ns", qname.getNamespaceURI());
                params.addTestParameter("name", DOMUtils.getQualifiedName(qname));
            };

    public static InjectorNode create(DocumentBuilderFactoryFactory dbff) {
        DocumentBuilderFactory dbf = dbff.newInstance();
        dbf.setNamespaceAware(true);
        return new InjectorNode(
                binder -> binder.bind(DocumentBuilderFactory.class).toInstance(dbf),
                new ParentNode(
                        new MatrixTest(org.apache.axiom.ts.dom.attr.TestGetChildNodes.class),
                        new MatrixTest(org.apache.axiom.ts.dom.attr.TestGetFirstChild.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.attr.TestGetNamespaceURIWithNoNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.attr.TestGetValueWithMultipleChildren
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.attr
                                        .TestLookupNamespaceURIWithoutOwnerElement.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.attr.TestSetPrefixNotNullWithNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.attr.TestSetPrefixNotNullWithoutNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.attr.TestSetPrefixNullWithNamespace.class),
                        new MatrixTest(org.apache.axiom.ts.dom.builder.TestParseURI.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.builder.TestWhitespaceAroundDocumentElement
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestAdoptNode.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestAdoptNodeToSameDocument.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestAdoptNodeWithParent.class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestAllowedChildren.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document
                                        .TestAppendChildForeignImplementation.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestAppendChildWrongDocument
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestCreateAttribute.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document
                                        .TestCreateAttributeNSWithoutNamespace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestCreateCDATASection.class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestCreateElement.class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestCreateElementNS.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestCreateElementNSWithInvalidName
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestCreateElementNSWithoutNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document
                                        .TestCreateElementNSWithSupplementaryCharacter.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document
                                        .TestCreateElementWithSupplementaryCharacter.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestCreateEntityReference.class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestCreateText.class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestDocumentSiblings.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestGetDomConfigDefaults.class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestGetOwnerDocument.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document
                                        .TestGetXmlVersionFromParsedDocumentWithoutDeclaration
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestLookupNamespaceURI.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document
                                        .TestLookupNamespaceURIWithEmptyDocument.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestLookupPrefixWithEmptyDocument
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.document.TestNormalizeDocumentNamespace
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.document.TestValidator.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.documentfragment.TestCloneNodeDeep.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.documentfragment.TestCloneNodeShallow
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.documentfragment.TestLookupNamespaceURI
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.documentfragment.TestLookupPrefix.class),
                        new MatrixTest(org.apache.axiom.ts.dom.documenttype.TestWithParser1.class),
                        new MatrixTest(org.apache.axiom.ts.dom.documenttype.TestWithParser2.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAppendChild.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAppendChildCyclic.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAppendChildSelf.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestAppendChildWrongDocument.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAttributes.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAttributes2.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAttributes3.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestAttributes4.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestCloneNode.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestCloneNodeWithAttributeHavingMultipleChildren.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestGetElementsByTagName.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestGetElementsByTagNameNS.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestGetElementsByTagNameRecursive
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestGetElementsByTagNameWithNamespaces.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestGetElementsByTagNameWithWildcard
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestGetNamespaceURIWithNoNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestGetPrefixWithDefaultNamespace
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestGetTextContent.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestInsertBefore.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestInsertBeforeWithDocumentFragment
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestLookupNamespaceURIDefaultBindings.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupNamespaceURIExplicit
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupNamespaceURIImplicit
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestLookupNamespaceURINamespaceDeclarationAsNSUnawareAttribute
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupNamespaceURIXercesJ1586
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupPrefixDefaultBindings
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupPrefixEmptyNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupPrefixExplicitMasked
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestLookupPrefixImplicitMasked
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestRemoveAttributeNodeForeignImplementation.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestRemoveAttributeNodeNotOwner
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestRemoveFirstChild.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestRemoveLastChild.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestRemoveSingleChild.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestReplaceChildCyclic.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestReplaceChildFirstWithDocumentFragment.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestReplaceChildLastWithDocumentFragment.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestReplaceChildMiddleWithDocumentFragment.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestReplaceChildNotFound.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestReplaceChildNullNewChild.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestReplaceChildWrongDocument
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestSetAttributeNodeNSForeignImplementation.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSInUse.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSReplace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetAttributeNodeNSWrongDocument
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetAttributeNodeWrongDocument
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetAttributeNSExisting.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestSetAttributeNSExistingDefaultNamespaceDeclaration
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetPrefixNotNullWithNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.TestSetPrefixNotNullWithoutNamespace
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestSetPrefixNull.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element
                                        .TestSetPrefixWithSupplementaryCharacter.class),
                        new MatrixTest(org.apache.axiom.ts.dom.element.TestSetTextContent.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.attributes
                                        .TestSetNamedItemNSWrongDocument.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.element.attributes
                                        .TestSetNamedItemWrongDocument.class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestAppendData.class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestGetChildNodes.class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestGetLength.class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestGetWholeText.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.text.TestGetWholeTextWithCDATASection
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.text.TestGetWholeTextWithComment.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.text.TestLookupNamespaceURIWithoutParent
                                        .class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestReplaceDataAppend.class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestSetPrefix.class),
                        new MatrixTest(org.apache.axiom.ts.dom.text.TestSplitText.class),
                        new MatrixTest(
                                org.apache.axiom.ts.dom.text.TestSplitTextWithoutParent.class),
                        new FanOutNode<>(
                                ImmutableList.of(true, false),
                                Binding.singleton(Key.get(Boolean.class, Names.named("deep"))),
                                (injector, value, params) -> params.addTestParameter("deep", value),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.attr.TestCloneNode.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element
                                                        .TestCloneNodeWithAttributes.class))),
                        new FanOutNode<>(
                                ImmutableList.of(true, false),
                                Binding.singleton(
                                        Key.get(Boolean.class, Names.named("newChildHasSiblings"))),
                                (injector, value, params) ->
                                        params.addTestParameter("newChildHasSiblings", value),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element
                                                        .TestReplaceChildFirst.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element.TestReplaceChildLast
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element
                                                        .TestReplaceChildMiddle.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element
                                                        .TestReplaceChildSingle.class))),
                        new FanOutNode<>(
                                VALID_ATTR_QNAMES,
                                Binding.singleton(Key.get(QName.class)),
                                QNAME_PARAMS,
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.document
                                                        .TestCreateAttributeNS.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element.TestSetAttributeNS
                                                        .class))),
                        new FanOutNode<>(
                                INVALID_ATTR_QNAMES,
                                Binding.singleton(Key.get(QName.class)),
                                QNAME_PARAMS,
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.document
                                                        .TestCreateAttributeNSInvalid.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.element
                                                        .TestSetAttributeNSInvalid.class))),
                        new FanOutNode<>(
                                Multiton.getInstances(XMLSample.class),
                                Binding.singleton(Key.get(XMLSample.class)),
                                (injector, value, params) ->
                                        params.addTestParameter("file", value.getName()),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.document.TestCloneNode
                                                        .class),
                                        new FanOutNode<>(
                                                Multiton.getInstances(DOMImplementation.class),
                                                Binding.singleton(Key.get(DOMImplementation.class)),
                                                (injector, value, params) ->
                                                        params.addTestParameter(
                                                                "from", value.getName()),
                                                new MatrixTest(
                                                        org.apache.axiom.ts.dom.element
                                                                .TestImportNode.class)))),
                        new FanOutNode<>(
                                Multiton.getInstances(XSLTImplementation.class),
                                Binding.singleton(Key.get(XSLTImplementation.class)),
                                (injector, value, params) ->
                                        params.addTestParameter("xslt", value.getName()),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.document
                                                        .TestTransformerWithIdentityStylesheet
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.dom.document
                                                        .TestTransformerWithStylesheet.class)))));
    }
}
