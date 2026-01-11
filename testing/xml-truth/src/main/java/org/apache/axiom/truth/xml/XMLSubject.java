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
package org.apache.axiom.truth.xml;

import static com.google.common.truth.Truth.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.apache.axiom.truth.xml.spi.TraverserException;
import org.apache.axiom.truth.xml.spi.XML;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

/** Propositions for objects representing XML data. */
public final class XMLSubject extends Subject {
    private final XML xml;
    private boolean ignoreComments;
    private boolean ignoreElementContentWhitespace;
    private boolean ignoreWhitespace;
    private boolean ignoreWhitespaceInPrologAndEpilog;
    private boolean ignorePrologAndEpilog;
    private boolean ignoreNamespaceDeclarations;
    private boolean ignoreNamespacePrefixes;
    private boolean ignoreRedundantNamespaceDeclarations;
    private boolean expandEntityReferences;
    private boolean treatWhitespaceAsText;

    XMLSubject(FailureMetadata metadata, Object subject) {
        super(metadata, subject);
        xml = XMLTruth.xml(subject);
    }

    /**
     * Ignore comments; same as {@code ignoringComments(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringComments() {
        return ignoringComments(true);
    }

    /**
     * Specifies if comments should be ignored.
     *
     * @param value {@code true} if comments should be ignored, {@code false} otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringComments(boolean value) {
        ignoreComments = value;
        return this;
    }

    /**
     * Ignore element content whitespace; same as {@code ignoringElementContentWhitespace(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringElementContentWhitespace() {
        return ignoringElementContentWhitespace(true);
    }

    /**
     * Specifies if element content whitespace should be ignored. Note that this only has an effect
     * for documents that have a DTD.
     *
     * @param value {@code true} if element content whitespace should be ignored, {@code false}
     *     otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringElementContentWhitespace(boolean value) {
        ignoreElementContentWhitespace = true;
        return this;
    }

    /**
     * Ignore all whitespace; same as {@code ignoringWhitespace(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringWhitespace() {
        return ignoringWhitespace(true);
    }

    /**
     * Specifies if whitespace should be ignored.
     *
     * @param value {@code true} if all text nodes that contain only whitespace should be ignored,
     *     {@code false} otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringWhitespace(boolean value) {
        ignoreWhitespace = value;
        return this;
    }

    /**
     * Ignore whitespace in the prolog and epilog; same as {@code
     * ignoringWhitespaceInPrologAndEpilog(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringWhitespaceInPrologAndEpilog() {
        return ignoringWhitespaceInPrologAndEpilog(true);
    }

    /**
     * Specifies if whitespace in the prolog and epilog should be ignored. This is especially useful
     * when working with DOM documents because DOM strips whitespace from the prolog and epilog.
     *
     * @param value {@code true} if whitespace in the prolog and epilog should be ignored, {@code
     *     false} otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringWhitespaceInPrologAndEpilog(boolean value) {
        ignoreWhitespaceInPrologAndEpilog = value;
        return this;
    }

    /**
     * Ignore the prolog and epilog entirely; same as {@code ignoringPrologAndEpilog(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringPrologAndEpilog() {
        return ignoringPrologAndEpilog(true);
    }

    /**
     * Specifies if the prolog and epilog should be ignored entirely.
     *
     * @param value {@code true} if (text, comment and document type declaration) nodes in the
     *     prolog and epilog should be ignored, {@code false} otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringPrologAndEpilog(boolean value) {
        ignorePrologAndEpilog = value;
        return this;
    }

    /**
     * Ignore all namespace declarations; same as {@code ignoringNamespaceDeclarations(true)}.
     *
     * @return <code>this</code>
     */
    public XMLSubject ignoringNamespaceDeclarations() {
        return ignoringNamespaceDeclarations(true);
    }

