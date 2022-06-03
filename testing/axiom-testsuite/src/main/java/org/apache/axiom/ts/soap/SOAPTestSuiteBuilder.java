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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.dimension.ExpansionStrategy;
import org.apache.axiom.ts.dimension.serialization.SerializationStrategy;

public class SOAPTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private static final String[] badSOAPFiles = {
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
        "soap12/additional-element-after-body.xml"
    };

    private static final List<SOAPSample> goodSOAPFiles;

    static {
        goodSOAPFiles = new ArrayList<SOAPSample>();
        goodSOAPFiles.addAll(
                Arrays.asList(
                        new SimpleSOAPSample(SOAPSpec.SOAP11, "soap/soap11/whitespacedMessage.xml"),
                        new SimpleSOAPSample(SOAPSpec.SOAP11, "soap/soap11/minimalMessage.xml"),
                        new SimpleSOAPSample(
                                SOAPSpec.SOAP11, "soap/soap11/reallyReallyBigMessage.xml"),
                        new SimpleSOAPSample(SOAPSpec.SOAP11, "soap/soap11/emtyBodymessage.xml"),
                        new SimpleSOAPSample(SOAPSpec.SOAP11, "soap/soap11/soapfault.xml"),
                        new SimpleSOAPSample(SOAPSpec.SOAP11, "soap/soap11/bodyNotQualified.xml"),
                        new SimpleSOAPSample(
                                SOAPSpec.SOAP11, "soap/soap11/faultelements-with-comment.xml"),
                        new SimpleSOAPSample(
                                SOAPSpec.SOAP11, "soap/soap11/additional-element-after-body.xml"),
                        new SimpleSOAPSample(SOAPSpec.SOAP11, "soap/soap11/empty-header.xml"),
                        new SimpleSOAPSample(SOAPSpec.SOAP12, "soap/soap12/empty-header.xml")));
        goodSOAPFiles.addAll(Multiton.getInstances(SOAPSample.class));
    }

    private static final QName[] generalQNames = {
        new QName("root"), new QName("urn:test", "root", "p"), new QName("urn:test", "root")
    };

    private static final QName[] noFaultQNames = {
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
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX)
    };

    private final OMMetaFactory metaFactory;

    /**
     * Constructor.
     *
     * @param metaFactory
     */
    public SOAPTestSuiteBuilder(OMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }

    private void addTests(SOAPSpec spec) {
        BooleanLiteral[] booleanLiterals = spec.getBooleanLiterals();
        String[] invalidBooleanLiterals = spec.getInvalidBooleanLiterals();
        addTest(new org.apache.axiom.ts.soap.body.TestAddFault1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestAddFault2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestCloneOMElement(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFaultFakeFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFaultWithParser(metaFactory, spec));
        for (int i = 0; i < generalQNames.length; i++) {
            QName qname = generalQNames[i];
            addTest(
                    new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameWithParser(
                            metaFactory, spec, qname));
            addTest(
                    new org.apache.axiom.ts.soap.body.TestGetFirstElementNSWithParser(
                            metaFactory, spec, qname));
        }
        addTest(
                new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameWithParser2(
                        metaFactory, spec, false));
        addTest(
                new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameWithParser2(
                        metaFactory, spec, true));
        addTest(
                new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameWithParserNoLookahead(
                        metaFactory, spec));
        for (int i = 0; i < noFaultQNames.length; i++) {
            QName qname = noFaultQNames[i];
            addTest(
                    new org.apache.axiom.ts.soap.body.TestGetFaultNoFault(
                            metaFactory, spec, qname));
            addTest(
                    new org.apache.axiom.ts.soap.body.TestGetFaultWithParserNoFault(
                            metaFactory, spec, qname));
            addTest(
                    new org.apache.axiom.ts.soap.body.TestHasFaultNoFault(
                            metaFactory, spec, qname));
            addTest(
                    new org.apache.axiom.ts.soap.body.TestHasFaultWithParserNoFault(
                            metaFactory, spec, qname));
        }
        addTest(
                new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameEmptyBody(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.body.TestGetFirstElementNSEmptyBody(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultAfterReplace(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultFakeFault(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.body.TestHasFaultWithOMSEUnknownName(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultWithParser(metaFactory, spec));
        for (SerializationStrategy serializationStrategy :
                getInstances(SerializationStrategy.class)) {
            addTest(
                    new org.apache.axiom.ts.soap.body.TestSerializeWithXSITypeAttribute(
                            metaFactory, spec, serializationStrategy));
        }
        addTest(new org.apache.axiom.ts.soap.builder.TestCommentInEpilog(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.builder.TestCommentInProlog(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.builder.TestCreateSOAPModelBuilderFromDOMSource(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.builder.TestCreateSOAPModelBuilderFromSAXSource(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.builder
                        .TestCreateSOAPModelBuilderMTOMContentTypeMismatch(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.builder.TestDTD(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.builder.TestRegisterCustomBuilder(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.builder
                        .TestRegisterCustomBuilderForPayloadAfterSOAPFaultCheck(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestAddElementAfterBody(
                        metaFactory, spec, false));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestAddElementAfterBody(
                        metaFactory, spec, true));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestAddHeaderToIncompleteEnvelope(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestBodyHeaderOrder(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestCloneWithSourcedElement1(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestCloneWithSourcedElement2(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestDetach(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBody(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestGetBodyOnEmptyEnvelope(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestGetBodyOnEnvelopeWithHeaderOnly(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetHeader(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetHeaderWithParser(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestGetHeaderWithParserNoHeader(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetOrCreateHeader(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestGetOrCreateHeaderWithParserNoHeader(
                        metaFactory, spec));
        for (int i = 0; i < generalQNames.length; i++) {
            QName qname = generalQNames[i];
            addTest(
                    new org.apache.axiom.ts.soap.envelope.TestGetSOAPBodyFirstElementLocalNameAndNS(
                            metaFactory, spec, qname));
            addTest(
                    new org.apache.axiom.ts.soap.envelope
                            .TestGetSOAPBodyFirstElementLocalNameAndNSWithParser(
                            metaFactory, spec, qname));
        }
        addTest(
                new org.apache.axiom.ts.soap.envelope
                        .TestGetXMLStreamReaderWithoutCachingWithPartiallyBuiltHeaderBlock(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestHasFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestHasFaultWithParser(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.envelope.TestSerializeAndConsumeWithOMSEInBody(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestSerializeAsChild(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.factory.TestCreateDefaultSOAPMessage(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.factory.TestCreateSOAPEnvelopeWithCustomPrefix(
                        metaFactory, spec));
        for (SOAPElementType type : SOAPElementType.getAll()) {
            addTest(
                    new org.apache.axiom.ts.soap.factory.TestCreateSOAPElement(
                            metaFactory, spec, type));
            for (SOAPElementType childType : type.getChildTypes()) {
                addTest(
                        new org.apache.axiom.ts.soap.factory.TestCreateSOAPElementWithNullParent(
                                metaFactory, spec, childType, type));
                if (type.getQName(spec) != null) {
                    addTest(
                            new org.apache.axiom.ts.soap.factory.TestCreateSOAPElementWithParent(
                                    metaFactory, spec, childType, type));
                }
            }
        }
        addTest(
                new org.apache.axiom.ts.soap.factory.TestCreateSOAPFaultWithException(
                        metaFactory, spec, true));
        addTest(
                new org.apache.axiom.ts.soap.factory.TestCreateSOAPFaultWithException(
                        metaFactory, spec, false));
        addTest(
                new org.apache.axiom.ts.soap.factory.TestCreateSOAPHeaderBlockFromOMElement(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestFactoryIsSingleton(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetDefaultEnvelope(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.factory.TestGetDefaultFaultEnvelope(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetMetaFactory(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetNamespace(metaFactory, spec));
        for (SerializationStrategy ss : getInstances(SerializationStrategy.class)) {
            addTest(
                    new org.apache.axiom.ts.soap.fault.TestChildOrder(
                            metaFactory,
                            spec,
                            new SOAPFaultChild[] {SOAPFaultChild.REASON, SOAPFaultChild.CODE},
                            ss));
            addTest(
                    new org.apache.axiom.ts.soap.fault.TestChildOrder(
                            metaFactory,
                            spec,
                            new SOAPFaultChild[] {
                                SOAPFaultChild.CODE,
                                SOAPFaultChild.REASON,
                                SOAPFaultChild.DETAIL,
                                SOAPFaultChild.REASON
                            },
                            ss));
        }
        addTest(new org.apache.axiom.ts.soap.fault.TestGetCodeWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetDetailWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetException(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetReasonWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetRoleWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestSetException(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestWrongParent1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestWrongParent2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestWrongParent3(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultcode.TestGetValueAsQName(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.faultcode.TestGetValueAsQNameWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestAddDetailEntry(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.faultdetail
                        .TestDetailEntriesUsingDefaultNamespaceWithParser(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntries(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntriesWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestSerialization(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestWSCommons202(metaFactory, spec));
        if (spec.getFaultTextQName() != null) {
            addTest(new org.apache.axiom.ts.soap.faulttext.TestGetLang(metaFactory, spec));
            addTest(
                    new org.apache.axiom.ts.soap.faulttext.TestGetLangFromParser(
                            metaFactory, spec));
            addTest(new org.apache.axiom.ts.soap.faulttext.TestSetLang(metaFactory, spec));
        }
        if (spec.getFaultNodeQName() != null) {
            addTest(
                    new org.apache.axiom.ts.soap.faultnode.TestGetFaultNodeValue(
                            metaFactory, spec));
            addTest(
                    new org.apache.axiom.ts.soap.faultnode.TestGetFaultNodeValueWithParser(
                            metaFactory, spec));
            addTest(
                    new org.apache.axiom.ts.soap.faultnode.TestSetFaultNodeValue(
                            metaFactory, spec));
        }
        addTest(new org.apache.axiom.ts.soap.faultreason.TestGetFaultReasonText(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultrole.TestGetRoleValue(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.faultrole.TestGetRoleValueWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultrole.TestSetRoleValue(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestAddChildWithPlainOMElement(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestAddHeaderBlock(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestAddHeaderBlockFromQName(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestAddHeaderBlockFromQNameWithoutNamespace(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestAddHeaderBlockWithoutNamespace1(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestAddHeaderBlockWithoutNamespace2(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestDiscardIncomplete(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestDiscardPartiallyBuilt(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExamineAllHeaderBlocks(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestExamineAllHeaderBlocksWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExamineHeaderBlocks(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestExamineHeaderBlocksWithParser(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestExamineMustUnderstandHeaderBlocksWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExtractAllHeaderBlocks(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithName(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithNSURI(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithNSURIWithParser(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestGetHeadersToProcessWithNamespace(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.header.TestGetHeadersToProcessWithParser(
                        metaFactory, spec));
        for (HeaderBlockAttribute attribute : getInstances(HeaderBlockAttribute.class)) {
            if (attribute.isBoolean()) {
                if (attribute.isSupported(spec)) {
                    for (int j = 0; j < booleanLiterals.length; j++) {
                        addTest(
                                new org.apache.axiom.ts.soap.headerblock.TestGetBooleanAttribute(
                                        metaFactory, spec, attribute, booleanLiterals[j]));
                    }
                    addTest(
                            new org.apache.axiom.ts.soap.headerblock.TestGetBooleanAttributeDefault(
                                    metaFactory, spec, attribute));
                    for (int j = 0; j < invalidBooleanLiterals.length; j++) {
                        addTest(
                                new org.apache.axiom.ts.soap.headerblock
                                        .TestGetBooleanAttributeInvalid(
                                        metaFactory, spec, attribute, invalidBooleanLiterals[j]));
                    }
                    addTest(
                            new org.apache.axiom.ts.soap.headerblock.TestSetBooleanAttribute(
                                    metaFactory, spec, attribute, true));
                    addTest(
                            new org.apache.axiom.ts.soap.headerblock.TestSetBooleanAttribute(
                                    metaFactory, spec, attribute, false));
                } else {
                    addTest(
                            new org.apache.axiom.ts.soap.headerblock
                                    .TestGetBooleanAttributeUnspported(
                                    metaFactory, spec, attribute));
                    addTest(
                            new org.apache.axiom.ts.soap.headerblock
                                    .TestSetBooleanAttributeUnsupported(
                                    metaFactory, spec, attribute));
                }
            }
        }
        addTest(new org.apache.axiom.ts.soap.headerblock.TestBlobOMDataSource(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.headerblock.TestClone(
                        metaFactory, spec, Boolean.TRUE));
        addTest(
                new org.apache.axiom.ts.soap.headerblock.TestClone(
                        metaFactory, spec, Boolean.FALSE));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestClone(metaFactory, spec, null));
        addTest(
                new org.apache.axiom.ts.soap.headerblock.TestCloneProcessedWithoutPreservingModel(
                        metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.headerblock.TestGetMustUnderstandWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestGetRole(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestGetRoleWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestGetVersion(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestSetRole(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.headerblock.TestSetRoleWithoutExistingNamespaceDecl(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestWrongParent1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestWrongParent2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestWrongParent3(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.message.TestClone(metaFactory, spec, true));
        addTest(new org.apache.axiom.ts.soap.message.TestClone(metaFactory, spec, false));
        addTest(new org.apache.axiom.ts.soap.message.TestCloneIncomplete(metaFactory, spec, true));
        addTest(new org.apache.axiom.ts.soap.message.TestCloneIncomplete(metaFactory, spec, false));
        addTest(
                new org.apache.axiom.ts.soap.message.TestGetCharsetEncodingWithParser(
                        metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.message.TestGetOMFactoryWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.message.TestSetOMDocumentElement(metaFactory, spec));
        addTest(
                new org.apache.axiom.ts.soap.message.TestSetOMDocumentElementNonSOAPEnvelope(
                        metaFactory, spec));
        for (SOAPElementType type : SOAPElementType.getAll()) {
            if (type.getQName(spec) != null) {
                for (SOAPElementType childType : type.getChildTypes()) {
                    if (childType.getQName(spec) != null) {
                        if (childType.getAdapter(SOAPElementTypeAdapter.class).getGetter()
                                != null) {
                            addTest(
                                    new org.apache.axiom.ts.soap.misc.TestGetChild(
                                            metaFactory, spec, type, childType));
                        }
                        if (childType.getAdapter(SOAPElementTypeAdapter.class).getSetter()
                                != null) {
                            addTest(
                                    new org.apache.axiom.ts.soap.misc.TestSetChild(
                                            metaFactory, spec, type, childType));
                            if (childType.getQName(spec.getAltSpec()) != null) {
                                addTest(
                                        new org.apache.axiom.ts.soap.misc
                                                .TestSetChildVersionMismatch(
                                                metaFactory, spec, type, childType));
                            }
                        }
                    }
                }
            }
        }
        addTest(
                new org.apache.axiom.ts.soap.xpath.TestXPathAppliedToSOAPEnvelope(
                        metaFactory, spec, true));
        addTest(
                new org.apache.axiom.ts.soap.xpath.TestXPathAppliedToSOAPEnvelope(
                        metaFactory, spec, false));
    }

    @Override
    protected void addTests() {
        addTests(SOAPSpec.SOAP11);
        addTests(SOAPSpec.SOAP12);
        for (int i = 0; i < badSOAPFiles.length; i++) {
            addTest(
                    new org.apache.axiom.ts.soap.builder.BadInputTest(
                            metaFactory, badSOAPFiles[i]));
        }
        for (SOAPSample msg : goodSOAPFiles) {
            addTest(new org.apache.axiom.ts.soap.builder.MessageTest(metaFactory, msg));
            addTest(
                    new org.apache.axiom.ts.soap.builder.TestRegisterCustomBuilderForPayload(
                            metaFactory, msg));
            addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, msg));
            for (ExpansionStrategy expansionStrategy : getInstances(ExpansionStrategy.class)) {
                for (SerializationStrategy serializationStrategy :
                        getInstances(SerializationStrategy.class)) {
                    addTest(
                            new org.apache.axiom.ts.soap.envelope.TestSerialize(
                                    metaFactory, msg, expansionStrategy, serializationStrategy));
                    addTest(
                            new org.apache.axiom.ts.soap.message.TestSerialize(
                                    metaFactory, msg, expansionStrategy, serializationStrategy));
                }
            }
        }
        addTest(new org.apache.axiom.ts.soap11.builder.TestBuilder(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.fault.TestGetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.fault.TestSetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultcode.TestGetValue(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultcode.TestGetValueWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultcode.TestSetValueFromQName(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultreason.TestAddSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultreason.TestGetFirstSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultreason.TestGetTextWithCDATA(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.builder.TestBuilder(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.envelope.TestBuildWithAttachments(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.envelope.TestMTOMForwardStreaming(
                        metaFactory, true));
        addTest(
                new org.apache.axiom.ts.soap12.envelope.TestMTOMForwardStreaming(
                        metaFactory, false));
        addTest(new org.apache.axiom.ts.soap12.factory.TestCreateSOAPFaultSubCode(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestGetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestGetNodeWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestMoreChildrenAddition(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestSetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestGetSubCodeWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestGetValueWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestSetValueFromQName(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultcode.TestSetValueFromQNameWithExistingValue(
                        metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestAddSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestAddSOAPTextMultiple(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultreason.TestAddSOAPTextWithSOAPVersionMismatch(
                        metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultreason.TestGetAllSoapTextsWithParser(
                        metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestGetFaultReasonText(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultreason.TestGetFaultReasonTextCaseSensitivity(
                        metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultreason
                        .TestGetFaultReasonTextWithoutLangAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestGetFirstSOAPText(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultreason.TestGetFirstSOAPTextWithParser(
                        metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultsubcode.TestGetSubCodeNestedWithParser(
                        metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultsubcode.TestGetSubCodeWithParser(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultsubcode.TestGetValueNestedWithParser(
                        metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.faultsubcode.TestGetValueAsQNameWithParser(
                        metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultsubcode.TestGetValueWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faulttext.TestGetLangWithParser(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.header.TestExamineMustUnderstandHeaderBlocks(
                        metaFactory));
        addTest(new org.apache.axiom.ts.soap12.headerblock.TestGetRelayWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.mtom.TestBuilderDetach(metaFactory));
        addTest(
                new org.apache.axiom.ts.soap12.mtom.TestGetXMLStreamReaderMTOMEncoded(
                        metaFactory, true));
        addTest(
                new org.apache.axiom.ts.soap12.mtom.TestGetXMLStreamReaderMTOMEncoded(
                        metaFactory, false));
    }
}
