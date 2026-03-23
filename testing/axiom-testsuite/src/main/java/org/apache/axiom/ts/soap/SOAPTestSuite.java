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
package org.apache.axiom.ts.soap;

import static org.apache.axiom.testing.multiton.Multiton.getInstances;

import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.ConditionalNode;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.ParameterBinding;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.ts.dimension.ExpansionStrategy;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

public class SOAPTestSuite {
    private static final ImmutableList<String> badSOAPFiles =
            ImmutableList.of(
                    "wrongSoapNs.xml",
                    "notnamespaceQualified.xml",
                    "soap11/twoheaders.xml",
                    "soap11/twoBodymessage.xml",
                    "soap11/envelopeMissing.xml",
                    "soap11/haederBodyWrongOrder.xml",
                    "soap11/invalid-faultcode.xml",
                    "soap11/invalid-faultstring.xml",
                    "soap11/invalid-faultactor.xml",
                    "soap11/processing-instruction.xml",
                    "soap11/entity-reference.xml",
                    "soap12/header-bad-case.xml",
                    "soap12/header-no-namespace.xml",
                    "soap12/processing-instruction.xml",
                    "soap12/entity-reference.xml",
                    "soap12/additional-element-after-body.xml");

    private static final ImmutableList<SOAPSample> goodSOAPFiles;

    static {
        ImmutableList.Builder<SOAPSample> builder = ImmutableList.builder();
        builder.add(
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/whitespacedMessage.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/minimalMessage.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/reallyReallyBigMessage.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/emtyBodymessage.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/soapfault.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/bodyNotQualified.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/faultelements-with-comment.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/additional-element-after-body.xml"),
                new SOAPSample(SOAPSpec.SOAP11, "soap/soap11/empty-header.xml"),
                new SOAPSample(SOAPSpec.SOAP12, "soap/soap12/empty-header.xml"));
        builder.addAll(Multiton.getInstances(SOAPSample.class));
        goodSOAPFiles = builder.build();
    }

    private static final ImmutableList<QName> generalQNames =
            ImmutableList.of(
                    new QName("root"),
                    new QName("urn:test", "root", "p"),
                    new QName("urn:test", "root"));

    private static final ImmutableList<QName> noFaultQNames =
            ImmutableList.of(
                    new QName("root"),
                    new QName("urn:test", "root", "p"),
                    new QName("urn:test", "root"),
                    new QName("Fault"),
                    new QName("urn:test", "Fault", "p"),
                    new QName(
                            SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                            "NoFault",
                            SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX),
                    new QName(
                            SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                            "NoFault",
                            SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX));

    private static final ParameterBinding<QName> QNAME_PARAMS =
            (injector, qname, params) -> {
                params.addTestParameter("prefix", qname.getPrefix());
                params.addTestParameter("uri", qname.getNamespaceURI());
                params.addTestParameter("localName", qname.getLocalPart());
            };

