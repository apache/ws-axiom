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
package org.apache.axiom.ts.om;

import java.lang.reflect.Method;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.testutils.suite.TestSuiteBuilder;
import org.apache.axiom.ts.om.container.OMContainerFactory;
import org.apache.axiom.ts.om.container.OMElementFactory;
import org.apache.axiom.ts.om.container.SerializationMethod;
import org.apache.axiom.ts.om.container.SerializeToOutputStream;
import org.apache.axiom.ts.om.factory.CreateOMElementParentSupplier;
import org.apache.axiom.ts.om.factory.CreateOMElementVariant;
import org.apache.axiom.ts.om.xpath.AXIOMXPathTestCase;
import org.apache.axiom.ts.om.xpath.TestAXIOMXPath;

public class OMTestSuiteBuilder extends TestSuiteBuilder {
    private static final OMContainerFactory[] containerFactories = {
        OMContainerFactory.DOCUMENT,
        new OMElementFactory(false),
        new OMElementFactory(true) };
    
    private static final SerializationMethod[] serializationMethods = {
        new SerializeToOutputStream(true),
        new SerializeToOutputStream(false) };
    
    private final OMMetaFactory metaFactory;
    private final boolean supportsOMSourcedElement;
    
    public OMTestSuiteBuilder(OMMetaFactory metaFactory, boolean supportsOMSourcedElement) {
        this.metaFactory = metaFactory;
        this.supportsOMSourcedElement = supportsOMSourcedElement;
    }
    
