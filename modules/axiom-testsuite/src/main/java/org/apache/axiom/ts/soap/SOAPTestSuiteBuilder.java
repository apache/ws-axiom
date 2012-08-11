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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.testutils.suite.TestSuiteBuilder;

public class SOAPTestSuiteBuilder extends TestSuiteBuilder {
    private static final String[] badSOAPFiles = { "wrongSoapNs.xml", "notnamespaceQualified.xml", "soap11/twoheaders.xml", "soap11/twoBodymessage.xml",
            "soap11/envelopeMissing.xml", "soap11/haederBodyWrongOrder.xml", "soap11/invalid-faultcode.xml", "soap11/invalid-faultstring.xml",
            "soap11/invalid-faultactor.xml", "soap11/processing-instruction.xml", "soap11/entity-reference.xml",
            "soap12/header-bad-case.xml", "soap12/header-no-namespace.xml", "soap12/processing-instruction.xml", "soap12/entity-reference.xml",
            "soap12/additional-element-after-body.xml"};
    
    private static final String[] goodSOAPFiles = { TestConstants.WHITESPACE_MESSAGE,
        TestConstants.MINIMAL_MESSAGE, TestConstants.REALLY_BIG_MESSAGE,
        TestConstants.EMPTY_BODY_MESSAGE, "soap/soap11/soapfault.xml", "soap/soap11/bodyNotQualified.xml",
        "soap/soap11/faultstring-with-comment.xml", "soap/soap11/additional-element-after-body.xml"};
    
    private static final QName[] generalQNames = {
        new QName("root"),
        new QName("urn:test", "root", "p"),
        new QName("urn:test", "root") };
    
