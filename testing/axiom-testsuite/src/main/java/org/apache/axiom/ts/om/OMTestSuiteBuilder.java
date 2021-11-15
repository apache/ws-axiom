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

import static org.apache.axiom.testing.multiton.Multiton.getInstances;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.dimension.AddAttributeStrategy;
import org.apache.axiom.ts.dimension.BuilderFactory;
import org.apache.axiom.ts.dimension.ElementContext;
import org.apache.axiom.ts.dimension.ExpansionStrategy;
import org.apache.axiom.ts.dimension.NoNamespaceStrategy;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.jaxp.sax.SAXImplementation;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.apache.axiom.ts.om.container.OMContainerExtractor;
import org.apache.axiom.ts.om.container.OMContainerFactory;
import org.apache.axiom.ts.om.factory.CreateOMElementParentSupplier;
import org.apache.axiom.ts.om.factory.CreateOMElementVariant;
import org.apache.axiom.ts.om.sourcedelement.OMSourcedElementVariant;
import org.apache.axiom.ts.om.sourcedelement.push.PushOMDataSourceScenario;
import org.apache.axiom.ts.om.xpath.AXIOMXPathTestCase;
import org.apache.axiom.ts.om.xpath.TestAXIOMXPath;
import org.apache.axiom.ts.xml.StreamType;
import org.apache.axiom.ts.xml.XMLSample;
import org.apache.axiom.ts.xml.XOPSample;

