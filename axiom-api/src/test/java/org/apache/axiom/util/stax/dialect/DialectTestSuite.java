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

import java.util.Set;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamConstants;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.MatrixTestFilters;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.ts.jaxp.stax.StAXImplementation;
import org.apache.axiom.ts.xml.StreamType;
import org.apache.axiom.util.xml.stream.XMLEventUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class DialectTestSuite {
    @TestFactory
    public Stream<DynamicNode> tests() {
        MatrixTestNode root =
                new FanOutNode<>(
                        Multiton.getInstances(StAXImplementation.class),
                        (binder, value) ->
                                binder.bind(StAXImplementationAdapter.class)
                                        .toInstance(
                                                value.getAdapter(StAXImplementationAdapter.class)),
                        (injector, value, params) ->
                                params.addTestParameter("implementation", value.getName()),
                        new ParentNode(
                                new FanOutNode<>(
                                        Multiton.getInstances(StreamType.class),
                                        (binder, value) ->
                                                binder.bind(StreamType.class).toInstance(value),
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "type", value.getType().getSimpleName()),
                                        new MatrixTest(TestClose.class)),
                                new MatrixTest(TestCreateXMLEventWriterWithNullEncoding.class),
                                new MatrixTest(TestCreateXMLStreamReaderThreadSafety.class),
                                new MatrixTest(TestCreateXMLStreamWriterThreadSafety.class),
                                new MatrixTest(TestCreateXMLStreamWriterWithNullEncoding.class),
                                new MatrixTest(TestDisallowDoctypeDeclWithDenialOfService.class),
                                new MatrixTest(TestDisallowDoctypeDeclWithExternalSubset.class),
                                new MatrixTest(TestDisallowDoctypeDeclWithInternalSubset.class),
                                new MatrixTest(TestDTDReader.class),
                                new MatrixTest(TestEnableCDataReporting.class),
                                new MatrixTest(TestGetAttributeNamespaceWithNoPrefix.class),
                                new MatrixTest(TestGetCharacterEncodingScheme.class),
                                new MatrixTest(TestGetEncodingExternal.class),
                                new FanOutNode<>(
                                        ImmutableList.of(
                                                Pair.of("UTF-8", Set.of("UTF-8")),
                                                // The case of UTF-16 with a byte order marker is
                                                // not well defined:
                                                // * One may argue that the result should be
                                                //   UTF-16BE or UTF-16LE because otherwise the
                                                //   information about the byte order is lost.
                                                // * On the other hand, one may argue that the
                                                //   result should be UTF-16 because UTF-16BE or
                                                //   UTF-16LE may be interpreted as an indication
                                                //   that there should be no BOM.
                                                // Therefore we accept both results.
                                                Pair.of("UnicodeBig", Set.of("UTF-16", "UTF-16BE")),
                                                Pair.of(
                                                        "UnicodeLittle",
                                                        Set.of("UTF-16", "UTF-16LE")),
                                                // Here there is no doubt; if the encoding is UTF-16
                                                // without BOM, then the parser should report the
                                                // detected byte order.
                                                Pair.of("UnicodeBigUnmarked", Set.of("UTF-16BE")),
                                                Pair.of(
                                                        "UnicodeLittleUnmarked",
                                                        Set.of("UTF-16LE"))),
                                        (binder, value) -> {
                                            binder.bind(String.class)
                                                    .annotatedWith(Names.named("javaEncoding"))
                                                    .toInstance(value.getLeft());
                                            binder.bind(new TypeLiteral<Set<String>>() {})
                                                    .annotatedWith(Names.named("xmlEncodings"))
                                                    .toInstance(value.getRight());
                                        },
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "javaEncoding", value.getLeft()),
                                        new MatrixTest(TestGetEncodingFromDetection.class)),
                                new MatrixTest(TestGetEncoding.class),
                                new MatrixTest(TestGetEncodingWithCharacterStream.class),
                                new FanOutNode<>(
                                        ImmutableList.of(
                                                Pair.of(XMLStreamConstants.START_ELEMENT, false),
                                                Pair.of(XMLStreamConstants.END_ELEMENT, false),
                                                Pair.of(
                                                        XMLStreamConstants.PROCESSING_INSTRUCTION,
                                                        true),
                                                Pair.of(XMLStreamConstants.CHARACTERS, true),
                                                Pair.of(XMLStreamConstants.COMMENT, true),
                                                Pair.of(XMLStreamConstants.SPACE, true),
                                                Pair.of(XMLStreamConstants.START_DOCUMENT, true),
                                                Pair.of(XMLStreamConstants.END_DOCUMENT, true),
                                                Pair.of(XMLStreamConstants.ENTITY_REFERENCE, false),
                                                Pair.of(XMLStreamConstants.DTD, true),
                                                Pair.of(XMLStreamConstants.CDATA, true)),
                                        (binder, value) -> {
                                            binder.bind(Integer.class)
                                                    .annotatedWith(Names.named("event"))
                                                    .toInstance(value.getLeft());
                                            binder.bind(Boolean.class)
                                                    .annotatedWith(Names.named("expectException"))
                                                    .toInstance(value.getRight());
                                        },
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "event",
                                                        XMLEventUtils.getEventTypeString(
                                                                value.getLeft())),
                                        new MatrixTest(
                                                TestGetLocalNameIllegalStateException.class)),
                                new FanOutNode<>(
                                        ImmutableList.of(
                                                Pair.of(XMLStreamConstants.START_ELEMENT, false),
                                                Pair.of(XMLStreamConstants.END_ELEMENT, false),
                                                Pair.of(
                                                        XMLStreamConstants.PROCESSING_INSTRUCTION,
                                                        true),
                                                Pair.of(XMLStreamConstants.CHARACTERS, true),
                                                Pair.of(XMLStreamConstants.COMMENT, true),
                                                Pair.of(XMLStreamConstants.SPACE, true),
                                                Pair.of(XMLStreamConstants.START_DOCUMENT, true),
                                                Pair.of(XMLStreamConstants.END_DOCUMENT, true),
                                                Pair.of(XMLStreamConstants.ENTITY_REFERENCE, true),
                                                Pair.of(XMLStreamConstants.DTD, true),
                                                Pair.of(XMLStreamConstants.CDATA, true)),
                                        (binder, value) -> {
                                            binder.bind(Integer.class)
                                                    .annotatedWith(Names.named("event"))
                                                    .toInstance(value.getLeft());
                                            binder.bind(Boolean.class)
                                                    .annotatedWith(Names.named("expectException"))
                                                    .toInstance(value.getRight());
                                        },
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "event",
                                                        XMLEventUtils.getEventTypeString(
                                                                value.getLeft())),
                                        new ParentNode(
                                                new MatrixTest(
                                                        TestGetNameIllegalStateException.class),
                                                new MatrixTest(
                                                        TestGetNamespaceURIIllegalStateException
                                                                .class),
                                                new MatrixTest(
                                                        TestGetPrefixIllegalStateException.class))),
                                new MatrixTest(TestGetNamespaceContextImplicitNamespaces.class),
                                new MatrixTest(TestGetNamespacePrefixDefaultNamespace.class),
                                new MatrixTest(TestGetNamespaceURIWithNullNamespace.class),
                                new MatrixTest(TestGetPrefixAfterWriteDefaultNamespace.class),
                                new MatrixTest(TestGetPrefixAfterWriteNamespace.class),
                                new MatrixTest(TestGetTextInProlog.class),
                                new MatrixTest(TestGetVersion.class),
                                new FanOutNode<>(
                                        ImmutableList.of(
                                                Pair.of(XMLStreamConstants.START_ELEMENT, true),
                                                Pair.of(XMLStreamConstants.END_ELEMENT, true),
                                                Pair.of(
                                                        XMLStreamConstants.PROCESSING_INSTRUCTION,
                                                        false),
                                                Pair.of(XMLStreamConstants.CHARACTERS, false),
                                                Pair.of(XMLStreamConstants.COMMENT, false),
                                                Pair.of(XMLStreamConstants.SPACE, false),
                                                Pair.of(XMLStreamConstants.START_DOCUMENT, false),
                                                Pair.of(XMLStreamConstants.END_DOCUMENT, false),
                                                Pair.of(XMLStreamConstants.ENTITY_REFERENCE, false),
                                                Pair.of(XMLStreamConstants.DTD, false),
                                                Pair.of(XMLStreamConstants.CDATA, false)),
                                        (binder, value) -> {
                                            binder.bind(Integer.class)
                                                    .annotatedWith(Names.named("event"))
                                                    .toInstance(value.getLeft());
                                            binder.bind(Boolean.class)
                                                    .annotatedWith(Names.named("expected"))
                                                    .toInstance(value.getRight());
                                        },
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "event",
                                                        XMLEventUtils.getEventTypeString(
                                                                value.getLeft())),
                                        new MatrixTest(TestHasName.class)),
                                new FanOutNode<>(
                                        ImmutableList.of(
                                                Pair.of(XMLStreamConstants.START_ELEMENT, false),
                                                Pair.of(XMLStreamConstants.END_ELEMENT, false),
                                                Pair.of(
                                                        XMLStreamConstants.PROCESSING_INSTRUCTION,
                                                        false),
                                                Pair.of(XMLStreamConstants.CHARACTERS, true),
                                                Pair.of(XMLStreamConstants.COMMENT, true),
                                                Pair.of(XMLStreamConstants.SPACE, true),
                                                Pair.of(XMLStreamConstants.START_DOCUMENT, false),
                                                Pair.of(XMLStreamConstants.END_DOCUMENT, false),
                                                Pair.of(XMLStreamConstants.ENTITY_REFERENCE, true),
                                                Pair.of(XMLStreamConstants.DTD, true),
                                                // Note: CDATA events are actually not mentioned in
                                                // the Javadoc of XMLStreamReader#hasText(). This is
                                                // because reporting CDATA sections as CDATA events
                                                // is an implementation specific feature.
                                                // Nevertheless, for obvious reasons, we expect
                                                // hasText to return true in this case.
                                                Pair.of(XMLStreamConstants.CDATA, true)),
                                        (binder, value) -> {
                                            binder.bind(Integer.class)
                                                    .annotatedWith(Names.named("event"))
                                                    .toInstance(value.getLeft());
                                            binder.bind(Boolean.class)
                                                    .annotatedWith(Names.named("expected"))
                                                    .toInstance(value.getRight());
                                        },
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "event",
                                                        XMLEventUtils.getEventTypeString(
                                                                value.getLeft())),
                                        new MatrixTest(TestHasText.class)),
                                new MatrixTest(TestIsCharactersOnCDATASection.class),
                                new FanOutNode<>(
                                        ImmutableList.of(
                                                Pair.of(XMLStreamConstants.START_ELEMENT, false),
                                                Pair.of(XMLStreamConstants.END_ELEMENT, false),
                                                Pair.of(
                                                        XMLStreamConstants.PROCESSING_INSTRUCTION,
                                                        false),
                                                Pair.of(XMLStreamConstants.CHARACTERS, true),
                                                Pair.of(XMLStreamConstants.COMMENT, false),
                                                Pair.of(XMLStreamConstants.SPACE, false),
                                                Pair.of(XMLStreamConstants.START_DOCUMENT, false),
                                                Pair.of(XMLStreamConstants.END_DOCUMENT, false),
                                                Pair.of(XMLStreamConstants.ENTITY_REFERENCE, false),
                                                Pair.of(XMLStreamConstants.DTD, false),
                                                Pair.of(XMLStreamConstants.CDATA, false)),
                                        (binder, value) -> {
                                            binder.bind(Integer.class)
                                                    .annotatedWith(Names.named("event"))
                                                    .toInstance(value.getLeft());
                                            binder.bind(Boolean.class)
                                                    .annotatedWith(Names.named("expected"))
                                                    .toInstance(value.getRight());
                                        },
                                        (injector, value, params) ->
                                                params.addTestParameter(
                                                        "event",
                                                        XMLEventUtils.getEventTypeString(
                                                                value.getLeft())),
                                        new MatrixTest(TestIsCharacters.class)),
                                new MatrixTest(TestIsStandalone.class),
                                new MatrixTest(TestNextAfterEndDocument.class),
                                new MatrixTest(TestSetPrefixScope.class),
                                new MatrixTest(TestStandaloneSet.class),
                                new MatrixTest(TestWriteStartDocumentWithNullEncoding.class)));

        return root.toDynamicNodes(
                MatrixTestFilters.builder()
                        // Neither SJSXP nor XLXP report whitespace in prolog
                        .add(TestGetTextInProlog.class, "(implementation=JRE)")
                        // SJSXP and XLXP don't report whitespace in prolog
                        .add(
                                TestGetTextInProlog.class,
                                "(|(implementation=sjsxp-*)(implementation=com.ibm.ws.prereq.xlxp.jar)(implementation=xml.jar))")
                        // DTDReader is not supported for all StAX implementations
                        .add(
                                TestDTDReader.class,
                                "(|(implementation=stax-1.2.0.jar)(implementation=wstx-asl-3.*))")
                        // TODO: investigate why this fails; didn't occur with the old
                        // TestCloseInputStream test
                        .add(
                                TestClose.class,
                                "(&(implementation=stax-1.2.0.jar)(type=InputStream))")
                        .build());
    }
}
