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
package org.apache.axiom.ts;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.om.container.OMContainerFactory;
import org.apache.axiom.ts.om.container.OMElementFactory;
import org.apache.axiom.ts.om.container.SerializationMethod;
import org.apache.axiom.ts.om.container.SerializeToOutputStream;
import org.apache.axiom.ts.om.factory.OMElementCreator;

public class OMTestSuiteBuilder extends AxiomTestSuiteBuilder {
    private static final OMContainerFactory[] containerFactories = {
        OMContainerFactory.DOCUMENT,
        new OMElementFactory(false),
        new OMElementFactory(true) };
    
    private static final SerializationMethod[] serializationMethods = {
        new SerializeToOutputStream(true),
        new SerializeToOutputStream(false) };
    
    public OMTestSuiteBuilder(OMMetaFactory metaFactory) {
        super(metaFactory);
    }
    
    protected void addTests() {
        String[] conformanceFiles = AbstractTestCase.getConformanceTestFiles();
        addTest(new org.apache.axiom.ts.om.attribute.TestEqualsHashCode(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQNameWithNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQNameWithoutNamespace(metaFactory));
        for (int i=0; i<conformanceFiles.length; i++) {
            addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMSource(metaFactory, conformanceFiles[i]));
            addTest(new org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromSAXSource(metaFactory, conformanceFiles[i]));
        }
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElementWithDiscardDocument(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestInvalidXML(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestIOExceptionInGetText(metaFactory));
        for (int i=0; i<conformanceFiles.length; i++) {
            for (int j=0; j<containerFactories.length; j++) {
                addTest(new org.apache.axiom.ts.om.container.TestGetXMLStreamReader(metaFactory, conformanceFiles[i], containerFactories[j], true));
                addTest(new org.apache.axiom.ts.om.container.TestGetXMLStreamReader(metaFactory, conformanceFiles[i], containerFactories[j], false));
                for (int k=0; k<serializationMethods.length; k++) {
                    addTest(new org.apache.axiom.ts.om.container.TestSerialize(metaFactory, conformanceFiles[i], containerFactories[j], serializationMethods[k]));
                }
            }
        }
        addTest(new org.apache.axiom.ts.om.document.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsume(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByOtherElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeFromOMAttributeMultiple(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeFromOMAttributeWithExistingName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeReplace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeReplace2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationInScope(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationOnSameElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithMaskedNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutExistingNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddChildWithParent(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestDeclareNamespace1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllAttributes1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllAttributes2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespaces(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueNonExisting(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElements(metaFactory));
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
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithNameOnIncompleteElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetNamespaceURIWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetQName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetQNameWithoutNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromParser(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCommentEvent(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderGetElementText(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderGetNamespaceContext(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderGetNamespaceContext(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderNextTag(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElement(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderOnNonRootElement(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithOMSourcedElementDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
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
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetText(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQName(metaFactory));
        for (int i=0; i<OMElementCreator.INSTANCES.length; i++) {
            OMElementCreator creator = OMElementCreator.INSTANCES[i];
            if (creator.isSupportsDefaultNamespace()) {
                addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithDefaultNamespace(metaFactory, creator));
            }
            addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithGeneratedPrefix(metaFactory, creator));
            addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithNonDefaultNamespace(metaFactory, creator));
            addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElementWithoutNamespace(metaFactory, creator));
        }
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMNamespace(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMNamespaceWithNullURI(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMText(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMTextFromDataHandlerProvider(metaFactory));
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
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfter(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterLastChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnSelf(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBefore(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnSelf(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestBase64Streaming(metaFactory));
    }
}