public class OMTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private static final QName[] qnames = {
        new QName("root"),
        new QName("urn:test", "root", "p"),
        new QName("urn:test", "root") };
    
    private final OMMetaFactory metaFactory;
    
    public OMTestSuiteBuilder(OMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
        // This test is particularly slow because it checks all XMLStreamReader methods on
        // every event and many of these invocations throw exceptions. Exclude it because
        // it doesn't add value (with respect to the other TestGetXMLStreamReader runs.
        exclude(org.apache.axiom.ts.om.container.TestGetXMLStreamReader.class, "(file=large.xml)");
    }
    
    @Override
    protected void addTests() {
        addTest(new org.apache.axiom.ts.om.attribute.TestDigestWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestDigestWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetAttributeTypeDefault(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetNamespaceNormalized(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetNamespaceURIWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetNamespaceURIWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetPrefixWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetPrefixWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQNameWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestHasNameWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestHasNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestSetLocalName(metaFactory));
        for (int i=0; i<4; i++) {
            boolean declare = (i & 1) != 0;
            boolean owner = (i & 2) != 0;
            // Valid
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", "p", declare, owner, null, false, "p", declare && owner));
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", null, declare, owner, null, false, null, declare && owner));
            if (owner) {
                addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", "p", declare, true, "p", false, "p", false));
                addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", "p", declare, true, "q", false, "p", declare));
                addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", null, declare, true, "p", false, "p", false));
                addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", null, declare, true, "", false, null, declare));
            }
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "", "", declare, owner, null, false, "", false));
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "", null, declare, owner, null, false, "", false));
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, null, null, declare, owner, null, false, "", false));
            // Invalid
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "urn:test", "", declare, owner, null, true, null, false));
            addTest(new org.apache.axiom.ts.om.attribute.TestSetNamespace(metaFactory, "", "p", declare, owner, null, true, null, false));
        }
        for (StreamType streamType : Multiton.getInstances(StreamType.class)) {
            addTest(new org.apache.axiom.ts.om.builder.TestCloseWithStream(metaFactory, streamType));
        }
        addTest(new org.apache.axiom.ts.om.builder.TestCloseWithSystemId(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCloseWithXMLStreamReader(metaFactory));
        for (XMLSample file : getInstances(XMLSample.class)) {
            for (DOMImplementation implementation : getInstances(DOMImplementation.class)) {
                addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOM(metaFactory, file, implementation, Boolean.TRUE));
                addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOM(metaFactory, file, implementation, Boolean.FALSE));
                addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOM(metaFactory, file, implementation, null));
            }
            for (SAXImplementation implementation : getInstances(SAXImplementation.class)) {
                if (!file.hasExternalSubset() || implementation.reportsExternalSubsetEntity()) {
                    addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromSAXSource(metaFactory, file, implementation, Boolean.TRUE));
                    addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromSAXSource(metaFactory, file, implementation, Boolean.FALSE));
                    addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromSAXSource(metaFactory, file, implementation, null));
                }
            }
        }
        addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMWithNSUnawareNamespaceDeclaration(metaFactory, ""));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMWithNSUnawareNamespaceDeclaration(metaFactory, "p"));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMWithNSUnawarePrefixedAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMWithNSUnawareUnprefixedAttribute(metaFactory));
        for (XOPSample sample : getInstances(XOPSample.class)) {
            addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderXOP(metaFactory, sample, false));
            addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderXOP(metaFactory, sample, true));
        }
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderFromFragment(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderFromXmlBeansPullParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderIncorrectState(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderNamespaceRepairing(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderNamespaceRepairing2(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestDetachWithDOM(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.builder.TestDetachWithDOM(metaFactory, true));
        for (StreamType streamType : Multiton.getInstances(StreamType.class)) {
            addTest(new org.apache.axiom.ts.om.builder.TestDetachWithStream(metaFactory, streamType, false));
            addTest(new org.apache.axiom.ts.om.builder.TestDetachWithStream(metaFactory, streamType, true));
        }
        addTest(new org.apache.axiom.ts.om.builder.TestDetachWithSAXSource(metaFactory));
        for (BuilderFactory bf : getInstances(BuilderFactory.class)) {
            addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElement(metaFactory, bf, null));
            addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElement(metaFactory, bf, Boolean.FALSE));
            addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElement(metaFactory, bf, Boolean.TRUE));
        }
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElementWithDiscardDocumentIllFormedEpilog(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElementWithIllFormedDocument(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestInvalidXML(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestIOExceptionInGetText(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestMalformedDocument(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestReadAttachmentBeforeRootPartComplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestRegisterCustomBuilderForPayloadJAXBPlain(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestRegisterCustomBuilderForPayloadJAXBWithDataHandlerReaderExtension(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestRegisterCustomBuilderForPayloadJAXBWithXOP(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestRootPartStreaming(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestStandaloneConfiguration(metaFactory));
        addTest(new org.apache.axiom.ts.om.comment.TestSerialize(metaFactory));
        for (XMLSample file : getInstances(XMLSample.class)) {
            for (OMContainerExtractor ce : getInstances(OMContainerExtractor.class)) {
                // TODO: investigate why this causes problems
                if (!file.getName().equals("character-references.xml")) {
                    for (BuilderFactory bf : getInstances(BuilderFactory.class)) {
                        addTest(new org.apache.axiom.ts.om.container.TestGetXMLStreamReader(metaFactory, file, bf, ce, true));
                        addTest(new org.apache.axiom.ts.om.container.TestGetXMLStreamReader(metaFactory, file, bf, ce, false));
                    }
                }
                // On a document containing entity references, serialization tests will only work correctly if
                // the entire document is serialized (so that the DTD is available)
                if (!file.hasEntityReferences() || ce == OMContainerExtractor.DOCUMENT) {
                    for (SerializationStrategy ss : getInstances(SerializationStrategy.class)) {
                        if (ss.supportsInternalSubset() || !file.hasInternalSubset()) {
                            addTest(new org.apache.axiom.ts.om.container.TestSerialize(metaFactory, file, ce, ss));
                        }
                    }
                }
            }
        }
        for (OMContainerFactory cf : getInstances(OMContainerFactory.class)) {
            addTest(new org.apache.axiom.ts.om.container.TestAddChildWithIncompleteSibling(metaFactory, cf));
            addTest(new org.apache.axiom.ts.om.container.TestGetBuilderNull(metaFactory, cf));
            addTest(new org.apache.axiom.ts.om.container.TestGetDescendants(metaFactory, cf, true));
            addTest(new org.apache.axiom.ts.om.container.TestGetDescendants(metaFactory, cf, false));
        }
        addTest(new org.apache.axiom.ts.om.doctype.TestSerialize(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestAddChildIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestAddChildWithExistingDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestBuild(metaFactory));
        for (XMLSample file : getInstances(XMLSample.class)) {
            addTest(new org.apache.axiom.ts.om.document.TestClone(metaFactory, file));
        }
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest1.xml", "MD5", "3e5d68c6607bc56c9c171560e4f19db9"));
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest2.xml", "SHA1", "3c47a807517d867d42ffacb2d3e9da81895d5aac"));
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest3.xml", "SHA", "41466144c1cab4234fb127cfb8cf92f9"));
        addTest(new org.apache.axiom.ts.om.document.TestDigest(metaFactory, "digest4.xml", "SHA", "be3b0836cd6f0ceacdf3d40b49a0468d03d2ba2e"));
        addTest(new org.apache.axiom.ts.om.document.TestGetOMDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestGetOMDocumentElementAfterDetach(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestGetOMDocumentElementWithParser(metaFactory));
        for (XSLTImplementation xsltImplementation : getInstances(XSLTImplementation.class)) {
            if (xsltImplementation.supportsLexicalHandlerWithStreamSource()) {
                for (XMLSample file : getInstances(XMLSample.class)) {
                    addTest(new org.apache.axiom.ts.om.document.TestGetSAXResult(metaFactory, xsltImplementation, file));
                }
            }
        }
        for (SAXImplementation saxImplementation : getInstances(SAXImplementation.class)) {
            for (XMLSample file : getInstances(XMLSample.class)) {
                if (!file.hasExternalSubset() || saxImplementation.reportsExternalSubsetEntity()) {
                    addTest(new org.apache.axiom.ts.om.document.TestGetSAXResultSAXParser(metaFactory, saxImplementation, file));
                }
            }
        }
        addTest(new org.apache.axiom.ts.om.document.TestGetSAXResultJAXB(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestGetSAXResultXMLBeans(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestRemoveChildren(metaFactory, true, false));
        addTest(new org.apache.axiom.ts.om.document.TestRemoveChildren(metaFactory, true, true));
        addTest(new org.apache.axiom.ts.om.document.TestRemoveChildren(metaFactory, false, false));
        addTest(new org.apache.axiom.ts.om.document.TestRemoveChildren(metaFactory, false, true));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsume(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeUTF16(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeWithIgnoreXMLDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeXML11(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeXMLDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementNew(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementNull(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementReplace(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSetOMDocumentElementReplaceSame(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.sr.TestCharacterDataReaderFromParser(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.document.sr.TestCharacterDataReaderFromParser(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.document.sr.TestCloseWithoutCaching(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.sr.TestDTDReader(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.sr.TestDTDReaderFromParser(metaFactory, false, true));
        addTest(new org.apache.axiom.ts.om.document.sr.TestDTDReaderFromParser(metaFactory, true, true));
        addTest(new org.apache.axiom.ts.om.document.sr.TestDTDReaderFromParser(metaFactory, false, false));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByOtherElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeGeneratedPrefix(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeGeneratedPrefix(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeReuseExistingPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithInvalidNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithInvalidNamespace2(metaFactory));
        for (AddAttributeStrategy strategy : getInstances(AddAttributeStrategy.class)) {
            addTest(new org.apache.axiom.ts.om.element.TestAddAttributeMultiple(metaFactory, strategy));
            addTest(new org.apache.axiom.ts.om.element.TestAddAttributeReplace(metaFactory, strategy));
            addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationInScope(metaFactory, strategy));
            addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationOnSameElement(metaFactory, strategy));
            addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithMaskedNamespaceDeclaration(metaFactory, strategy));
            addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutExistingNamespaceDeclaration(metaFactory, strategy));
            for (NoNamespaceStrategy noNamespaceStrategy : getInstances(NoNamespaceStrategy.class)) {
                addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutNamespace(metaFactory, strategy, noNamespaceStrategy));
            }
        }
        addTest(new org.apache.axiom.ts.om.element.TestAddChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChild2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildDiscarded(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithSameParent(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithSameParent(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestBuildDiscarded(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestChildReDeclaringGrandParentsDefaultNSWithPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestChildReDeclaringParentsDefaultNSWithPrefix(metaFactory));
        for (XMLSample file : getInstances(XMLSample.class)) {
            if (!file.hasEntityReferences()) {
                addTest(new org.apache.axiom.ts.om.element.TestCloneOMElement2(metaFactory, file));
            }
        }
        addTest(new org.apache.axiom.ts.om.element.TestCloneOMElementNamespaceRepairing(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestClose(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespace2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespaceConflict1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareDefaultNamespaceConflict2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceInvalid1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceInvalid2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceWithEmptyPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix3(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDetachWithDifferentBuilder(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDigestWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDigestWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDiscardDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDiscardIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDiscardPartiallyBuilt(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceByNamespaceURIMasked(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceByPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceCaseSensitivity(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestFindNamespaceURIWithPrefixUndeclaring(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllAttributes1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllAttributes2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespaces(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespacesNoSuchElementException(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespacesRemove(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueNonExisting(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElements(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElementsConcurrentModification(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElementsConsumed(metaFactory));
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
        addTest(new org.apache.axiom.ts.om.element.TestGetDescendantsRemoveSubtree(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithNameOnIncompleteElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstOMChildAfterConsume(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstOMChildAfterDiscard(metaFactory));
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
        addTest(new org.apache.axiom.ts.om.element.TestGetSAXResultWithDTD(metaFactory));
        for (XSLTImplementation xsltImplementation : getInstances(XSLTImplementation.class)) {
            addTest(new org.apache.axiom.ts.om.element.TestGetSAXSourceIdentityTransform(metaFactory, xsltImplementation, true));
            addTest(new org.apache.axiom.ts.om.element.TestGetSAXSourceIdentityTransform(metaFactory, xsltImplementation, false));
            addTest(new org.apache.axiom.ts.om.element.TestGetSAXSourceIdentityTransformOnFragment(metaFactory, xsltImplementation, true));
            addTest(new org.apache.axiom.ts.om.element.TestGetSAXSourceIdentityTransformOnFragment(metaFactory, xsltImplementation, false));
        }
        addTest(new org.apache.axiom.ts.om.element.TestGetText(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQNameEmpty(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQNameNoNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsQNameWithExtraWhitespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsStreamWithNonTextChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsStreamWithoutCaching(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextAsStreamWithSingleTextNode(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextBinary(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextBinary(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextWithCDATASectionChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetTextWithMixedOMTextChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElement(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElement(metaFactory, false));
        for (int build=0; build<5; build++) {
            addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElementPartiallyBuilt(metaFactory, true, build));
            addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElementPartiallyBuilt(metaFactory, false, build));
        }
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithCaching(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithIncompleteDescendant(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithIncompleteDescendant(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithNamespaceURIInterning(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithoutCachingPartiallyBuilt(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithoutCachingPartiallyBuiltModified(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithPreserveNamespaceContext(metaFactory, true, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithPreserveNamespaceContext(metaFactory, true, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithPreserveNamespaceContext(metaFactory, false, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithPreserveNamespaceContext(metaFactory, false, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithPreserveNamespaceContext2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestHasNameWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestHasNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestIsCompleteWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestMultipleDefaultNS(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestRemoveAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestRemoveAttributeNotOwner(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestRemoveChildren(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestRemoveChildren(metaFactory, false));
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
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumeConsumed(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumePartiallyBuilt(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        for (int i=0; i<3; i++) {
            Boolean declare = i == 0 ? null : Boolean.valueOf(i==2);
            boolean implicitDeclare = declare == null || declare.booleanValue();
            // Valid
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", "p", declare, null, false, "p", implicitDeclare));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", null, declare, null, false, null, implicitDeclare));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", "p", declare, "p", false, "p", false));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", "p", declare, "q", false, "p", implicitDeclare));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", null, declare, "p", false, "p", false));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", null, declare, "", false, "", false));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", "", declare, null, false, "", implicitDeclare));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "urn:test", "", declare, "", false, "", false));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "", "", declare, null, false, "", false));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "", null, declare, null, false, "", false));
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, null, null, declare, null, false, "", false));
            // Invalid
            addTest(new org.apache.axiom.ts.om.element.TestSetNamespace(metaFactory, "", "p", declare, null, true, null, false));
        }
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
        addTest(new org.apache.axiom.ts.om.element.TestUnmarshalWithDataHandler(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestUnmarshalWithDataHandlerToByteArray(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestUnmarshalWithDeclaredType(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestWriteTextTo(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestWriteTextToWithNonTextNodes(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.sr.TestCloseAndContinueBuilding(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.sr.TestCloseAndContinueBuilding(metaFactory, false));
        for (BuilderFactory bf : getInstances(BuilderFactory.class)) {
            addTest(new org.apache.axiom.ts.om.element.sr.TestCommentEvent(metaFactory, bf, true));
            addTest(new org.apache.axiom.ts.om.element.sr.TestCommentEvent(metaFactory, bf, false));
        }
        addTest(new org.apache.axiom.ts.om.element.sr.TestGetDataHandlerFromElement(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.sr.TestGetDataHandlerFromElement(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.sr.TestGetElementText(metaFactory));
        for (BuilderFactory bf : getInstances(BuilderFactory.class)) {
            addTest(new org.apache.axiom.ts.om.element.sr.TestGetElementTextFromParser(metaFactory, bf, true, 0));
            for (int build=0; build<6; build++) {
                addTest(new org.apache.axiom.ts.om.element.sr.TestGetElementTextFromParser(metaFactory, bf, false, build));
            }
        }
        addTest(new org.apache.axiom.ts.om.element.sr.TestGetNamespaceContext(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.sr.TestGetNamespaceContext(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.sr.TestNextTag(metaFactory));
        addTest(new org.apache.axiom.ts.om.entref.TestSerialize(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeGeneratedPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeInterfaces(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeNullPrefixNoNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeWithInvalidNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMAttributeWithInvalidNamespace2(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMCommentWithoutParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMDocTypeWithoutParent(metaFactory));
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
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNamespaceInScope1(metaFactory, variant));
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNamespaceInScope2(metaFactory, variant));
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespace2(metaFactory, variant));
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespace3(metaFactory, variant));
            }
            if (variant.isSupportsContainer() && variant.isSupportsDefaultNamespace()) {
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNamespaceInScope3(metaFactory, variant));
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNamespaceInScope4(metaFactory, variant));
            }
        }
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullOMDataSource1(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullOMDataSource2(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullURIAndPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMEntityReference(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMEntityReferenceWithNullParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMNamespaceWithNullURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMProcessingInstructionWithoutParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMText(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextFromDataHandlerProvider(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextFromDataHandlerProvider(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextFromOMText(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextWithNullParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestFactoryIsSingleton(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestGetMetaFactory(metaFactory));
        addTest(new org.apache.axiom.ts.om.misc.TestAxiom95(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestEquals(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestEqualsWithNullPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestGetNamespaceURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestGetPrefix(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestHashCode(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestObjectEquals(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestObjectEqualsWithDifferentPrefixes(metaFactory));
        addTest(new org.apache.axiom.ts.om.namespace.TestObjectEqualsWithDifferentURIs(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, true, true));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, true, false));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, false, true));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, false, false));
        addTest(new org.apache.axiom.ts.om.node.TestDetachAfterBuilderClose(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestDetachFirstChild(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.node.TestDetachFirstChild(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.node.TestGetNextOMSiblingAfterDiscard(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfter(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterLastChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnSelf(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterSameParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBefore(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnSelf(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeSameParent(metaFactory));
        for (int i=0; i<OMSourcedElementVariant.INSTANCES.length; i++) {
            OMSourcedElementVariant variant = OMSourcedElementVariant.INSTANCES[i];
            for (int j=0; j<qnames.length; j++) {
                QName qname = qnames[j];
                addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetLocalName(metaFactory, variant, qname));
                addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNamespace(metaFactory, variant, qname));
                addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetPrefix(metaFactory, variant, qname));
                addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceURI(metaFactory, variant, qname));
                addTest(new org.apache.axiom.ts.om.sourcedelement.TestHasName(metaFactory, variant, qname));
            }
        }
        for (AddAttributeStrategy strategy : getInstances(AddAttributeStrategy.class)) {
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestAddAttribute(metaFactory, strategy));
        }
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestBlobOMDataSource(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestStringOMDataSource(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestCloneNonDestructive(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestCloneNonDestructive(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestCloneUnknownName(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestCloseOnComplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestComplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestDeclareNamespace(metaFactory));
        for (ExpansionStrategy es : getInstances(ExpansionStrategy.class)) {
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestDetach(metaFactory, es));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestDiscard(metaFactory, es));
        }
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestExpand(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetAllAttributes(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetAllDeclaredNamespaces(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetAttributeValue(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetDocumentFromBuilder(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceNormalized(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceNormalized2(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetNextOMSiblingIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetObject(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetReaderException(metaFactory));
        for (int i=0; i<PushOMDataSourceScenario.INSTANCES.length; i++) {
            PushOMDataSourceScenario scenario = PushOMDataSourceScenario.INSTANCES[i];
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetSAXSourceWithPushOMDataSource(metaFactory, scenario, false));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetSAXSourceWithPushOMDataSource(metaFactory, scenario, true));
        }
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestGetSAXSourceWithPushOMDataSourceThrowingException(metaFactory));
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
        for (int i=0; i<PushOMDataSourceScenario.INSTANCES.length; i++) {
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestPushOMDataSourceExpansion(metaFactory, PushOMDataSourceScenario.INSTANCES[i]));
        }
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestRemoveChildrenUnexpanded(metaFactory));
        for (ElementContext ec : getInstances(ElementContext.class)) {
            for (ExpansionStrategy es : getInstances(ExpansionStrategy.class)) {
                for (SerializationStrategy ss : getInstances(SerializationStrategy.class)) {
                    for (int count = 1; count <= 2; count++) {
                        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, false, false, ec, es, ss, false, count));
                        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, false, true, ec, es, ss, false, count));
                        if (ec != ElementContext.ORPHAN) {
                            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, false, false, ec, es, ss, true, count));
                            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, false, true, ec, es, ss, true, count));
                        }
                        if (es != ExpansionStrategy.PARTIAL) {
                            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, true, false, ec, es, ss, false, count));
                            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, true, true, ec, es, ss, false, count));
                            if (ec != ElementContext.ORPHAN) {
                                addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, true, false, ec, es, ss, true, count));
                                addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerialize(metaFactory, true, true, ec, es, ss, true, count));
                            }
                        }
                    }
                }
            }
        }
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeModifiedOMSEWithNonDestructiveDataSource(metaFactory));
        for (SerializationStrategy ss : getInstances(SerializationStrategy.class)) {
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeOMDataSourceWritingToOutputStream(metaFactory, ss, false));
            addTest(new org.apache.axiom.ts.om.sourcedelement.TestSerializeOMDataSourceWritingToOutputStream(metaFactory, ss, true));
        }
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetDataSource(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetDataSourceOnAlreadyExpandedElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetLocalName(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestSetLocalName(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestWrappedTextNodeOMDataSourceFromReader(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.TestWriteTextToWithNonDestructiveOMDataSource(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.jaxb.TestDataHandlerExpansion(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.jaxb.TestDataHandlerSerializationWithMTOM(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.jaxb.TestDataHandlerSerializationWithoutMTOM(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.jaxb.TestExceptionDuringSerialization(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.jaxb.TestGetNameFromJAXBElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.jaxb.TestGetNameFromPlainObject(metaFactory));
        addTest(new org.apache.axiom.ts.om.sourcedelement.sr.TestGetName(metaFactory));
        for (int events = 0; events < 7; events++) {
            addTest(new org.apache.axiom.ts.om.sourcedelement.sr.TestCloseWithoutCaching(metaFactory, events));
        }
        addTest(new org.apache.axiom.ts.om.pi.TestDigest(metaFactory));
        addTest(new org.apache.axiom.ts.om.pi.TestSerialize(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestBase64StreamingWithGetSAXSource(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestBase64StreamingWithSerialize(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestCloneBinary(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.text.TestCloneBinary(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.text.TestDigest(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestGetNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestGetNamespaceNoNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestGetTextCharactersFromDataHandler(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestSerialize(metaFactory, OMNode.TEXT_NODE));
        addTest(new org.apache.axiom.ts.om.text.TestSerialize(metaFactory, OMNode.SPACE_NODE));
        addTest(new org.apache.axiom.ts.om.text.TestSerialize(metaFactory, OMNode.CDATA_SECTION_NODE));
        addTest(new org.apache.axiom.ts.om.xop.TestSerialize(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.xop.TestSerialize(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.xop.TestSetOptimize(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.xop.TestSetOptimize(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.xop.XOPRoundtripTest(metaFactory));
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