    /**
     * Specifies if namespace declarations should be ignored.
     *
     * @param value {@code true} if namespace declarations should be ignored, {@code false}
     *     otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringNamespaceDeclarations(boolean value) {
        ignoreNamespaceDeclarations = value;
        return this;
    }

    /**
     * Ignore namespace prefixes; same as {@code ignoringNamespacePrefixes(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringNamespacePrefixes() {
        return ignoringNamespacePrefixes(true);
    }

    /**
     * Specifies if namespace prefixes should be ignored.
     *
     * @param value {@code true} if namespace prefixes are ignored when comparing elements and
     *     attributes, {@code false} otherwise
     * @return {@code this}
     */
    public XMLSubject ignoringNamespacePrefixes(boolean value) {
        ignoreNamespacePrefixes = value;
        return this;
    }

    /**
     * Ignore redundant namespace declarations; same as {@code
     * ignoringRedundantNamespaceDeclarations(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject ignoringRedundantNamespaceDeclarations() {
        return ignoringRedundantNamespaceDeclarations(true);
    }

    /**
     * Specify if redundant namespace declarations should be ignored. A namespace declaration is
     * considered redundant if its presence doesn't modify the namespace context.
     *
     * @param value {@code true} if redundant namespace declarations should be ignored, {@code
     *     false} if all namespace declarations should be compared
     * @return {@code this}
     */
    public XMLSubject ignoringRedundantNamespaceDeclarations(boolean value) {
        ignoreRedundantNamespaceDeclarations = value;
        return this;
    }

    /**
     * Expand entity references; same as {@code expandingEntityReferences(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject expandingEntityReferences() {
        return expandingEntityReferences(true);
    }

    /**
     * Specifies if entity references should be expanded.
     *
     * @param value {@code true} if entity references should be expanded and their replacement
     *     compared, {@code false} if the entity references themselves should be compared
     * @return {@code this}
     */
    public XMLSubject expandingEntityReferences(boolean value) {
        expandEntityReferences = value;
        return this;
    }

    /**
     * Treat element content whitespace as simple text nodes; same as {@code
     * treatingElementContentWhitespaceAsText(true)}.
     *
     * @return {@code this}
     */
    public XMLSubject treatingElementContentWhitespaceAsText() {
        return treatingElementContentWhitespaceAsText(true);
    }

    /**
     * Specifies how element content whitespace is to be treated. Use this when comparing a document
     * that has a DTD with a document that doesn't.
     *
     * @param value {@code true} if element whitespace should be considered as text nodes, {@code
     *     false} if element whitespace should be considered as a distinct node type
     * @return {@code this}
     */
    public XMLSubject treatingElementContentWhitespaceAsText(boolean value) {
        treatWhitespaceAsText = value;
        return this;
    }

    private Traverser createTraverser(XML xml) throws TraverserException {
        Traverser traverser = xml.createTraverser(expandEntityReferences);
        if (ignoreWhitespaceInPrologAndEpilog || ignorePrologAndEpilog) {
            final boolean onlyWhitespace = !ignorePrologAndEpilog;
            traverser =
                    new Filter(traverser) {
                        private int depth;

                        @Override
                        public Event next() throws TraverserException {
                            Event event;
                            while ((event = super.next()) != null) {
                                switch (event) {
                                    case START_ELEMENT:
                                        depth++;
                                        break;
                                    case END_ELEMENT:
                                        depth--;
                                        break;
                                    default:
                                        if (onlyWhitespace) {
                                            break;
                                        }
                                    // Fall through
                                    case WHITESPACE:
                                        if (depth == 0) {
                                            continue;
                                        }
                                }
                                break;
                            }
                            return event;
                        }
                    };
        }
        final Set<Event> ignoredEvents = new HashSet<>();
        if (ignoreComments) {
            ignoredEvents.add(Event.COMMENT);
        }
        if (ignoreWhitespace || ignoreElementContentWhitespace) {
            ignoredEvents.add(Event.WHITESPACE);
        }
        if (!ignoredEvents.isEmpty()) {
            traverser =
                    new Filter(traverser) {
                        @Override
                        public Event next() throws TraverserException {
                            Event event;
                            while (ignoredEvents.contains(event = super.next())) {
                                // loop
                            }
                            return event;
                        }
                    };
        }
        traverser = new CoalescingFilter(traverser);
        if (ignoreWhitespace) {
            traverser =
                    new Filter(traverser) {
                        @Override
                        public Event next() throws TraverserException {
                            Event event = super.next();
                            if (event == Event.TEXT) {
                                String text = getText();
                                for (int i = 0; i < text.length(); i++) {
                                    if (" \r\n\t".indexOf(text.charAt(i)) == -1) {
                                        return Event.TEXT;
                                    }
                                }
                                return super.next();
                            } else {
                                return event;
                            }
                        }
                    };
        }
        if (ignoreRedundantNamespaceDeclarations && !ignoreNamespaceDeclarations) {
            traverser = new RedundantNamespaceDeclarationFilter(traverser);
        }
        return traverser;
    }