    private static final QName[] noFaultQNames = {
        new QName("root"),
        new QName("urn:test", "root", "p"),
        new QName("urn:test", "root"),
        new QName("Fault"),
        new QName("urn:test", "Fault", "p"),
        new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI, "NoFault", SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX),
        new QName(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, "NoFault", SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX) };
    
    private final OMMetaFactory metaFactory;
    private final boolean supportsOMSourcedElement;
    private final boolean supportsBodyElementNameOptimization;
    
    /**
     * Constructor.
     * 
     * @param metaFactory
     * @param supportsOMSourcedElement
     *            indicates whether the implementation supports {@link OMSourcedElement}
     * @param supportsBodyElementNameOptimization
     *            indicates whether the implementation supports the optimization described by <a
     *            href="https://issues.apache.org/jira/browse/AXIOM-282">AXIOM-282</a>
     */
    public SOAPTestSuiteBuilder(OMMetaFactory metaFactory, boolean supportsOMSourcedElement, boolean supportsBodyElementNameOptimization) {
        this.metaFactory = metaFactory;
        this.supportsOMSourcedElement = supportsOMSourcedElement;
        this.supportsBodyElementNameOptimization = supportsBodyElementNameOptimization;
    }
    
    private void addTests(SOAPSpec spec) {
        addTest(new org.apache.axiom.ts.soap.body.TestAddFault1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestAddFault2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFaultFakeFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFaultWithParser(metaFactory, spec));
        for (int i=0; i<generalQNames.length; i++) {
            QName qname = generalQNames[i];
            addTest(new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameWithParser(metaFactory, spec,
                    qname, supportsBodyElementNameOptimization));
            addTest(new org.apache.axiom.ts.soap.body.TestGetFirstElementNSWithParser(metaFactory, spec,
                    qname, supportsBodyElementNameOptimization));
        }
        for (int i=0; i<noFaultQNames.length; i++) {
            QName qname = noFaultQNames[i];
            addTest(new org.apache.axiom.ts.soap.body.TestGetFaultNoFault(metaFactory, spec, qname));
            addTest(new org.apache.axiom.ts.soap.body.TestGetFaultWithParserNoFault(metaFactory, spec, qname));
            addTest(new org.apache.axiom.ts.soap.body.TestHasFaultNoFault(metaFactory, spec, qname));
            addTest(new org.apache.axiom.ts.soap.body.TestHasFaultWithParserNoFault(metaFactory, spec,
                    qname, supportsBodyElementNameOptimization));
        }
        addTest(new org.apache.axiom.ts.soap.body.TestGetFirstElementLocalNameEmptyBody(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestGetFirstElementNSEmptyBody(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultAfterReplace(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultFakeFault(metaFactory, spec));
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.soap.body.TestHasFaultWithOMSEUnknownName(metaFactory, spec));
        }
        addTest(new org.apache.axiom.ts.soap.body.TestHasFaultWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.builder.TestCommentInEpilog(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.builder.TestCommentInProlog(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.builder.TestDTD(metaFactory, spec));
        if (supportsBodyElementNameOptimization) {
            addTest(new org.apache.axiom.ts.soap.builder.TestRegisterCustomBuilderForPayloadAfterSOAPFaultCheck(metaFactory, spec));
        }
        addTest(new org.apache.axiom.ts.soap.envelope.TestAddHeaderToIncompleteEnvelope(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestBodyHeaderOrder(metaFactory, spec));
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.soap.envelope.TestCloneWithSourcedElement1(metaFactory, spec));
            addTest(new org.apache.axiom.ts.soap.envelope.TestCloneWithSourcedElement2(metaFactory, spec));
        }
        addTest(new org.apache.axiom.ts.soap.envelope.TestDetach(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBody(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyOnEmptyEnvelope(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyOnEnvelopeWithHeaderOnly(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetBodyWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetHeader(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetHeaderWithParser(metaFactory, spec));
        for (int i=0; i<generalQNames.length; i++) {
            QName qname = generalQNames[i];
            addTest(new org.apache.axiom.ts.soap.envelope.TestGetSOAPBodyFirstElementLocalNameAndNS(metaFactory, spec, qname));
            addTest(new org.apache.axiom.ts.soap.envelope.TestGetSOAPBodyFirstElementLocalNameAndNSWithParser(metaFactory, spec, qname));
        }
        addTest(new org.apache.axiom.ts.soap.envelope.TestGetXMLStreamReaderWithoutCachingWithPartiallyBuiltHeaderBlock(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestHasFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestHasFaultWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.envelope.TestHasFaultWithParserOptimized(metaFactory, spec));
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.soap.envelope.TestSerializeAndConsumeWithOMSEInBody(metaFactory, spec));
        }
        addTest(new org.apache.axiom.ts.soap.factory.TestCreateSOAPEnvelope(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestCreateSOAPEnvelopeWithCustomPrefix(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestCreateSOAPFault(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestCreateSOAPFaultDetail(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestFactoryIsSingleton(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetDefaultEnvelope(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetDefaultFaultEnvelope(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetMetaFactory(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.factory.TestGetNamespace(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetCode(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetCodeWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetDetail(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetDetailWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetException(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetReason(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetReasonWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetRole(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestGetRoleWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestSetCode(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestSetDetail(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestSetException(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestSetReason(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestSetRole(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestWrongParent1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestWrongParent2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.fault.TestWrongParent3(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestAddDetailEntry(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestDetailEntriesUsingDefaultNamespaceWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntries(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestGetAllDetailEntriesWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestSerialization(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faultdetail.TestWSCommons202(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.faulttext.TestSetLang(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestAddHeaderBlock(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestAddHeaderBlockWithoutNamespace1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestAddHeaderBlockWithoutNamespace2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestDiscardIncomplete(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestDiscardPartiallyBuilt(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExamineAllHeaderBlocks(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExamineAllHeaderBlocksWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExamineHeaderBlocks(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestExtractAllHeaderBlocks(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestGetHeaderBlocksWithNSURI(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.header.TestGetHeadersToProcessWithNamespace(metaFactory, spec));
        if (supportsOMSourcedElement) {
            addTest(new org.apache.axiom.ts.soap.headerblock.TestByteArrayDS(metaFactory, spec));
        }
        addTest(new org.apache.axiom.ts.soap.headerblock.TestGetMustUnderstand(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestGetRole(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestGetVersion(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestSetMustUnderstandBoolean(metaFactory, spec, true, spec == SOAPSpec.SOAP11 ? "1" : "true"));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestSetMustUnderstandBoolean(metaFactory, spec, false, spec == SOAPSpec.SOAP11 ? "0" : "false"));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestSetMustUnderstandString01(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestSetMustUnderstandWithInvalidValue(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestSetRole(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestWrongParent1(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestWrongParent2(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.headerblock.TestWrongParent3(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.message.TestClone(metaFactory, spec, true));
        addTest(new org.apache.axiom.ts.soap.message.TestClone(metaFactory, spec, false));
        addTest(new org.apache.axiom.ts.soap.message.TestCloneIncomplete(metaFactory, spec, true));
        addTest(new org.apache.axiom.ts.soap.message.TestCloneIncomplete(metaFactory, spec, false));
        addTest(new org.apache.axiom.ts.soap.message.TestGetCharsetEncodingWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.message.TestGetOMFactoryWithParser(metaFactory, spec));
        addTest(new org.apache.axiom.ts.soap.xpath.TestXPathAppliedToSOAPEnvelope(metaFactory, spec, true));
        addTest(new org.apache.axiom.ts.soap.xpath.TestXPathAppliedToSOAPEnvelope(metaFactory, spec, false));
    }
    
    protected void addTests() {
        addTests(SOAPSpec.SOAP11);
        addTests(SOAPSpec.SOAP12);
        for (int i=0; i<badSOAPFiles.length; i++) {
            addTest(new org.apache.axiom.ts.soap.builder.BadInputTest(metaFactory, badSOAPFiles[i]));
        }
        for (int i=0; i<goodSOAPFiles.length; i++) {
            addTest(new org.apache.axiom.ts.soap.builder.MessageTest(metaFactory, goodSOAPFiles[i]));
        }
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "sample1.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "soapmessage.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "soapmessage1.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "whitespacedMessage.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "minimalMessage.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "reallyReallyBigMessage.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "emtyBodymessage.xml"));
        addTest(new org.apache.axiom.ts.soap.envelope.TestClone(metaFactory, SOAPSpec.SOAP11, "soap11fault.xml")); 
        addTest(new org.apache.axiom.ts.soap11.builder.TestBuilder(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.envelope.TestAddElementAfterBody(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.fault.TestGetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.fault.TestSetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultcode.TestSetValueFromQName(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultreason.TestAddSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultreason.TestGetFirstSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.faultreason.TestGetTextWithCDATA(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.header.TestExamineAllHeaderBlocksWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.header.TestExamineHeaderBlocksWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.header.TestExamineMustUnderstandHeaderBlocksWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.header.TestGetHeaderBlocksWithNSURIWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap11.header.TestGetHeadersToProcessWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.builder.TestBuilder(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.envelope.TestAddElementAfterBody(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.envelope.TestBuildWithAttachments(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.envelope.TestMTOMForwardStreaming(metaFactory, true));
        addTest(new org.apache.axiom.ts.soap12.envelope.TestMTOMForwardStreaming(metaFactory, false));
        addTest(new org.apache.axiom.ts.soap12.factory.TestCreateSOAPFaultSubCode(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestGetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestGetNodeWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestMoreChildrenAddition(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.fault.TestSetNode(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestGetSubCodeWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestGetTextAsQNameWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestGetValueWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestSetValueFromQName(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultcode.TestSetValueFromQNameWithExistingValue(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestAddSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestAddSOAPTextWithSOAPVersionMismatch(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestGetFirstSOAPText(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faultreason.TestGetFirstSOAPTextWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.faulttext.TestGetLangWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.header.TestExamineAllHeaderBlocksWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.header.TestExamineHeaderBlocksWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.header.TestExamineMustUnderstandHeaderBlocks(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.header.TestExamineMustUnderstandHeaderBlocksWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.header.TestGetHeaderBlocksWithNSURIWithParser(metaFactory));
        addTest(new org.apache.axiom.ts.soap12.header.TestGetHeadersToProcessWithParser(metaFactory));
    }
}