    public static InjectorNode create(OMMetaFactory metaFactory) {
        return new InjectorNode(
                binder -> binder.bind(OMMetaFactory.class).toInstance(metaFactory),
                new ParentNode(
                        // Per-spec tests (SOAP11 + SOAP12)
                        new FanOutNode<>(
                                Multiton.getInstances(SOAPSpec.class),
                                Binding.singleton(Key.get(SOAPSpec.class)),
                                (injector, value, params) ->
                                        params.addTestParameter("spec", value.getName()),
                                specTests()),
                        // Bad SOAP files (spec-independent)
                        new FanOutNode<>(
                                badSOAPFiles,
                                Binding.singleton(Key.get(String.class, Names.named("file"))),
                                (injector, value, params) -> params.addTestParameter("file", value),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.builder.BadInputTest.class)),
                        // Good SOAP files (spec-independent)
                        new FanOutNode<>(
                                goodSOAPFiles,
                                Binding.singleton(Key.get(SOAPSample.class)),
                                (injector, value, params) ->
                                        params.addTestParameter("message", value.getName()),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.soap.builder.MessageTest.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.soap.builder
                                                        .TestRegisterCustomBuilderForPayload.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.soap.envelope.TestClone.class),
                                        new FanOutNode<>(
                                                getInstances(ExpansionStrategy.class),
                                                Binding.singleton(Key.get(ExpansionStrategy.class)),
                                                ParameterBinding.DIMENSION,
                                                new FanOutNode<>(
                                                        getInstances(SerializationStrategy.class),
                                                        Binding.singleton(
                                                                Key.get(
                                                                        SerializationStrategy
                                                                                .class)),
                                                        ParameterBinding.DIMENSION,
                                                        new ParentNode(
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.soap
                                                                                .envelope
                                                                                .TestSerialize
                                                                                .class),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.soap
                                                                                .message
                                                                                .TestSerialize
                                                                                .class)))))),
                        // SOAP 1.1 specific tests
                        soap11Tests(),
                        // SOAP 1.2 specific tests
                        soap12Tests()));
    }

    private static MatrixTestNode specTests() {
        return new ParentNode(
                // ── body package ──
                new MatrixTest(org.apache.axiom.ts.soap.body.TestAddFault1.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestAddFault2.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestCloneOMElement.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestGetFault.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestGetFaultFakeFault.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestGetFaultWithParser.class),
                new FanOutNode<>(
                        generalQNames,
                        Binding.singleton(Key.get(QName.class)),
                        QNAME_PARAMS,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.body
                                                .TestGetFirstElementLocalNameWithParser.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.body
                                                .TestGetFirstElementNSWithParser.class))),
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("buildPayload"))),
                        (injector, value, params) ->
                                params.addTestParameter("buildPayload", String.valueOf(value)),
                        new MatrixTest(
                                org.apache.axiom.ts.soap.body
                                        .TestGetFirstElementLocalNameWithParser2.class)),
                new MatrixTest(
                        org.apache.axiom.ts.soap.body
                                .TestGetFirstElementLocalNameWithParserNoLookahead.class),
                new FanOutNode<>(
                        noFaultQNames,
                        Binding.singleton(Key.get(QName.class)),
                        QNAME_PARAMS,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.body.TestGetFaultNoFault.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.body.TestGetFaultWithParserNoFault
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.body.TestHasFaultNoFault.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.body.TestHasFaultWithParserNoFault
                                                .class))),
                new MatrixTest(
                        org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameEmptyBody.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestGetFirstElementNSEmptyBody.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestHasFault.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestHasFaultAfterReplace.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestHasFaultFakeFault.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestHasFaultWithOMSEUnknownName.class),
                new MatrixTest(org.apache.axiom.ts.soap.body.TestHasFaultWithParser.class),
                new FanOutNode<>(
                        getInstances(SerializationStrategy.class),
                        Binding.singleton(Key.get(SerializationStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new MatrixTest(
                                org.apache.axiom.ts.soap.body.TestSerializeWithXSITypeAttribute
                                        .class)),
                // ── builder package ──
                new MatrixTest(org.apache.axiom.ts.soap.builder.TestCommentInEpilog.class),
                new MatrixTest(org.apache.axiom.ts.soap.builder.TestCommentInProlog.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.builder.TestCreateSOAPModelBuilderFromDOMSource
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.builder.TestCreateSOAPModelBuilderFromSAXSource
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.builder
                                .TestCreateSOAPModelBuilderMTOMContentTypeMismatch.class),
                new MatrixTest(org.apache.axiom.ts.soap.builder.TestDTD.class),
                new MatrixTest(org.apache.axiom.ts.soap.builder.TestRegisterCustomBuilder.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.builder
                                .TestRegisterCustomBuilderForPayloadAfterSOAPFaultCheck.class),
                // ── envelope package ──
                new FanOutNode<>(
                        ImmutableList.of(false, true),
                        Binding.singleton(Key.get(Boolean.class, Names.named("header"))),
                        (injector, value, params) ->
                                params.addTestParameter("header", String.valueOf(value)),
                        new MatrixTest(
                                org.apache.axiom.ts.soap.envelope.TestAddElementAfterBody.class)),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestAddHeaderToIncompleteEnvelope.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestBodyHeaderOrder.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestCloneWithSourcedElement1.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestCloneWithSourcedElement2.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestDetach.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestGetBody.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestGetBodyOnEmptyEnvelope.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestGetBodyOnEnvelopeWithHeaderOnly
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestGetBodyWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestGetHeader.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestGetHeaderWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestGetHeaderWithParserNoHeader.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestGetOrCreateHeader.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestGetOrCreateHeaderWithParserNoHeader
                                .class),
                new FanOutNode<>(
                        generalQNames,
                        Binding.singleton(Key.get(QName.class)),
                        QNAME_PARAMS,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.envelope
                                                .TestGetSOAPBodyFirstElementLocalNameAndNS.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.envelope
                                                .TestGetSOAPBodyFirstElementLocalNameAndNSWithParser
                                                .class))),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope
                                .TestGetXMLStreamReaderWithoutCachingWithPartiallyBuiltHeaderBlock
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestHasFault.class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestHasFaultWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.envelope.TestSerializeAndConsumeWithOMSEInBody
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap.envelope.TestSerializeAsChild.class),
                // ── factory package ──
                new MatrixTest(org.apache.axiom.ts.soap.factory.TestCreateDefaultSOAPMessage.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.factory.TestCreateSOAPEnvelopeWithCustomPrefix
                                .class),
                factoryElementTypeTests(),
                new FanOutNode<>(
                        ImmutableList.of(true, false),
                        Binding.singleton(Key.get(Boolean.class, Names.named("withParent"))),
                        (injector, value, params) ->
                                params.addTestParameter("withParent", String.valueOf(value)),
                        new MatrixTest(
                                org.apache.axiom.ts.soap.factory.TestCreateSOAPFaultWithException
                                        .class)),
                new MatrixTest(
                        org.apache.axiom.ts.soap.factory.TestCreateSOAPHeaderBlockFromOMElement
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap.factory.TestFactoryIsSingleton.class),
                new MatrixTest(org.apache.axiom.ts.soap.factory.TestGetDefaultEnvelope.class),
                new MatrixTest(org.apache.axiom.ts.soap.factory.TestGetDefaultFaultEnvelope.class),
                new MatrixTest(org.apache.axiom.ts.soap.factory.TestGetMetaFactory.class),
                new MatrixTest(org.apache.axiom.ts.soap.factory.TestGetNamespace.class),
                // ── fault package ──
                new FanOutNode<>(
                        getInstances(SerializationStrategy.class),
                        Binding.singleton(Key.get(SerializationStrategy.class)),
                        ParameterBinding.DIMENSION,
                        new FanOutNode<>(
                                ImmutableList.of(
                                        new SOAPFaultChild[] {
                                            SOAPFaultChild.REASON, SOAPFaultChild.CODE
                                        },
                                        new SOAPFaultChild[] {
                                            SOAPFaultChild.CODE,
                                            SOAPFaultChild.REASON,
                                            SOAPFaultChild.DETAIL,
                                            SOAPFaultChild.REASON
                                        }),
                                Binding.singleton(
                                        Key.get(SOAPFaultChild[].class, Names.named("inputOrder"))),
                                (injector, value, params) -> {
                                    StringBuilder buffer = new StringBuilder();
                                    for (int i = 0; i < value.length; i++) {
                                        if (i > 0) {
                                            buffer.append(',');
                                        }
                                        buffer.append(
                                                value[i].getAdapter(SOAPElementTypeAdapter.class)
                                                        .getType()
                                                        .getSimpleName());
                                    }
                                    params.addTestParameter("inputOrder", buffer.toString());
                                },
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.fault.TestChildOrder.class))),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestGetCodeWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestGetDetailWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestGetException.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestGetReasonWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestGetRoleWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestSetException.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestWrongParent1.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestWrongParent2.class),
                new MatrixTest(org.apache.axiom.ts.soap.fault.TestWrongParent3.class),
                // ── faultcode package ──
                new MatrixTest(org.apache.axiom.ts.soap.faultcode.TestGetValueAsQName.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.faultcode.TestGetValueAsQNameWithParser.class),
                // ── faultdetail package ──
                new MatrixTest(org.apache.axiom.ts.soap.faultdetail.TestAddDetailEntry.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.faultdetail
                                .TestDetailEntriesUsingDefaultNamespaceWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntries.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntriesWithParser
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap.faultdetail.TestSerialization.class),
                new MatrixTest(org.apache.axiom.ts.soap.faultdetail.TestWSCommons202.class),
                // ── faulttext package (conditional) ──
                new ConditionalNode(
                        injector ->
                                injector.getInstance(SOAPSpec.class).getFaultTextQName() != null,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.faulttext.TestGetLang.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.faulttext.TestGetLangFromParser
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.faulttext.TestSetLang.class))),
                // ── faultnode package (conditional) ──
                new ConditionalNode(
                        injector ->
                                injector.getInstance(SOAPSpec.class).getFaultNodeQName() != null,
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.faultnode.TestGetFaultNodeValue
                                                .class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.faultnode
                                                .TestGetFaultNodeValueWithParser.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.faultnode.TestSetFaultNodeValue
                                                .class))),
                // ── faultreason package ──
                new MatrixTest(org.apache.axiom.ts.soap.faultreason.TestGetFaultReasonText.class),
                // ── faultrole package ──
                new MatrixTest(org.apache.axiom.ts.soap.faultrole.TestGetRoleValue.class),
                new MatrixTest(org.apache.axiom.ts.soap.faultrole.TestGetRoleValueWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.faultrole.TestSetRoleValue.class),
                // ── header package ──
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestAddChildWithPlainOMElement.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestAddHeaderBlock.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestAddHeaderBlockFromQName.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestAddHeaderBlockFromQNameWithoutNamespace
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestAddHeaderBlockWithoutNamespace1.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestAddHeaderBlockWithoutNamespace2.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestDiscardIncomplete.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestDiscardPartiallyBuilt.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestExamineAllHeaderBlocks.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestExamineAllHeaderBlocksWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestExamineHeaderBlocks.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestExamineHeaderBlocksWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header
                                .TestExamineMustUnderstandHeaderBlocksWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestExtractAllHeaderBlocks.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithName.class),
                new MatrixTest(org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithNSURI.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithNSURIWithParser
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestGetHeadersToProcessWithNamespace.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.header.TestGetHeadersToProcessWithParser.class),
                // ── headerblock package ──
                headerBlockAttributeTests(),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestBlobOMDataSource.class),
                new FanOutNode<>(
                        // This is non-standard because of the null value.
                        injector -> Arrays.asList(Boolean.TRUE, Boolean.FALSE, null),
                        (binder, value) ->
                                binder.bind(Boolean.class)
                                        .annotatedWith(Names.named("processed"))
                                        .toProvider(Providers.of(value)),
                        (injector, value, params) ->
                                params.addTestParameter("processed", String.valueOf(value)),
                        new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestClone.class)),
                new MatrixTest(
                        org.apache.axiom.ts.soap.headerblock
                                .TestCloneProcessedWithoutPreservingModel.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.headerblock.TestGetMustUnderstandWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestGetRole.class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestGetRoleWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestGetVersion.class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestSetRole.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.headerblock.TestSetRoleWithoutExistingNamespaceDecl
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestWrongParent1.class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestWrongParent2.class),
                new MatrixTest(org.apache.axiom.ts.soap.headerblock.TestWrongParent3.class),
                // ── message package ──
                new FanOutNode<>(
                        ImmutableList.of(true, false),
                        Binding.singleton(Key.get(Boolean.class, Names.named("preserveModel"))),
                        (injector, value, params) ->
                                params.addTestParameter("preserveModel", String.valueOf(value)),
                        new ParentNode(
                                new MatrixTest(org.apache.axiom.ts.soap.message.TestClone.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.message.TestCloneIncomplete
                                                .class))),
                new MatrixTest(
                        org.apache.axiom.ts.soap.message.TestGetCharsetEncodingWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.message.TestGetOMFactoryWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap.message.TestSetOMDocumentElement.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap.message.TestSetOMDocumentElementNonSOAPEnvelope
                                .class),
                // ── misc package ──
                miscElementTypeTests(),
                // ── xpath package ──
                new FanOutNode<>(
                        ImmutableList.of(true, false),
                        Binding.singleton(Key.get(Boolean.class, Names.named("createDocument"))),
                        (injector, value, params) ->
                                params.addTestParameter("createDocument", String.valueOf(value)),
                        new MatrixTest(
                                org.apache.axiom.ts.soap.xpath.TestXPathAppliedToSOAPEnvelope
                                        .class)));
    }

    private static MatrixTestNode factoryElementTypeTests() {
        return new FanOutNode<>(
                ImmutableList.copyOf(SOAPElementType.getAll()),
                Binding.singleton(Key.get(SOAPElementType.class, Names.named("type"))),
                (injector, value, params) ->
                        value.getAdapter(SOAPElementTypeAdapter.class).addTestParameters(params),
                new ParentNode(
                        new MatrixTest(
                                org.apache.axiom.ts.soap.factory.TestCreateSOAPElement.class),
                        new FanOutNode<>(
                                injector ->
                                        ImmutableList.copyOf(
                                                injector.getInstance(
                                                                Key.get(
                                                                        SOAPElementType.class,
                                                                        Names.named("type")))
                                                        .getChildTypes()),
                                Binding.singleton(
                                        Key.get(SOAPElementType.class, Names.named("childType"))),
                                (injector, value, params) ->
                                        params.addTestParameter(
                                                "childType",
                                                value.getAdapter(SOAPElementTypeAdapter.class)
                                                        .getType()
                                                        .getSimpleName()),
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.soap.factory
                                                        .TestCreateSOAPElementWithNullParent.class),
                                        new ConditionalNode(
                                                injector -> {
                                                    SOAPSpec spec =
                                                            injector.getInstance(SOAPSpec.class);
                                                    SOAPElementType type =
                                                            injector.getInstance(
                                                                    Key.get(
                                                                            SOAPElementType.class,
                                                                            Names.named("type")));
                                                    return type.getQName(spec) != null;
                                                },
                                                new MatrixTest(
                                                        org.apache.axiom.ts.soap.factory
                                                                .TestCreateSOAPElementWithParent
                                                                .class))))));
    }

    private static MatrixTestNode miscElementTypeTests() {
        return new FanOutNode<>(
                injector -> {
                    SOAPSpec spec = injector.getInstance(SOAPSpec.class);
                    return Arrays.stream(SOAPElementType.getAll())
                            .filter(type -> type.getQName(spec) != null)
                            .collect(ImmutableList.toImmutableList());
                },
                Binding.singleton(Key.get(SOAPElementType.class, Names.named("type"))),
                (injector, value, params) ->
                        params.addTestParameter(
                                "type",
                                value.getAdapter(SOAPElementTypeAdapter.class)
                                        .getType()
                                        .getSimpleName()),
                new FanOutNode<>(
                        injector -> {
                            SOAPSpec spec = injector.getInstance(SOAPSpec.class);
                            SOAPElementType type =
                                    injector.getInstance(
                                            Key.get(SOAPElementType.class, Names.named("type")));
                            return Arrays.stream(type.getChildTypes())
                                    .filter(childType -> childType.getQName(spec) != null)
                                    .collect(ImmutableList.toImmutableList());
                        },
                        Binding.singleton(Key.get(SOAPElementType.class, Names.named("childType"))),
                        (injector, value, params) ->
                                params.addTestParameter(
                                        "childType",
                                        value.getAdapter(SOAPElementTypeAdapter.class)
                                                .getType()
                                                .getSimpleName()),
                        new ParentNode(
                                new ConditionalNode(
                                        injector ->
                                                injector.getInstance(
                                                                        Key.get(
                                                                                SOAPElementType
                                                                                        .class,
                                                                                Names.named(
                                                                                        "childType")))
                                                                .getAdapter(
                                                                        SOAPElementTypeAdapter
                                                                                .class)
                                                                .getGetter()
                                                        != null,
                                        new MatrixTest(
                                                org.apache.axiom.ts.soap.misc.TestGetChild.class)),
                                new ConditionalNode(
                                        injector ->
                                                injector.getInstance(
                                                                        Key.get(
                                                                                SOAPElementType
                                                                                        .class,
                                                                                Names.named(
                                                                                        "childType")))
                                                                .getAdapter(
                                                                        SOAPElementTypeAdapter
                                                                                .class)
                                                                .getSetter()
                                                        != null,
                                        new ParentNode(
                                                new MatrixTest(
                                                        org.apache.axiom.ts.soap.misc.TestSetChild
                                                                .class),
                                                new ConditionalNode(
                                                        injector -> {
                                                            SOAPSpec spec =
                                                                    injector.getInstance(
                                                                            SOAPSpec.class);
                                                            SOAPElementType childType =
                                                                    injector.getInstance(
                                                                            Key.get(
                                                                                    SOAPElementType
                                                                                            .class,
                                                                                    Names.named(
                                                                                            "childType")));
                                                            return childType.getQName(
                                                                            spec.getAltSpec())
                                                                    != null;
                                                        },
                                                        new MatrixTest(
                                                                org.apache.axiom.ts.soap.misc
                                                                        .TestSetChildVersionMismatch
                                                                        .class)))))));
    }

    private static MatrixTestNode headerBlockAttributeTests() {
        return new FanOutNode<>(
                getInstances(HeaderBlockAttribute.class),
                Binding.singleton(Key.get(HeaderBlockAttribute.class)),
                (injector, value, params) ->
                        params.addTestParameter(
                                "attribute", value.getName(injector.getInstance(SOAPSpec.class))),
                new ParentNode(
                        new ConditionalNode(
                                injector ->
                                        injector.getInstance(HeaderBlockAttribute.class)
                                                .isSupported(injector.getInstance(SOAPSpec.class)),
                                new MatrixTest(
                                        org.apache.axiom.ts.soap.headerblock
                                                .TestSetAttributeNamespacePrefix.class)),
                        new ConditionalNode(
                                injector ->
                                        injector.getInstance(HeaderBlockAttribute.class)
                                                .isBoolean(),
                                new ParentNode(
                                        new ConditionalNode(
                                                injector ->
                                                        injector.getInstance(
                                                                        HeaderBlockAttribute.class)
                                                                .isSupported(
                                                                        injector.getInstance(
                                                                                SOAPSpec.class)),
                                                new ParentNode(
                                                        new FanOutNode<>(
                                                                injector ->
                                                                        ImmutableList.copyOf(
                                                                                injector.getInstance(
                                                                                                SOAPSpec
                                                                                                        .class)
                                                                                        .getBooleanLiterals()),
                                                                Binding.singleton(
                                                                        Key.get(
                                                                                BooleanLiteral
                                                                                        .class)),
                                                                (injector, value, params) ->
                                                                        params.addTestParameter(
                                                                                "literal",
                                                                                value
                                                                                        .getLexicalRepresentation()),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.soap
                                                                                .headerblock
                                                                                .TestGetBooleanAttribute
                                                                                .class)),
                                                        new MatrixTest(
                                                                org.apache.axiom.ts.soap.headerblock
                                                                        .TestGetBooleanAttributeDefault
                                                                        .class),
                                                        new FanOutNode<>(
                                                                injector ->
                                                                        ImmutableList.copyOf(
                                                                                injector.getInstance(
                                                                                                SOAPSpec
                                                                                                        .class)
                                                                                        .getInvalidBooleanLiterals()),
                                                                Binding.singleton(
                                                                        Key.get(
                                                                                String.class,
                                                                                Names.named(
                                                                                        "value"))),
                                                                (injector, value, params) ->
                                                                        params.addTestParameter(
                                                                                "value", value),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.soap
                                                                                .headerblock
                                                                                .TestGetBooleanAttributeInvalid
                                                                                .class)),
                                                        new FanOutNode<>(
                                                                ImmutableList.of(true, false),
                                                                Binding.singleton(
                                                                        Key.get(
                                                                                Boolean.class,
                                                                                Names.named(
                                                                                        "value"))),
                                                                (injector, value, params) ->
                                                                        params.addTestParameter(
                                                                                "value",
                                                                                String.valueOf(
                                                                                        value)),
                                                                new MatrixTest(
                                                                        org.apache.axiom.ts.soap
                                                                                .headerblock
                                                                                .TestSetBooleanAttribute
                                                                                .class)))),
                                        new ConditionalNode(
                                                injector ->
                                                        !injector.getInstance(
                                                                        HeaderBlockAttribute.class)
                                                                .isSupported(
                                                                        injector.getInstance(
                                                                                SOAPSpec.class)),
                                                new ParentNode(
                                                        new MatrixTest(
                                                                org.apache.axiom.ts.soap.headerblock
                                                                        .TestGetBooleanAttributeUnspported
                                                                        .class),
                                                        new MatrixTest(
                                                                org.apache.axiom.ts.soap.headerblock
                                                                        .TestSetBooleanAttributeUnsupported
                                                                        .class)))))));
    }

    private static MatrixTestNode soap11Tests() {
        return new ParentNode(
                new MatrixTest(org.apache.axiom.ts.soap11.builder.TestBuilder.class),
                new MatrixTest(org.apache.axiom.ts.soap11.fault.TestGetNode.class),
                new MatrixTest(org.apache.axiom.ts.soap11.fault.TestSetNode.class),
                new MatrixTest(org.apache.axiom.ts.soap11.faultcode.TestGetValue.class),
                new MatrixTest(org.apache.axiom.ts.soap11.faultcode.TestGetValueWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap11.faultcode.TestSetValueFromQName.class),
                new MatrixTest(org.apache.axiom.ts.soap11.faultreason.TestAddSOAPText.class),
                new MatrixTest(org.apache.axiom.ts.soap11.faultreason.TestGetFirstSOAPText.class),
                new MatrixTest(org.apache.axiom.ts.soap11.faultreason.TestGetTextWithCDATA.class));
    }

    private static MatrixTestNode soap12Tests() {
        return new ParentNode(
                new MatrixTest(org.apache.axiom.ts.soap12.builder.TestBuilder.class),
                new MatrixTest(org.apache.axiom.ts.soap12.envelope.TestBuildWithAttachments.class),
                new FanOutNode<>(
                        ImmutableList.of(true, false),
                        Binding.singleton(Key.get(Boolean.class, Names.named("buildSOAPPart"))),
                        (injector, value, params) ->
                                params.addTestParameter("buildSOAPPart", String.valueOf(value)),
                        new MatrixTest(
                                org.apache.axiom.ts.soap12.envelope.TestMTOMForwardStreaming
                                        .class)),
                new MatrixTest(org.apache.axiom.ts.soap12.factory.TestCreateSOAPFaultSubCode.class),
                new MatrixTest(org.apache.axiom.ts.soap12.fault.TestGetNode.class),
                new MatrixTest(org.apache.axiom.ts.soap12.fault.TestGetNodeWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap12.fault.TestMoreChildrenAddition.class),
                new MatrixTest(org.apache.axiom.ts.soap12.fault.TestSetNode.class),
                new MatrixTest(org.apache.axiom.ts.soap12.faultcode.TestGetSubCodeWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap12.faultcode.TestGetValueWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap12.faultcode.TestSetValueFromQName.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultcode.TestSetValueFromQNameWithExistingValue
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap12.faultreason.TestAddSOAPText.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultreason.TestAddSOAPTextMultiple.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultreason
                                .TestAddSOAPTextWithSOAPVersionMismatch.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultreason.TestGetAllSoapTextsWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap12.faultreason.TestGetFaultReasonText.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultreason.TestGetFaultReasonTextCaseSensitivity
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultreason
                                .TestGetFaultReasonTextWithoutLangAttribute.class),
                new MatrixTest(org.apache.axiom.ts.soap12.faultreason.TestGetFirstSOAPText.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultreason.TestGetFirstSOAPTextWithParser
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultsubcode.TestGetSubCodeNestedWithParser
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultsubcode.TestGetSubCodeWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultsubcode.TestGetValueNestedWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultsubcode.TestGetValueAsQNameWithParser
                                .class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.faultsubcode.TestGetValueWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap12.faulttext.TestGetLangWithParser.class),
                new MatrixTest(
                        org.apache.axiom.ts.soap12.header.TestExamineMustUnderstandHeaderBlocks
                                .class),
                new MatrixTest(org.apache.axiom.ts.soap12.headerblock.TestGetRelayWithParser.class),
                new MatrixTest(org.apache.axiom.ts.soap12.mtom.TestBuilderDetach.class),
                new FanOutNode<>(
                        ImmutableList.of(true, false),
                        Binding.singleton(Key.get(Boolean.class, Names.named("cache"))),
                        (injector, value, params) ->
                                params.addTestParameter("cache", String.valueOf(value)),
                        new MatrixTest(
                                org.apache.axiom.ts.soap12.mtom.TestGetXMLStreamReaderMTOMEncoded
                                        .class)));
    }
}
