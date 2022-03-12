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

import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.jaxp.stax.StAXImplementation;
import org.apache.axiom.ts.xml.StreamType;

public class DialectTestSuiteBuilder extends MatrixTestSuiteBuilder {
    @Override
    protected void addTests() {
        for (StAXImplementation impl : Multiton.getInstances(StAXImplementation.class)) {
            addTests(impl.getAdapter(StAXImplementationAdapter.class));
        }
    }

    private void addTests(StAXImplementationAdapter staxImpl) {
        for (StreamType streamType : Multiton.getInstances(StreamType.class)) {
            addTest(new TestClose(staxImpl, streamType));
        }
        addTest(new TestCreateXMLEventWriterWithNullEncoding(staxImpl));
        addTest(new TestCreateXMLStreamReaderThreadSafety(staxImpl));
        addTest(new TestCreateXMLStreamWriterThreadSafety(staxImpl));
        addTest(new TestCreateXMLStreamWriterWithNullEncoding(staxImpl));
        addTest(new TestDisallowDoctypeDeclWithDenialOfService(staxImpl));
        addTest(new TestDisallowDoctypeDeclWithExternalSubset(staxImpl));
        addTest(new TestDisallowDoctypeDeclWithInternalSubset(staxImpl));
        addTest(new TestDTDReader(staxImpl));
        addTest(new TestEnableCDataReporting(staxImpl));
        addTest(new TestGetAttributeNamespaceWithNoPrefix(staxImpl));
        addTest(new TestGetCharacterEncodingScheme(staxImpl));
        addTest(new TestGetEncodingExternal(staxImpl));
        addTest(new TestGetEncodingFromDetection(staxImpl, "UTF-8", "UTF-8"));
        // The case of UTF-16 with a byte order marker is not well defined:
        // * One may argue that the result should be UTF-16BE or UTF-16LE because
        //   otherwise the information about the byte order is lost.
        // * On the other hand, one may argue that the result should be UTF-16
        //   because UTF-16BE or UTF-16LE may be interpreted as an indication that
        //   there should be no BOM.
        // Therefore we accept both results.
        addTest(new TestGetEncodingFromDetection(staxImpl, "UnicodeBig", "UTF-16", "UTF-16BE"));
        addTest(new TestGetEncodingFromDetection(staxImpl, "UnicodeLittle", "UTF-16", "UTF-16LE"));
        // Here there is no doubt; if the encoding is UTF-16 without BOM, then the
        // parser should report the detected byte order.
        addTest(new TestGetEncodingFromDetection(staxImpl, "UnicodeBigUnmarked", "UTF-16BE"));
        addTest(new TestGetEncodingFromDetection(staxImpl, "UnicodeLittleUnmarked", "UTF-16LE"));
        addTest(new TestGetEncoding(staxImpl));
        addTest(new TestGetEncodingWithCharacterStream(staxImpl));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, false));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new TestGetLocalNameIllegalStateException(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new TestGetNameIllegalStateException(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new TestGetNamespaceContextImplicitNamespaces(staxImpl));
        addTest(new TestGetNamespacePrefixDefaultNamespace(staxImpl));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new TestGetNamespaceURIIllegalStateException(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new TestGetNamespaceURIWithNullNamespace(staxImpl));
        addTest(new TestGetPrefixAfterWriteDefaultNamespace(staxImpl));
        addTest(new TestGetPrefixAfterWriteNamespace(staxImpl));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.START_DOCUMENT, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.END_DOCUMENT, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.DTD, true));
        addTest(new TestGetPrefixIllegalStateException(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new TestGetTextInProlog(staxImpl));
        addTest(new TestGetVersion(staxImpl));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.START_ELEMENT, true));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.END_ELEMENT, true));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.CHARACTERS, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.COMMENT, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.SPACE, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.START_DOCUMENT, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.END_DOCUMENT, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.DTD, false));
        addTest(new TestHasName(staxImpl, XMLStreamConstants.CDATA, false));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, false));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.COMMENT, true));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.SPACE, true));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.START_DOCUMENT, false));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.END_DOCUMENT, false));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, true));
        addTest(new TestHasText(staxImpl, XMLStreamConstants.DTD, true));
        // Note: CDATA events are actually not mentioned in the Javadoc of XMLStreamReader#hasText().
        //       This is because reporting CDATA sections as CDATA events is an implementation
        //       specific feature. Nevertheless, for obvious reasons, we expect hasText to
        //       return true in this case.
        addTest(new TestHasText(staxImpl, XMLStreamConstants.CDATA, true));
        addTest(new TestIsCharactersOnCDATASection(staxImpl));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.START_ELEMENT, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.END_ELEMENT, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.PROCESSING_INSTRUCTION, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.CHARACTERS, true));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.COMMENT, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.SPACE, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.START_DOCUMENT, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.END_DOCUMENT, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.ENTITY_REFERENCE, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.DTD, false));
        addTest(new TestIsCharacters(staxImpl, XMLStreamConstants.CDATA, false));
        addTest(new TestIsStandalone(staxImpl));
        addTest(new TestNextAfterEndDocument(staxImpl));
        addTest(new TestSetPrefixScope(staxImpl));
        addTest(new TestStandaloneSet(staxImpl));
        addTest(new TestWriteStartDocumentWithNullEncoding(staxImpl));
    }
}