    protected void addTests() {
        String[] conformanceFiles = AbstractTestCase.getConformanceTestFiles();
        addTest(new org.apache.axiom.ts.om.attribute.TestDigestWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestDigestWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestEqualsHashCode(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetNamespaceNormalized(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetNamespaceURIWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetNamespaceURIWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetPrefixWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetPrefixWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQNameWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestSetLocalName(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCloseWithInputStream(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCloseWithReader(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCloseWithXMLStreamReader(metaFactory));
        for (int i=0; i<conformanceFiles.length; i++) {
            addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMSource(metaFactory, conformanceFiles[i]));
            addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromSAXSource(metaFactory, conformanceFiles[i]));
        }
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderFromFragment(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderIncorrectState(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderNamespaceRepairing(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderNamespaceRepairing2(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElementWithDiscardDocument(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElementWithIllFormedDocument(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestInvalidXML(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestIOExceptionInGetText(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestReadAttachmentBeforeRootPartComplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestRootPartStreaming(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestStandaloneConfiguration(metaFactory));
        for (int i=0; i<conformanceFiles.length; i++) {
            for (int j=0; j<containerFactories.length; j++) {
                addTest(new org.apache.axiom.ts.om.container.TestGetSAXSource(metaFactory, conformanceFiles[i], containerFactories[j], true));
                addTest(new org.apache.axiom.ts.om.container.TestGetSAXSource(metaFactory, conformanceFiles[i], containerFactories[j], false));
                addTest(new org.apache.axiom.ts.om.container.TestGetXMLStreamReader(metaFactory, conformanceFiles[i], containerFactories[j], true));
                addTest(new org.apache.axiom.ts.om.container.TestGetXMLStreamReader(metaFactory, conformanceFiles[i], containerFactories[j], false));
                for (int k=0; k<serializationMethods.length; k++) {
                    addTest(new org.apache.axiom.ts.om.container.TestSerialize(metaFactory, conformanceFiles[i], containerFactories[j], serializationMethods[k]));
                }
            }
        }
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest1.xml", "MD5", "3e5d68c6607bc56c9c171560e4f19db9"));
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest2.xml", "SHA1", "3c47a807517d867d42ffacb2d3e9da81895d5aac"));
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest3.xml", "SHA", "41466144c1cab4234fb127cfb8cf92f9"));
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest4.xml", "SHA", "be3b0836cd6f0ceacdf3d40b49a0468d03d2ba2e"));
        addTest(new org.apache.axiom.ts.om.document.TestGetOMDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestGetOMDocumentElementAfterDetach(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestGetOMDocumentElementWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsume(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementNew(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementNull(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementReplace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByOtherElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeFromOMAttributeMultiple(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeFromOMAttributeWithExistingName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeReplace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeReplace2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationInScope(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationOnSameElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithInvalidNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithMaskedNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutExistingNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChild2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithSameParent(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithSameParent(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestChildReDeclaringGrandParentsDefaultNSWithPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestChildReDeclaringParentsDefaultNSWithPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespace2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespaceConflict1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespaceConflict2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceInvalid1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceInvalid2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix3(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDigestWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDigestWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceByNamespaceURIMasked(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceByPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceCaseSensitivity(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceURIWithPrefixUndeclaring(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllAttributes1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllAttributes2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespaces(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespacesRemove(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueNonExisting(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElements(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElementsConcurrentModification(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenConcurrentModification(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove3(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove4(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithLocalName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithName2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithName3(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithName4(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithNameNextWithoutHasNext(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithNamespaceURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetDefaultNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetDefaultNamespace2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetDescendants(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetDescendants(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithNameOnIncompleteElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceContext(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceContext(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceNormalized(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceNormalized(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceNormalizedWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceNormalizedWithSAXSource(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespacesInScope(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespacesInScopeWithDefaultNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespacesInScopeWithMaskedDefaultNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespacesInScopeWithMaskedNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceURIWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceURIWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetPrefixWithDefaultNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetPrefixWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetPrefixWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetQNameWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetQNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetText(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQNameEmpty(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQNameNoNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQNameWithExtraWhitespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsStreamWithNonTextChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsStreamWithoutCaching(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsStreamWithSingleTextNode(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextWithCDATASectionChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextWithMixedOMTextChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCommentEvent(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderGetElementText(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderGetNamespaceContext(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderGetNamespaceContext(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderNextTag(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElement(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElement(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithNamespaceURIInterning(metaFactory));
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithOMSourcedElementDescendant(metaFactory));
        }
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithoutCachingPartiallyBuilt(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithPreserveNamespaceContext(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestMultipleDefaultNS(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestRemoveAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestRemoveAttributeNotOwner(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestResolveQNameWithDefaultNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestResolveQNameWithNonDefaultNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestResolveQNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestResolveQNameWithUnboundPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "D", "D",
                "<person xmlns=\"urn:ns\"><name>John</name><age>34</age><weight>50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "D", "U",
                "<person xmlns=\"urn:ns\"><name xmlns=\"\">John</name><age xmlns=\"\">34</age><weight xmlns=\"\">50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "D", "Q",
                "<person xmlns=\"urn:ns\"><p:name xmlns:p=\"urn:ns\">John</p:name><p:age xmlns:p=\"urn:ns\">34</p:age><p:weight xmlns:p=\"urn:ns\">50</p:weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "Q", "Q",
                "<p:person xmlns:p=\"urn:ns\"><p:name>John</p:name><p:age>34</p:age><p:weight>50</p:weight></p:person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "Q", "U",
                "<p:person xmlns:p=\"urn:ns\"><name>John</name><age>34</age><weight>50</weight></p:person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "Q", "D",
                "<p:person xmlns:p=\"urn:ns\"><name xmlns=\"urn:ns\">John</name><age xmlns=\"urn:ns\">34</age><weight xmlns=\"urn:ns\">50</weight></p:person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "U", "U",
                "<person><name>John</name><age>34</age><weight>50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "U", "Q",
                "<person><p:name xmlns:p=\"urn:ns\">John</p:name><p:age xmlns:p=\"urn:ns\">34</p:age><p:weight xmlns:p=\"urn:ns\">50</p:weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "U", "D",
                "<person><name xmlns=\"urn:ns\">John</name><age xmlns=\"urn:ns\">34</age><weight xmlns=\"urn:ns\">50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerializationWithTwoNonBuiltOMElements(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumePartiallyBuilt(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetNamespaceInvalid(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetNamespaceWithMatchingBindingInScope(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetNamespaceWithNullOMNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetNamespaceWithNullPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetText(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextEmptyString(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextNull(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextWithExistingChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQNameNull(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQNameWithEmptyPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQNameWithExistingChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestUndeclarePrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestWriteTextTo(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestWriteTextToWithNonTextNodes(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeGeneratedPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeNullPrefixNoNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeWithInvalidNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMCommentWithoutParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMDocument(metaFactory));
        for (int i=0; i<CreateOMElementVariant.INSTANCES.length; i++) {
            CreateOMElementVariant variant = CreateOMElementVariant.INSTANCES[i];
            for (int j=0; j<CreateOMElementParentSupplier.INSTANCES.length; j++) {
                CreateOMElementParentSupplier parentSupplier = CreateOMElementParentSupplier.INSTANCES[j];
                if (parentSupplier.isSupported(variant)) {
                    if (variant.isSupportsDefaultNamespace()) {
                        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithDefaultNamespace(metaFactory, variant, parentSupplier));
                    }
                    addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithGeneratedPrefix(metaFactory, variant, parentSupplier));
                    addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespaceNullPrefix(metaFactory, variant, parentSupplier));
                    addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithInvalidNamespace(metaFactory, variant, parentSupplier));
                    addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNonDefaultNamespace(metaFactory, variant, parentSupplier));
                    addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespace(metaFactory, variant, parentSupplier));
                }
            }
            if (variant.isSupportsContainer()) {
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNamespaceInScope(metaFactory, variant));
            }
            if (variant.isSupportsContainer() && variant.isSupportsDefaultNamespace()) {
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespace2(metaFactory, variant));
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespace3(metaFactory, variant));
            }
        }
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullOMDataSource1(metaFactory));
            addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullOMDataSource2(metaFactory));
        }
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullURIAndPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMNamespaceWithNullURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMProcessingInstructionWithoutParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMText(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextFromDataHandlerProvider(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextWithNullParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestFactoryIsSingleton(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestGetMetaFactory(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestEquals(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestEqualsWithNullPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestGetNamespaceURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestGetPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestHashCode(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestObjectEquals(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestObjectEqualsWithDifferentPrefixes(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestObjectEqualsWithDifferentURIs(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.node.TestDetachFirstChild(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.node.TestDetachFirstChild(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfter(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterLastChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnSelf(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBefore(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnSelf(metaFactory));
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestComplete(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestExpand(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceNormalized(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceNormalized2(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetTextAsStreamWithNonDestructiveOMDataSource(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName1DefaultPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName1QualifiedPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName1Unqualified(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName2DefaultPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName2QualifiedPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName2Unqualified(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName3DefaultPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName3QualifiedPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName3Unqualified(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName4DefaultPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName4QualifiedPrefix(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestName4Unqualified(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeAndConsumeToStream(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeAndConsumeToWriter(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeAndConsumeToXMLWriter(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeAndConsumeToXMLWriterEmbedded(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeModifiedOMSEWithNonDestructiveDataSource(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeToStream(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeToWriter(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeToXMLWriter(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeToXMLWriterEmbedded(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeToXMLWriterFromReader(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeToXMLWriterFromReaderEmbedded(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetDataSource(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetDataSourceOnAlreadyExpandedElement(metaFactory));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetLocalName(metaFactory, false));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetLocalName(metaFactory, true));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestWriteTextToWithNonDestructiveOMDataSource(metaFactory));
        }
        addTest(new org.apache.axiom.ts.om.pi.TestDigest(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestBase64Streaming(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestDigest(metaFactory));
        Method[] methods = AXIOMXPathTestCase.class.getMethods();
        for (int i=0; i<methods.length; i++) {
            String methodName = methods[i].getName();
            if (methodName.startsWith("test")) {
                addTest(new TestAXIOMXPath(metaFactory, methodName));
            }
        }
        addTest(new org.apache.axiom.ts.om.xpath.TestAddNamespaces(metaFactory));
        addTest(new org.apache.axiom.ts.om.xpath.TestAddNamespaces2(metaFactory));
        addTest(new org.apache.axiom.ts.om.xpath.TestGetAttributeQName(metaFactory));
    }
}
