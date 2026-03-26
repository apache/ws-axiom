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
import java.util.Arrays;
import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.ConditionalNode;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.ParameterBinding;
import org.apache.axiom.testutils.suite.ParentNode;
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
import org.apache.axiom.ts.xml.StreamType;
import org.apache.axiom.ts.xml.XMLSample;
import org.apache.axiom.ts.xml.XOPSample;
import org.apache.axiom.util.xml.stream.XMLEventUtils;

public class OMTestSuite {
    private static final ImmutableList<QName> QNAMES =
            ImmutableList.of(
                    new QName("root"),
                    new QName("urn:test", "root", "p"),
                    new QName("urn:test", "root"));

    private static final ParameterBinding<QName> QNAME_PARAMS =
            (injector, qname, params) -> {
                params.addTestParameter("prefix", qname.getPrefix());
                params.addTestParameter("uri", qname.getNamespaceURI());
            };

    public static InjectorNode create(OMMetaFactory metaFactory) {
        return new InjectorNode(
                binder -> binder.bind(OMMetaFactory.class).toInstance(metaFactory),
                new ParentNode(
                        // ── attribute package ──
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestDigestWithNamespace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestDigestWithoutNamespace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetAttributeTypeDefault.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetNamespaceNormalized.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetNamespaceURIWithNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetNamespaceURIWithoutNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetPrefixWithNamespace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetPrefixWithoutNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetQNameWithNamespace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestGetQNameWithoutNamespace
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestHasNameWithNamespace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.attribute.TestHasNameWithoutNamespace.class),
                        new MatrixTest(org.apache.axiom.ts.om.attribute.TestSetLocalName.class),
                        new FanOutNode<>(
                                org.apache.axiom.ts.om.attribute.TestSetNamespace.PARAMS,
                                Binding.singleton(
                                        Key.get(
                                                org.apache.axiom.ts.om.attribute.TestSetNamespace
                                                        .Params.class)),
                                (inj, v, p) -> {
                                    if (v.namespaceURI() != null)
                                        p.addTestParameter("uri", v.namespaceURI());
                                    if (v.prefix() != null)
                                        p.addTestParameter("prefix", v.prefix());
                                    if (v.prefixInScope() != null)
                                        p.addTestParameter("prefixInScope", v.prefixInScope());
                                    p.addTestParameter("invalid", v.invalid());
                                    p.addTestParameter("declare", v.declare());
                                    p.addTestParameter("owner", v.owner());
                                },
                                new MatrixTest(
                                        org.apache.axiom.ts.om.attribute.TestSetNamespace.class)),
                        // ── builder package ──
                        new FanOutNode<>(
                                getInstances(StreamType.class),
                                Binding.singleton(Key.get(StreamType.class)),
                                (inj, v, p) ->
                                        p.addTestParameter("type", v.getType().getSimpleName()),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.builder.TestCloseWithStream.class)),
                        new MatrixTest(org.apache.axiom.ts.om.builder.TestCloseWithSystemId.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestCloseWithXMLStreamReader.class),
                        new FanOutNode<>(
                                getInstances(XMLSample.class),
                                Binding.singleton(Key.get(XMLSample.class)),
                                (inj, v, p) -> p.addTestParameter("file", v.getName()),
                                new ParentNode(
                                        new FanOutNode<>(
                                                getInstances(DOMImplementation.class),
                                                Binding.singleton(Key.get(DOMImplementation.class)),
                                                (inj, v, p) ->
                                                        p.addTestParameter(
                                                                "implementation", v.getName()),
                                                new FanOutNode<>(
                                                        injector ->
                                                                Arrays.asList(
                                                                        Boolean.TRUE,
                                                                        Boolean.FALSE,
                                                                        null),
                                                        (binder, value) ->
                                                                binder.bind(Boolean.class)
                                                                        .annotatedWith(
                                                                                Names.named(
                                                                                        "expandEntityReferences"))
                                                                        .toProvider(
                                                                                Providers.of(
                                                                                        value)),
                                                        (inj, v, p) ->
                                                                p.addTestParameter(
                                                                        "expandEntityReferences",
                                                                        String.valueOf(v)),
                                                        new MatrixTest(
                                                                org.apache.axiom.ts.om.builder
                                                                        .TestCreateOMBuilderFromDOM
                                                                        .class))),
                                        new FanOutNode<>(
                                                getInstances(SAXImplementation.class),
                                                Binding.singleton(Key.get(SAXImplementation.class)),
                                                (inj, v, p) ->
                                                        p.addTestParameter(
                                                                "implementation", v.getName()),
                                                new ConditionalNode(
                                                        injector -> {
                                                            XMLSample file =
                                                                    injector.getInstance(
                                                                            XMLSample.class);
                                                            SAXImplementation impl =
                                                                    injector.getInstance(
                                                                            SAXImplementation
                                                                                    .class);
                                                            return !file.hasExternalSubset()
                                                                    || impl
                                                                            .reportsExternalSubsetEntity();
                                                        },
                                                        new FanOutNode<>(
                                                                injector ->
                                                                        Arrays.asList(
                                                                                Boolean.TRUE,
                                                                                Boolean.FALSE,
                                                                                null),
                                                                (binder, value) ->
                                                                        binder.bind(Boolean.class)
                                                                                .annotatedWith(
                                                                                        Names.named(
                                                                                                "expandEntityReferences"))
                                                                                .toProvider(
                                                                                        Providers
                                                                                                .of(
                                                                                                        value)),
                                                                (inj, v, p) ->
                                                                        p.addTestParameter(
                                                                                "expandEntityReferences",
                                                                                String.valueOf(v)),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.om
                                                                                .builder
                                                                                .TestCreateOMBuilderFromSAXSource
                                                                                .class)))))),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestCreateOMBuilderFromDOMElement
                                        .class),
                        new FanOutNode<>(
                                ImmutableList.of("", "p"),
                                Binding.singleton(Key.get(String.class, Names.named("prefix"))),
                                (inj, v, p) -> p.addTestParameter("prefix", v),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.builder
                                                .TestCreateOMBuilderFromDOMWithNSUnawareNamespaceDeclaration
                                                .class)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestCreateOMBuilderFromDOMWithNSUnawarePrefixedAttribute
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestCreateOMBuilderFromDOMWithNSUnawareUnprefixedAttribute
                                        .class),
                        new FanOutNode<>(
                                getInstances(XOPSample.class),
                                Binding.singleton(Key.get(XOPSample.class)),
                                (inj, v, p) -> p.addTestParameter("file", v.getName()),
                                new FanOutNode<>(
                                        ImmutableList.of(false, true),
                                        Binding.singleton(
                                                Key.get(Boolean.class, Names.named("build"))),
                                        (inj, v, p) ->
                                                p.addTestParameter("build", String.valueOf(v)),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.builder
                                                        .TestCreateOMBuilderXOP.class))),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderFromFragment
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestCreateStAXOMBuilderFromXmlBeansPullParser.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestCreateStAXOMBuilderIncorrectState
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestCreateStAXOMBuilderNamespaceRepairing.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestCreateStAXOMBuilderNamespaceRepairing2.class),
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(
                                        Key.get(Boolean.class, Names.named("useDOMSource"))),
                                (inj, v, p) ->
                                        p.addTestParameter("useDOMSource", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.builder.TestDetachWithDOM.class)),
                        new FanOutNode<>(
                                getInstances(StreamType.class),
                                Binding.singleton(Key.get(StreamType.class)),
                                (inj, v, p) ->
                                        p.addTestParameter(
                                                "streamType", v.getType().getSimpleName()),
                                new FanOutNode<>(
                                        ImmutableList.of(false, true),
                                        Binding.singleton(
                                                Key.get(
                                                        Boolean.class,
                                                        Names.named("useStreamSource"))),
                                        (inj, v, p) ->
                                                p.addTestParameter(
                                                        "useStreamSource", String.valueOf(v)),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.builder.TestDetachWithStream
                                                        .class))),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestDetachWithSAXSource.class),
                        new FanOutNode<>(
                                getInstances(BuilderFactory.class),
                                Binding.singleton(Key.get(BuilderFactory.class)),
                                ParameterBinding.DIMENSION,
                                new FanOutNode<>(
                                        injector ->
                                                Arrays.asList(null, Boolean.FALSE, Boolean.TRUE),
                                        (binder, value) ->
                                                binder.bind(Boolean.class)
                                                        .annotatedWith(
                                                                Names.named("discardDocument"))
                                                        .toProvider(Providers.of(value)),
                                        (inj, v, p) ->
                                                p.addTestParameter(
                                                        "discardDocument", String.valueOf(v)),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.builder
                                                        .TestGetDocumentElement.class))),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestGetDocumentElementWithDiscardDocumentIllFormedEpilog
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestGetDocumentElementWithIllFormedDocument.class),
                        new MatrixTest(org.apache.axiom.ts.om.builder.TestInvalidXML.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestIOExceptionInGetText.class),
                        new MatrixTest(org.apache.axiom.ts.om.builder.TestMalformedDocument.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder
                                        .TestReadAttachmentBeforeRootPartComplete.class),
                        new MatrixTest(org.apache.axiom.ts.om.builder.TestRootPartStreaming.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.builder.TestStandaloneConfiguration.class),
                        // ── comment package ──
                        new MatrixTest(org.apache.axiom.ts.om.comment.TestSerialize.class),
                        // ── container package ──
                        containerTests(),
                        // ── doctype package ──
                        new MatrixTest(org.apache.axiom.ts.om.doctype.TestSerialize.class),
                        // ── document package ──
                        documentTests(),
                        // ── element package ──
                        elementTests(),
                        // ── entref package ──
                        new MatrixTest(org.apache.axiom.ts.om.entref.TestSerialize.class),
                        // ── factory package ──
                        factoryTests(),
                        // ── misc package ──
                        new MatrixTest(org.apache.axiom.ts.om.misc.TestAxiom95.class),
                        // ── namespace package ──
                        new MatrixTest(org.apache.axiom.ts.om.namespace.TestEquals.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.namespace.TestEqualsWithNullPrefix.class),
                        new MatrixTest(org.apache.axiom.ts.om.namespace.TestGetNamespaceURI.class),
                        new MatrixTest(org.apache.axiom.ts.om.namespace.TestGetPrefix.class),
                        new MatrixTest(org.apache.axiom.ts.om.namespace.TestHashCode.class),
                        new MatrixTest(org.apache.axiom.ts.om.namespace.TestObjectEquals.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.namespace
                                        .TestObjectEqualsWithDifferentPrefixes.class),
                        new MatrixTest(
                                org.apache.axiom.ts.om.namespace.TestObjectEqualsWithDifferentURIs
                                        .class),
                        // ── node package ──
                        nodeTests(),
                        // ── sourcedelement package ──
                        sourcedElementTests(),
                        // ── pi package ──
                        new MatrixTest(org.apache.axiom.ts.om.pi.TestDigest.class),
                        new MatrixTest(org.apache.axiom.ts.om.pi.TestSerialize.class),
                        // ── text package ──
                        textTests(),
                        // ── xop package ──
                        xopTests(),
                        // ── xpath package ──
                        xpathTests()));
    }

    private static MatrixTestNode containerTests() {
        return new ParentNode(
                new FanOutNode<>(
                        getInstances(XMLSample.class),
                        Binding.singleton(Key.get(XMLSample.class)),
                        (inj, v, p) -> p.addTestParameter("file", v.getName()),
                        new FanOutNode<>(
                                getInstances(OMContainerExtractor.class),
                                Binding.singleton(Key.get(OMContainerExtractor.class)),
                                ParameterBinding.DIMENSION,
                                new ParentNode(
                                        new ConditionalNode(
                                                injector -> {
                                                    XMLSample file =
                                                            injector.getInstance(XMLSample.class);
                                                    return !file.getName()
                                                                    .equals(
                                                                            "character-references.xml")
                                                            && !file.getName().equals("large.xml");
                                                },
                                                new FanOutNode<>(
                                                        getInstances(BuilderFactory.class),
                                                        Binding.singleton(
                                                                Key.get(BuilderFactory.class)),
                                                        ParameterBinding.DIMENSION,
                                                        new FanOutNode<>(
                                                                ImmutableList.of(false, true),
                                                                Binding.singleton(
                                                                        Key.get(
                                                                                Boolean.class,
                                                                                Names.named(
                                                                                        "cache"))),
                                                                (inj, v, p) ->
                                                                        p.addTestParameter(
                                                                                "cache",
                                                                                String.valueOf(v)),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.om
                                                                                .container
                                                                                .TestGetXMLStreamReader
                                                                                .class)))),
                                        new ConditionalNode(
                                                injector -> {
                                                    XMLSample file =
                                                            injector.getInstance(XMLSample.class);
                                                    OMContainerExtractor ce =
                                                            injector.getInstance(
                                                                    OMContainerExtractor.class);
                                                    return !file.hasEntityReferences()
                                                            || ce == OMContainerExtractor.DOCUMENT;
                                                },
                                                new FanOutNode<>(
                                                        injector -> {
                                                            XMLSample file =
                                                                    injector.getInstance(
                                                                            XMLSample.class);
                                                            return getInstances(
                                                                            SerializationStrategy
                                                                                    .class)
                                                                    .stream()
                                                                    .filter(
                                                                            ss ->
                                                                                    ss
                                                                                                    .supportsInternalSubset()
                                                                                            || !file
                                                                                                    .hasInternalSubset())
                                                                    .collect(
                                                                            ImmutableList
                                                                                    .toImmutableList());
                                                        },
                                                        Binding.singleton(
                                                                Key.get(
                                                                        SerializationStrategy
                                                                                .class)),
                                                        ParameterBinding.DIMENSION,
                                                        new MatrixTest(
                                                                org.apache.axiom.ts.om.container
                                                                        .TestSerialize.class)))))),
                new FanOutNode<>(
                        getInstances(OMContainerFactory.class),
                        Binding.singleton(Key.get(OMContainerFactory.class)),
                        ParameterBinding.DIMENSION,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.om.container
                                                .TestAddChildWithIncompleteSibling.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.container.TestGetBuilderNull.class),
                                new FanOutNode<>(
                                        ImmutableList.of(false, true),
                                        Binding.singleton(
                                                Key.get(Boolean.class, Names.named("includeSelf"))),
                                        (inj, v, p) ->
                                                p.addTestParameter(
                                                        "includeSelf", String.valueOf(v)),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.container.TestGetDescendants
                                                        .class)))));
    }

    private static MatrixTestNode documentTests() {
        return new ParentNode(
                new MatrixTest(org.apache.axiom.ts.om.document.TestAddChildIncomplete.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestAddChildWithExistingDocumentElement
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.document.TestBuild.class),
                new FanOutNode<>(
                        getInstances(XMLSample.class),
                        Binding.singleton(Key.get(XMLSample.class)),
                        (inj, v, p) -> p.addTestParameter("file", v.getName()),
                        new MatrixTest(org.apache.axiom.ts.om.document.TestClone.class)),
                new FanOutNode<>(
                        org.apache.axiom.ts.om.document.TestDigest.PARAMS,
                        Binding.singleton(
                                Key.get(org.apache.axiom.ts.om.document.TestDigest.Params.class)),
                        (inj, v, p) -> p.addTestParameter("file", v.file()),
                        new MatrixTest(org.apache.axiom.ts.om.document.TestDigest.class)),
                new MatrixTest(org.apache.axiom.ts.om.document.TestGetOMDocumentElement.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestGetOMDocumentElementAfterDetach.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestGetOMDocumentElementWithParser.class),
                new FanOutNode<>(
                        getInstances(XSLTImplementation.class),
                        Binding.singleton(Key.get(XSLTImplementation.class)),
                        (inj, v, p) -> p.addTestParameter("xslt", v.getName()),
                        new ConditionalNode(
                                injector ->
                                        injector.getInstance(XSLTImplementation.class)
                                                .supportsLexicalHandlerWithStreamSource(),
                                new FanOutNode<>(
                                        getInstances(XMLSample.class),
                                        Binding.singleton(Key.get(XMLSample.class)),
                                        (inj, v, p) -> p.addTestParameter("file", v.getName()),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.document.TestGetSAXResult
                                                        .class)))),
                new FanOutNode<>(
                        getInstances(SAXImplementation.class),
                        Binding.singleton(Key.get(SAXImplementation.class)),
                        (inj, v, p) -> p.addTestParameter("parser", v.getName()),
                        new FanOutNode<>(
                                getInstances(XMLSample.class),
                                Binding.singleton(Key.get(XMLSample.class)),
                                (inj, v, p) -> p.addTestParameter("file", v.getName()),
                                new ConditionalNode(
                                        injector -> {
                                            XMLSample file = injector.getInstance(XMLSample.class);
                                            SAXImplementation impl =
                                                    injector.getInstance(SAXImplementation.class);
                                            return !file.hasExternalSubset()
                                                    || impl.reportsExternalSubsetEntity();
                                        },
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.document
                                                        .TestGetSAXResultSAXParser.class)))),
                new MatrixTest(org.apache.axiom.ts.om.document.TestGetSAXResultXMLBeans.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestIsCompleteAfterAddingIncompleteChild
                                .class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("complete"))),
                        (inj, v, p) -> p.addTestParameter("complete", String.valueOf(v)),
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(
                                        Key.get(
                                                Boolean.class,
                                                Names.named("accessDocumentElement"))),
                                (inj, v, p) ->
                                        p.addTestParameter(
                                                "accessDocumentElement", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.document.TestRemoveChildren.class))),
                new MatrixTest(org.apache.axiom.ts.om.document.TestSerializeAndConsume.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document
                                .TestSerializeAndConsumeWithIncompleteDescendant.class),
                new MatrixTest(org.apache.axiom.ts.om.document.TestSerializeUTF16.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestSerializeWithIgnoreXMLDeclaration
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.document.TestSerializeXML11.class),
                new MatrixTest(org.apache.axiom.ts.om.document.TestSerializeXMLDeclaration.class),
                new MatrixTest(org.apache.axiom.ts.om.document.TestSetOMDocumentElementNew.class),
                new MatrixTest(org.apache.axiom.ts.om.document.TestSetOMDocumentElementNull.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestSetOMDocumentElementReplace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.document.TestSetOMDocumentElementReplaceSame.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.document.sr.TestCharacterDataReaderFromParser
                                        .class)),
                new MatrixTest(org.apache.axiom.ts.om.document.sr.TestCloseWithoutCaching.class),
                new MatrixTest(org.apache.axiom.ts.om.document.sr.TestDTDReader.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("build"))),
                        (inj, v, p) -> p.addTestParameter("build", String.valueOf(v)),
                        new FanOutNode<>(
                                injector ->
                                        injector.getInstance(
                                                        Key.get(
                                                                Boolean.class,
                                                                Names.named("build")))
                                                ? ImmutableList.of(true)
                                                : ImmutableList.of(true, false),
                                Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                                (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.document.sr.TestDTDReaderFromParser
                                                .class))));
    }

    private static MatrixTestNode elementTests() {
        return new ParentNode(
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByElement.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestAddAttributeAlreadyOwnedByOtherElement
                                .class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(
                                Key.get(Boolean.class, Names.named("defaultNamespaceInScope"))),
                        (inj, v, p) ->
                                p.addTestParameter("defaultNamespaceInScope", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.TestAddAttributeGeneratedPrefix
                                        .class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestAddAttributeReuseExistingPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestAddAttributeWithInvalidNamespace1.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestAddAttributeWithInvalidNamespace2.class),
                new FanOutNode<>(
                        getInstances(AddAttributeStrategy.class),
                        Binding.singleton(Key.get(AddAttributeStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element.TestAddAttributeMultiple
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element.TestAddAttributeReplace
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element
                                                .TestAddAttributeWithExistingNamespaceDeclarationInScope
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element
                                                .TestAddAttributeWithExistingNamespaceDeclarationOnSameElement
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element
                                                .TestAddAttributeWithMaskedNamespaceDeclaration
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element
                                                .TestAddAttributeWithoutExistingNamespaceDeclaration
                                                .class),
                                new FanOutNode<>(
                                        getInstances(NoNamespaceStrategy.class),
                                        Binding.singleton(Key.get(NoNamespaceStrategy.class)),
                                        ParameterBinding.DIMENSION,
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.element
                                                        .TestAddAttributeWithoutNamespace.class)))),
                new MatrixTest(org.apache.axiom.ts.om.element.TestAddChild.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestAddChild2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestAddChildDiscarded.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestAddChildIncomplete.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestAddChildWithParent.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("build"))),
                        (inj, v, p) -> p.addTestParameter("build", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.TestAddChildWithSameParent.class)),
                new MatrixTest(org.apache.axiom.ts.om.element.TestBuildDiscarded.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestChildReDeclaringGrandParentsDefaultNSWithPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestChildReDeclaringParentsDefaultNSWithPrefix.class),
                new FanOutNode<>(
                        injector ->
                                getInstances(XMLSample.class).stream()
                                        .filter(f -> !f.hasEntityReferences())
                                        .collect(ImmutableList.toImmutableList()),
                        Binding.singleton(Key.get(XMLSample.class)),
                        (inj, v, p) -> p.addTestParameter("file", v.getName()),
                        new MatrixTest(org.apache.axiom.ts.om.element.TestCloneOMElement2.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestCloneOMElementNamespaceRepairing.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestClose.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDeclareDefaultNamespace1.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDeclareDefaultNamespace2.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestDeclareDefaultNamespaceConflict1.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestDeclareDefaultNamespaceConflict2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDeclareNamespace1.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDeclareNamespaceInvalid1.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDeclareNamespaceInvalid2.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestDeclareNamespaceWithEmptyPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix1
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestDeclareNamespaceWithGeneratedPrefix3
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDetachWithDifferentBuilder.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDigestWithNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDigestWithoutNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDiscardDocumentElement.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDiscardIncomplete.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestDiscardPartiallyBuilt.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestFindNamespaceByNamespaceURIMasked.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestFindNamespaceByPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestFindNamespaceCaseSensitivity.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestFindNamespaceURIWithPrefixUndeclaring
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetAllAttributes1.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetAllAttributes2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespaces.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestGetAllDeclaredNamespacesNoSuchElementException.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetAllDeclaredNamespacesRemove.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetAttributeValueNonExisting.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix1.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix1.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildElements.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetChildElementsConcurrentModification
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildElementsConsumed.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildren.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetChildrenConcurrentModification.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenRemove1.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenRemove2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenRemove3.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenRemove4.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenWithLocalName.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenWithName.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenWithName2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenWithName3.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetChildrenWithName4.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetChildrenWithNameNextWithoutHasNext
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetChildrenWithNamespaceURI.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetDefaultNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetDefaultNamespace2.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("includeSelf"))),
                        (inj, v, p) -> p.addTestParameter("includeSelf", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.element.TestGetDescendants.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetDescendantsRemoveSubtree.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetFirstChildWithName.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetFirstChildWithNameOnIncompleteElement
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetFirstOMChildAfterConsume.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetFirstOMChildAfterDiscard.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("detached"))),
                        (inj, v, p) -> p.addTestParameter("detached", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.TestGetNamespaceContext.class)),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("useNull"))),
                        (inj, v, p) -> p.addTestParameter("useNull", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.TestGetNamespaceNormalized.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetNamespaceNormalizedWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetNamespaceNormalizedWithSAXSource
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetNamespacesInScope.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetNamespacesInScopeWithDefaultNamespace
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestGetNamespacesInScopeWithMaskedDefaultNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetNamespacesInScopeWithMaskedNamespace
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetNamespaceURIWithNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetNamespaceURIWithoutNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetPrefixWithDefaultNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetPrefixWithNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetPrefixWithoutNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetQNameWithNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetQNameWithoutNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetSAXResultWithDTD.class),
                new FanOutNode<>(
                        getInstances(XSLTImplementation.class),
                        Binding.singleton(Key.get(XSLTImplementation.class)),
                        (inj, v, p) -> p.addTestParameter("xslt", v.getName()),
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                                (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.element
                                                        .TestGetSAXSourceIdentityTransform.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.element
                                                        .TestGetSAXSourceIdentityTransformOnFragment
                                                        .class)))),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetText.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetTextAsQName.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetTextAsQNameEmpty.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestGetTextAsQNameNoNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetTextAsQNameWithExtraWhitespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetTextAsStreamWithNonTextChildren
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetTextAsStreamWithoutCaching.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetTextAsStreamWithSingleTextNode.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("compact"))),
                        (inj, v, p) -> p.addTestParameter("compact", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.element.TestGetTextBinary.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetTextWithCDATASectionChild.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetTextWithMixedOMTextChildren.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromElement
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetXMLStreamReaderCDATAEventFromParser
                                .class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element
                                        .TestGetXMLStreamReaderOnNonRootElement.class)),
                new FanOutNode<>(
                        ImmutableList.of(0, 1, 2, 3, 4),
                        Binding.singleton(Key.get(Integer.class, Names.named("build"))),
                        (inj, v, p) -> p.addTestParameter("build", v),
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                                (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element
                                                .TestGetXMLStreamReaderOnNonRootElementPartiallyBuilt
                                                .class))),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestGetXMLStreamReaderWithCaching.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element
                                        .TestGetXMLStreamReaderWithIncompleteDescendant.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestGetXMLStreamReaderWithNamespaceURIInterning.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestGetXMLStreamReaderWithoutCachingPartiallyBuilt.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestGetXMLStreamReaderWithoutCachingPartiallyBuiltModified.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(
                                Key.get(Boolean.class, Names.named("preserveNamespaceContext"))),
                        (inj, v, p) ->
                                p.addTestParameter("preserveNamespaceContext", String.valueOf(v)),
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                                (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element
                                                .TestGetXMLStreamReaderWithPreserveNamespaceContext
                                                .class))),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestGetXMLStreamReaderWithPreserveNamespaceContext2.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestHasNameWithNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestHasNameWithoutNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestIsCompleteAfterAddingIncompleteChild
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestIsCompleteWithParser.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestMultipleDefaultNS.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestRemoveAttribute.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestRemoveAttributeNotOwner.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("complete"))),
                        (inj, v, p) -> p.addTestParameter("complete", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.element.TestRemoveChildren.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestResolveQNameWithDefaultNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestResolveQNameWithNonDefaultNamespace
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestResolveQNameWithoutNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestResolveQNameWithUnboundPrefix.class),
                new FanOutNode<>(
                        org.apache.axiom.ts.om.element.TestSerialization.PARAMS,
                        Binding.singleton(
                                Key.get(
                                        org.apache.axiom.ts.om.element.TestSerialization.Params
                                                .class)),
                        (inj, v, p) -> {
                            p.addTestParameter("parent", v.parent());
                            p.addTestParameter("children", v.children());
                        },
                        new MatrixTest(org.apache.axiom.ts.om.element.TestSerialization.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSerializationWithTwoNonBuiltOMElements
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSerializeAndConsumeConsumed.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSerializeAndConsumePartiallyBuilt.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element
                                .TestSerializeAndConsumeWithIncompleteDescendant.class),
                new FanOutNode<>(
                        org.apache.axiom.ts.om.element.TestSetNamespace.PARAMS,
                        Binding.singleton(
                                Key.get(
                                        org.apache.axiom.ts.om.element.TestSetNamespace.Params
                                                .class)),
                        (inj, v, p) -> {
                            if (v.namespaceURI() != null)
                                p.addTestParameter("uri", v.namespaceURI());
                            if (v.prefix() != null) p.addTestParameter("prefix", v.prefix());
                            if (v.prefixInScope() != null)
                                p.addTestParameter("prefixInScope", v.prefixInScope());
                            p.addTestParameter("invalid", v.invalid());
                            if (v.declare() != null)
                                p.addTestParameter("declare", v.declare().booleanValue());
                        },
                        new MatrixTest(org.apache.axiom.ts.om.element.TestSetNamespace.class)),
                new MatrixTest(org.apache.axiom.ts.om.element.TestSetText.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestSetTextEmptyString.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestSetTextNull.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSetTextWithExistingChildren.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestSetTextQName.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestSetTextQNameNull.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSetTextQNameWithEmptyPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSetTextQNameWithExistingChildren.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestSetTextQNameWithoutNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestUndeclarePrefix.class),
                new MatrixTest(org.apache.axiom.ts.om.element.TestWriteTextTo.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.element.TestWriteTextToWithNonTextNodes.class),
                // ── element/sr package ──
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.sr.TestCloseAndContinueBuilding
                                        .class)),
                new FanOutNode<>(
                        getInstances(BuilderFactory.class),
                        Binding.singleton(Key.get(BuilderFactory.class)),
                        ParameterBinding.DIMENSION,
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                                (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.element.sr.TestCommentEvent.class))),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.sr.TestGetBlobFromElement.class)),
                new MatrixTest(org.apache.axiom.ts.om.element.sr.TestGetElementText.class),
                new FanOutNode<>(
                        getInstances(BuilderFactory.class),
                        Binding.singleton(Key.get(BuilderFactory.class)),
                        ParameterBinding.DIMENSION,
                        new FanOutNode<>(
                                ImmutableList.of(true, false),
                                Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                                (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                                new FanOutNode<>(
                                        injector ->
                                                injector.getInstance(
                                                                Key.get(
                                                                        Boolean.class,
                                                                        Names.named("cache")))
                                                        ? ImmutableList.of(0)
                                                        : ImmutableList.of(0, 1, 2, 3, 4, 5),
                                        Binding.singleton(
                                                Key.get(Integer.class, Names.named("build"))),
                                        (inj, v, p) -> p.addTestParameter("build", v),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.element.sr
                                                        .TestGetElementTextFromParser.class)))),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (inj, v, p) -> p.addTestParameter("cache", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.element.sr.TestGetNamespaceContext.class)),
                new MatrixTest(org.apache.axiom.ts.om.element.sr.TestNextTag.class));
    }

    private static MatrixTestNode factoryTests() {
        return new ParentNode(
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMAttributeGeneratedPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMAttributeInterfaces.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMAttributeNullPrefixNoNamespace
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMAttributeWithInvalidNamespace1
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMAttributeWithInvalidNamespace2
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMCommentWithoutParent.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMDocTypeWithoutParent.class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestCreateOMDocument.class),
                createOMElementTests(),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullOMDataSource1
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullOMDataSource2
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMElementWithNullURIAndPrefix
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestCreateOMEntityReference.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMEntityReferenceWithNullParent
                                .class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestCreateOMNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory.TestCreateOMNamespaceWithNullURI.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.factory
                                .TestCreateOMProcessingInstructionWithoutParent.class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestCreateOMText.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("nullContentId"))),
                        (inj, v, p) -> p.addTestParameter("nullContentId", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.factory.TestCreateOMTextFromBlobProvider
                                        .class)),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestCreateOMTextFromOMText.class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestCreateOMTextWithNullParent.class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestFactoryIsSingleton.class),
                new MatrixTest(org.apache.axiom.ts.om.factory.TestGetMetaFactory.class));
    }

    private static MatrixTestNode createOMElementTests() {
        return new FanOutNode<>(
                ImmutableList.copyOf(CreateOMElementVariant.INSTANCES),
                Binding.singleton(Key.get(CreateOMElementVariant.class)),
                (inj, v, p) -> p.addTestParameter("variant", v.getName()),
                new ParentNode(
                        new FanOutNode<>(
                                injector -> {
                                    CreateOMElementVariant v =
                                            injector.getInstance(CreateOMElementVariant.class);
                                    return Arrays.stream(CreateOMElementParentSupplier.INSTANCES)
                                            .filter(ps -> ps.isSupported(v))
                                            .collect(ImmutableList.toImmutableList());
                                },
                                Binding.singleton(Key.get(CreateOMElementParentSupplier.class)),
                                (inj, v, p) -> p.addTestParameter("parent", v.getName()),
                                new ParentNode(
                                        new ConditionalNode(
                                                injector ->
                                                        injector.getInstance(
                                                                        CreateOMElementVariant
                                                                                .class)
                                                                .isSupportsDefaultNamespace(),
                                                new MatrixTest(
                                                        org.apache.axiom.ts.om.factory
                                                                .TestCreateOMElementWithDefaultNamespace
                                                                .class)),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithGeneratedPrefix
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithoutNamespaceNullPrefix
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithInvalidNamespace
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithNonDefaultNamespace
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithoutNamespace
                                                        .class))),
                        new ConditionalNode(
                                injector ->
                                        injector.getInstance(CreateOMElementVariant.class)
                                                .isSupportsContainer(),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithNamespaceInScope1
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithNamespaceInScope2
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithoutNamespace2
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithoutNamespace3
                                                        .class))),
                        new ConditionalNode(
                                injector -> {
                                    CreateOMElementVariant v =
                                            injector.getInstance(CreateOMElementVariant.class);
                                    return v.isSupportsContainer()
                                            && v.isSupportsDefaultNamespace();
                                },
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithNamespaceInScope3
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.factory
                                                        .TestCreateOMElementWithNamespaceInScope4
                                                        .class)))));
    }

    private static MatrixTestNode nodeTests() {
        return new ParentNode(
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("document"))),
                        (inj, v, p) -> p.addTestParameter("document", String.valueOf(v)),
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(Key.get(Boolean.class, Names.named("build"))),
                                (inj, v, p) -> p.addTestParameter("build", String.valueOf(v)),
                                new MatrixTest(org.apache.axiom.ts.om.node.TestDetach.class))),
                new MatrixTest(org.apache.axiom.ts.om.node.TestDetachAfterBuilderClose.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("build"))),
                        (inj, v, p) -> p.addTestParameter("build", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.node.TestDetachFirstChild.class)),
                new MatrixTest(org.apache.axiom.ts.om.node.TestGetNextOMSiblingAfterDiscard.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingAfter.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingAfterLastChild.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnChild.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnOrphan.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnSelf.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingAfterSameParent.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingBefore.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnChild.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnOrphan.class),
                new MatrixTest(org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnSelf.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.node.TestInsertSiblingBeforeSameParent.class));
    }

    private static MatrixTestNode sourcedElementTests() {
        return new ParentNode(
                new FanOutNode<>(
                        ImmutableList.copyOf(OMSourcedElementVariant.INSTANCES),
                        Binding.singleton(Key.get(OMSourcedElementVariant.class)),
                        (inj, v, p) -> {
                            p.addTestParameter("variant", v.getName());
                            v.addTestProperties(p);
                        },
                        new FanOutNode<>(
                                QNAMES,
                                Binding.singleton(Key.get(QName.class)),
                                QNAME_PARAMS,
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.sourcedelement
                                                        .TestGetLocalName.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.sourcedelement
                                                        .TestGetNamespace.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.sourcedelement.TestGetPrefix
                                                        .class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.sourcedelement
                                                        .TestGetNamespaceURI.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.om.sourcedelement.TestHasName
                                                        .class)))),
                new FanOutNode<>(
                        getInstances(AddAttributeStrategy.class),
                        Binding.singleton(Key.get(AddAttributeStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new MatrixTest(
                                org.apache.axiom.ts.om.sourcedelement.TestAddAttribute.class)),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestBlobOMDataSource.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestStringOMDataSource.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("copyOMDataSources"))),
                        (inj, v, p) -> p.addTestParameter("copyOMDataSources", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.sourcedelement.TestCloneNonDestructive
                                        .class)),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestCloneUnknownName.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestCloseOnComplete.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestComplete.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestDeclareNamespace.class),
                new FanOutNode<>(
                        getInstances(ExpansionStrategy.class),
                        Binding.singleton(Key.get(ExpansionStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.om.sourcedelement.TestDetach.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.sourcedelement.TestDiscard.class))),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestExpand.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestGetAllAttributes.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestGetAllDeclaredNamespaces.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestGetAttribute.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestGetAttributeValue.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestGetDocumentFromBuilder.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceNormalized.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestGetNamespaceNormalized2.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestGetNextOMSiblingIncomplete.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestGetObject.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestGetReaderException.class),
                new FanOutNode<>(
                        ImmutableList.copyOf(PushOMDataSourceScenario.INSTANCES),
                        Binding.singleton(Key.get(PushOMDataSourceScenario.class)),
                        ParameterBinding.DIMENSION,
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(
                                        Key.get(Boolean.class, Names.named("serializeParent"))),
                                (inj, v, p) ->
                                        p.addTestParameter("serializeParent", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.sourcedelement
                                                .TestGetSAXSourceWithPushOMDataSource.class))),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement
                                .TestGetSAXSourceWithPushOMDataSourceThrowingException.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement
                                .TestGetTextAsStreamWithNonDestructiveOMDataSource.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName1DefaultPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestName1QualifiedPrefix.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName1Unqualified.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName2DefaultPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestName2QualifiedPrefix.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName2Unqualified.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName3DefaultPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestName3QualifiedPrefix.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName3Unqualified.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName4DefaultPrefix.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestName4QualifiedPrefix.class),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestName4Unqualified.class),
                new FanOutNode<>(
                        ImmutableList.copyOf(PushOMDataSourceScenario.INSTANCES),
                        Binding.singleton(Key.get(PushOMDataSourceScenario.class)),
                        ParameterBinding.DIMENSION,
                        new MatrixTest(
                                org.apache.axiom.ts.om.sourcedelement.TestPushOMDataSourceExpansion
                                        .class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement.TestRemoveChildrenUnexpanded.class),
                sourcedElementSerializeTests(),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement
                                .TestSerializeModifiedOMSEWithNonDestructiveDataSource.class),
                new FanOutNode<>(
                        getInstances(SerializationStrategy.class),
                        Binding.singleton(Key.get(SerializationStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new FanOutNode<>(
                                ImmutableList.of(false, true),
                                Binding.singleton(
                                        Key.get(Boolean.class, Names.named("serializeParent"))),
                                (inj, v, p) ->
                                        p.addTestParameter("serializeParent", String.valueOf(v)),
                                new MatrixTest(
                                        org.apache.axiom.ts.om.sourcedelement
                                                .TestSerializeOMDataSourceWritingToOutputStream
                                                .class))),
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.TestSetDataSource.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement
                                .TestSetDataSourceOnAlreadyExpandedElement.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("expand"))),
                        (inj, v, p) -> p.addTestParameter("expand", String.valueOf(v)),
                        new MatrixTest(
                                org.apache.axiom.ts.om.sourcedelement.TestSetLocalName.class)),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement
                                .TestWrappedTextNodeOMDataSourceFromReader.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.sourcedelement
                                .TestWriteTextToWithNonDestructiveOMDataSource.class),
                // ── sourcedelement/sr package ──
                new MatrixTest(org.apache.axiom.ts.om.sourcedelement.sr.TestGetName.class),
                new FanOutNode<>(
                        ImmutableList.of(0, 1, 2, 3, 4, 5, 6),
                        Binding.singleton(Key.get(Integer.class, Names.named("events"))),
                        (inj, v, p) -> p.addTestParameter("events", v),
                        new MatrixTest(
                                org.apache.axiom.ts.om.sourcedelement.sr.TestCloseWithoutCaching
                                        .class)));
    }

    private static MatrixTestNode sourcedElementSerializeTests() {
        return new FanOutNode<>(
                getInstances(ElementContext.class),
                Binding.singleton(Key.get(ElementContext.class)),
                ParameterBinding.DIMENSION,
                new FanOutNode<>(
                        getInstances(ExpansionStrategy.class),
                        Binding.singleton(Key.get(ExpansionStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new FanOutNode<>(
                                getInstances(SerializationStrategy.class),
                                Binding.singleton(Key.get(SerializationStrategy.class)),
                                ParameterBinding.DIMENSION,
                                new FanOutNode<>(
                                        ImmutableList.of(1, 2),
                                        Binding.singleton(
                                                Key.get(Integer.class, Names.named("count"))),
                                        (inj, v, p) -> p.addTestParameter("count", v),
                                        new FanOutNode<>(
                                                injector ->
                                                        injector.getInstance(
                                                                                ExpansionStrategy
                                                                                        .class)
                                                                        != ExpansionStrategy.PARTIAL
                                                                ? ImmutableList.of(false, true)
                                                                : ImmutableList.of(false),
                                                Binding.singleton(
                                                        Key.get(
                                                                Boolean.class,
                                                                Names.named("push"))),
                                                (inj, v, p) ->
                                                        p.addTestParameter(
                                                                "push", String.valueOf(v)),
                                                new FanOutNode<>(
                                                        ImmutableList.of(false, true),
                                                        Binding.singleton(
                                                                Key.get(
                                                                        Boolean.class,
                                                                        Names.named(
                                                                                "destructive"))),
                                                        (inj, v, p) ->
                                                                p.addTestParameter(
                                                                        "destructive",
                                                                        String.valueOf(v)),
                                                        new FanOutNode<>(
                                                                injector ->
                                                                        injector.getInstance(
                                                                                                ElementContext
                                                                                                        .class)
                                                                                        != ElementContext
                                                                                                .ORPHAN
                                                                                ? ImmutableList.of(
                                                                                        false, true)
                                                                                : ImmutableList.of(
                                                                                        false),
                                                                Binding.singleton(
                                                                        Key.get(
                                                                                Boolean.class,
                                                                                Names.named(
                                                                                        "serializeParent"))),
                                                                (inj, v, p) ->
                                                                        p.addTestParameter(
                                                                                "serializeParent",
                                                                                String.valueOf(v)),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.om
                                                                                .sourcedelement
                                                                                .TestSerialize
                                                                                .class))))))));
    }

    private static MatrixTestNode textTests() {
        return new ParentNode(
                new MatrixTest(
                        org.apache.axiom.ts.om.text.TestBase64StreamingWithGetSAXSource.class),
                new MatrixTest(org.apache.axiom.ts.om.text.TestBase64StreamingWithSerialize.class),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("fetch"))),
                        (inj, v, p) -> p.addTestParameter("fetch", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.text.TestCloneBinary.class)),
                new MatrixTest(org.apache.axiom.ts.om.text.TestDigest.class),
                new MatrixTest(org.apache.axiom.ts.om.text.TestGetNamespace.class),
                new MatrixTest(org.apache.axiom.ts.om.text.TestGetNamespaceNoNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.om.text.TestGetTextCharactersFromDataHandler.class),
                new FanOutNode<Integer>(
                        ImmutableList.of(
                                (int) OMNode.TEXT_NODE,
                                (int) OMNode.SPACE_NODE,
                                (int) OMNode.CDATA_SECTION_NODE),
                        Binding.singleton(Key.get(Integer.class, Names.named("type"))),
                        (inj, v, p) ->
                                p.addTestParameter("type", XMLEventUtils.getEventTypeString(v)),
                        new MatrixTest(org.apache.axiom.ts.om.text.TestSerialize.class)));
    }

    private static MatrixTestNode xopTests() {
        return new ParentNode(
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("base64"))),
                        (inj, v, p) -> p.addTestParameter("base64", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.xop.TestSerialize.class)),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("optimize"))),
                        (inj, v, p) -> p.addTestParameter("optimize", String.valueOf(v)),
                        new MatrixTest(org.apache.axiom.ts.om.xop.TestSetOptimize.class)),
                new MatrixTest(org.apache.axiom.ts.om.xop.TestSetOptimizePlainOMText.class),
                new MatrixTest(org.apache.axiom.ts.om.xop.XOPRoundtripTest.class));
    }

    private static MatrixTestNode xpathTests() {
        ImmutableList<String> xpathMethods =
                Arrays.stream(AXIOMXPathTestCase.class.getMethods())
                        .map(Method::getName)
                        .filter(n -> n.startsWith("test"))
                        .collect(ImmutableList.toImmutableList());
        return new ParentNode(
                new FanOutNode<>(
                        xpathMethods,
                        Binding.singleton(Key.get(String.class, Names.named("methodName"))),
                        (inj, v, p) -> p.addTestParameter("test", v.substring(4)),
                        new MatrixTest(org.apache.axiom.ts.om.xpath.TestAXIOMXPath.class)),
                new MatrixTest(org.apache.axiom.ts.om.xpath.TestAddNamespaces.class),
                new MatrixTest(org.apache.axiom.ts.om.xpath.TestAddNamespaces2.class),
                new MatrixTest(org.apache.axiom.ts.om.xpath.TestGetAttributeQName.class));
    }
}