    private static Map<QName, String> extractPrefixes(Set<QName> qnames) {
        Map<QName, String> result = new HashMap<>();
        for (QName qname : qnames) {
            result.put(qname, qname.getPrefix());
        }
        return result;
    }

    /**
     * Fails unless the subject represents the same XML as the given object.
     *
     * @param other the object to compare with
     */
    public void hasSameContentAs(Object other) {
        try {
            Traverser actual = createTraverser(xml);
            XML expectedXML = XMLTruth.xml(other);
            Traverser expected = createTraverser(expectedXML);
            while (true) {
                Event actualEvent = actual.next();
                Event expectedEvent = expected.next();
                if (expectedEvent == Event.WHITESPACE || expectedEvent == Event.TEXT) {
                    if (!xml.isReportingElementContentWhitespace()) {
                        assertThat(actualEvent).isEqualTo(Event.TEXT);
                    } else if (treatWhitespaceAsText
                            || !expectedXML.isReportingElementContentWhitespace()) {
                        assertThat(actualEvent).isAnyOf(Event.WHITESPACE, Event.TEXT);
                    } else {
                        assertThat(actualEvent).isEqualTo(expectedEvent);
                    }
                } else {
                    assertThat(actualEvent).isEqualTo(expectedEvent);
                }
                if (expectedEvent == null) {
                    break;
                }
                switch (expectedEvent) {
                    case DOCUMENT_TYPE -> {
                        assertThat(actual.getRootName()).isEqualTo(expected.getRootName());
                        assertThat(actual.getPublicId()).isEqualTo(expected.getPublicId());
                        assertThat(actual.getSystemId()).isEqualTo(expected.getSystemId());
                    }
                    case START_ELEMENT -> {
                        QName actualQName = actual.getQName();
                        Map<QName, String> actualAttributes = actual.getAttributes();
                        QName expectedQName = expected.getQName();
                        Map<QName, String> expectedAttributes = expected.getAttributes();
                        assertThat(actualQName).isEqualTo(expectedQName);
                        assertThat(actualAttributes).isEqualTo(expectedAttributes);
                        if (!ignoreNamespacePrefixes) {
                            assertThat(actualQName.getPrefix())
                                    .isEqualTo(expectedQName.getPrefix());
                            if (expectedAttributes != null) {
                                assertThat(extractPrefixes(actualAttributes.keySet()))
                                        .isEqualTo(extractPrefixes(expectedAttributes.keySet()));
                            }
                        }
                        if (!ignoreNamespaceDeclarations) {
                            assertThat(actual.getNamespaces()).isEqualTo(expected.getNamespaces());
                        }
                    }
                    case END_ELEMENT -> {}
                    case TEXT, WHITESPACE, COMMENT, CDATA_SECTION ->
                            assertThat(actual.getText()).isEqualTo(expected.getText());
                    case ENTITY_REFERENCE -> {
                        if (expandEntityReferences) {
                            throw new IllegalStateException();
                        }
                        assertThat(actual.getEntityName()).isEqualTo(expected.getEntityName());
                    }
                    case PROCESSING_INSTRUCTION -> {
                        assertThat(actual.getPITarget()).isEqualTo(expected.getPITarget());
                        assertThat(actual.getPIData()).isEqualTo(expected.getPIData());
                    }
                    default -> throw new IllegalStateException();
                }
            }
        } catch (TraverserException ex) {
            // TODO: check how to fail properly
            throw new RuntimeException(ex);
        }
    }
}
