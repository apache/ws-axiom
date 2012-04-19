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
package org.apache.axiom.util.stax.dialect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.testutils.suite.TestSuiteBuilder;

public class DialectTestSuiteBuilder extends TestSuiteBuilder {
    private final List/*<StAXImplementation>*/ implementations = new ArrayList();
    
    public void addImplementation(StAXImplementation implementation) {
        implementations.add(implementation);
    }
    
    protected void addTests() {
        for (Iterator it = implementations.iterator(); it.hasNext(); ) {
            addTests((StAXImplementation)it.next());
        }
    }

    private void addTests(StAXImplementation staxImpl) {
        String[] conformanceTestFiles = AbstractTestCase.getConformanceTestFiles();
        addTest(new CreateXMLEventWriterWithNullEncodingTestCase(staxImpl));
        addTest(new CreateXMLStreamReaderThreadSafetyTestCase(staxImpl));
        addTest(new CreateXMLStreamWriterThreadSafetyTestCase(staxImpl));
        addTest(new CreateXMLStreamWriterWithNullEncodingTestCase(staxImpl));
        addTest(new DisallowDoctypeDeclWithDenialOfServiceTestCase(staxImpl));
        addTest(new DisallowDoctypeDeclWithExternalSubsetTestCase(staxImpl));
        addTest(new DisallowDoctypeDeclWithInternalSubsetTestCase(staxImpl));
        addTest(new EnableCDataReportingTestCase(staxImpl));
        addTest(new GetAttributeNamespaceWithNoPrefixTestCase(staxImpl));
        addTest(new GetCharacterEncodingSchemeTestCase(staxImpl));
        addTest(new GetEncodingExternalTestCase(staxImpl));
        addTest(new GetEncodingFromDetectionTestCase(staxImpl, "UTF-8", "UTF-8"));
        // The case of UTF-16 with a byte order marker is not well defined:
        // * One may argue that the result should be UTF-16BE or UTF-16LE because
        //   otherwise the information about the byte order is lost.
        // * On the other hand, one may argue that the result should be UTF-16
        //   because UTF-16BE or UTF-16LE may be interpreted as an indication that
        //   there should be no BOM.
        // Therefore we accept both results.
        addTest(new GetEncodingFromDetectionTestCase(staxImpl, "UnicodeBig", new String[] { "UTF-16", "UTF-16BE" } ));
        addTest(new GetEncodingFromDetectionTestCase(staxImpl, "UnicodeLittle", new String[] { "UTF-16", "UTF-16LE" }));
        // Here there is no doubt; if the encoding is UTF-16 without BOM, then the
        // parser should report the detected byte order.
        addTest(new GetEncodingFromDetectionTestCase(staxImpl, "UnicodeBigUnmarked", "UTF-16BE"));
        addTest(new GetEncodingFromDetectionTestCase(staxImpl, "UnicodeLittleUnmarked", "UTF-16LE"));
        addTest(new GetEncodingTestCase(staxImpl));
        addTest(new GetEncodingWithCharacterStreamTestCase(staxImpl));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, false));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new GetLocalNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new GetNameIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new GetNamespaceContextImplicitNamespacesTestCase(staxImpl));
        for (int i=0; i<conformanceTestFiles.length; i++) {
            addTest(new GetNamespaceContextTestCase(staxImpl, conformanceTestFiles[i]));
        }
        addTest(new GetNamespacePrefixDefaultNamespaceTestCase(staxImpl));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new GetNamespaceURIIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new GetNamespaceURIWithNullNamespaceTestCase(staxImpl));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new GetPrefixIllegalStateExceptionTestCase(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new GetPrefixWithNoPrefixTestCase(staxImpl));
        addTest(new GetTextInPrologTestCase(staxImpl));
        addTest(new GetVersionTestCase(staxImpl));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, true));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, true));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.CHARACTERS, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.COMMENT, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.SPACE, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.DTD, false));
        addTest(new HasNameTestCase(staxImpl, XMLStreamConstants.CDATA, false));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, false));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, false));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, false));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.DTD, true));
        // Note: CDATA events are actually not mentioned in the Javadoc of XMLStreamReader#hasText().
        //       This is because reporting CDATA sections as CDATA events is an implementation
        //       specific feature. Nevertheless, for obvious reasons, we expect hasText to
        //       return true in this case.
        addTest(new HasTextTestCase(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new IsCharactersOnCDATASectionTestCase(staxImpl));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.COMMENT, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.SPACE, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.START_DOCUMENT, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.END_DOCUMENT, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.DTD, false));
        addTest(new IsCharactersTestCase(staxImpl, XMLStreamConstants.CDATA, false));
        addTest(new IsStandaloneTestCase(staxImpl));
        addTest(new NextAfterEndDocumentTestCase(staxImpl));
        addTest(new SetPrefixScopeTestCase(staxImpl));
        addTest(new StandaloneSetTestCase(staxImpl));
        addTest(new UnwrapTestCase(staxImpl));
        addTest(new WriteStartDocumentWithNullEncodingTestCase(staxImpl));
    }
}
