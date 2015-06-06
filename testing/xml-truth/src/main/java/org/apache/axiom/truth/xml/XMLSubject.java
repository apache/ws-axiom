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

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;

public final class XMLSubject extends Subject<XMLSubject,XML> {
    private final Set<Event> ignoredEvents = new HashSet<Event>();
    private boolean ignoreWhitespace;
    private boolean ignoreWhitespaceInPrologAndEpilog;
    private boolean ignorePrologAndEpilog;
    private boolean ignoreNamespaceDeclarations;
    private boolean ignoreNamespacePrefixes;
    private boolean ignoreRedundantNamespaceDeclarations;
    private boolean expandEntityReferences;
    private boolean treatWhitespaceAsText;
    
    XMLSubject(FailureStrategy failureStrategy, XML subject) {
        super(failureStrategy, subject);
    }

    public XMLSubject ignoringComments() {
        ignoredEvents.add(Event.COMMENT);
        return this;
    }
    
    public XMLSubject ignoringElementContentWhitespace() {
        ignoredEvents.add(Event.WHITESPACE);
        return this;
    }
    
    public XMLSubject ignoringWhitespace() {
        ignoredEvents.add(Event.WHITESPACE);
        ignoreWhitespace = true;
        return this;
    }
    
    public XMLSubject ignoringWhitespaceInPrologAndEpilog() {
        ignoreWhitespaceInPrologAndEpilog = true;
        return this;
    }
    
    public XMLSubject ignoringPrologAndEpilog() {
        ignorePrologAndEpilog = true;
        return this;
    }
    
    /**
     * Ignore all namespace declarations.
     * 
     * @return <code>this</code>
     */
    public XMLSubject ignoringNamespaceDeclarations() {
        ignoreNamespaceDeclarations = true;
        return this;
    }
    
    public XMLSubject ignoringNamespacePrefixes() {
        ignoreNamespacePrefixes = true;
        return this;
    }
    
    /**
     * Ignore redundant namespace declarations. A namespace declaration is considered redundant if
     * its presence doesn't modify the namespace context.
     * 
     * @return <code>this</code>
     */
    public XMLSubject ignoringRedundantNamespaceDeclarations() {
        ignoreRedundantNamespaceDeclarations = true;
        return this;
    }
    
    public XMLSubject expandingEntityReferences() {
        return expandingEntityReferences(true);
    }
    
    public XMLSubject expandingEntityReferences(boolean value) {
        expandEntityReferences = value;
        return this;
    }
    
    public XMLSubject treatingElementContentWhitespaceAsText() {
        treatWhitespaceAsText = true;
        return this;
    }
    
    private Traverser createTraverser(XML xml) throws TraverserException {
        Traverser traverser = xml.createTraverser(expandEntityReferences);
        if (ignoreWhitespaceInPrologAndEpilog || ignorePrologAndEpilog) {
            final boolean onlyWhitespace = !ignorePrologAndEpilog;
            traverser = new Filter(traverser) {
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
        if (!ignoredEvents.isEmpty()) {
            traverser = new Filter(traverser) {
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
            traverser = new Filter(traverser) {
                @Override
                public Event next() throws TraverserException {
                    Event event = super.next();
                    if (event == Event.TEXT) {
                        String text = getText();
                        for (int i=0; i<text.length(); i++) {
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
        if (treatWhitespaceAsText) {
            traverser = new Filter(traverser) {
                @Override
                public Event next() throws TraverserException {
                    Event event = super.next();
                    return event == Event.WHITESPACE ? Event.TEXT : event;
                }
            };
        }
        if (ignoreRedundantNamespaceDeclarations && !ignoreNamespaceDeclarations) {
            traverser = new RedundantNamespaceDeclarationFilter(traverser);
        }
        return traverser;
    }
    
    private static Map<QName,String> extractPrefixes(Set<QName> qnames) {
        Map<QName,String> result = new HashMap<QName,String>();
        for (QName qname : qnames) {
            result.put(qname, qname.getPrefix());
        }
        return result;
    }
    
    public void hasSameContentAs(XML other) {
        try {
            Traverser actual = createTraverser(getSubject());
            Traverser expected = createTraverser(other);
            while (true) {
                Event actualEvent = actual.next();
                Event expectedEvent = expected.next();
                assertThat(actualEvent).isEqualTo(expectedEvent);
                if (expectedEvent == null) {
                    break;
                }
                switch (expectedEvent) {
                    case DOCUMENT_TYPE:
                        assertThat(actual.getRootName()).isEqualTo(expected.getRootName());
                        assertThat(actual.getPublicId()).isEqualTo(expected.getPublicId());
                        assertThat(actual.getSystemId()).isEqualTo(expected.getSystemId());
                        break;
                    case START_ELEMENT:
                        QName actualQName = actual.getQName();
                        Map<QName,String> actualAttributes = actual.getAttributes();
                        QName expectedQName = expected.getQName();
                        Map<QName,String> expectedAttributes = expected.getAttributes();
                        assertThat(actualQName).isEqualTo(expectedQName);
                        assertThat(actualAttributes).isEqualTo(expectedAttributes);
                        if (!ignoreNamespacePrefixes) {
                            assertThat(actualQName.getPrefix()).isEqualTo(expectedQName.getPrefix());
                            if (expectedAttributes != null) {
                                assertThat(extractPrefixes(actualAttributes.keySet())).isEqualTo(extractPrefixes(expectedAttributes.keySet()));
                            }
                        }
                        if (!ignoreNamespaceDeclarations) {
                            assertThat(actual.getNamespaces()).isEqualTo(expected.getNamespaces());
                        }
                        break;
                    case END_ELEMENT:
                        break;
                    case TEXT:
                    case WHITESPACE:
                    case COMMENT:
                    case CDATA_SECTION:
                        assertThat(actual.getText()).isEqualTo(expected.getText());
                        break;
                    case ENTITY_REFERENCE:
                        if (expandEntityReferences) {
                            throw new IllegalStateException();
                        }
                        assertThat(actual.getEntityName()).isEqualTo(expected.getEntityName());
                        break;
                    case PROCESSING_INSTRUCTION:
                        assertThat(actual.getPITarget()).isEqualTo(expected.getPITarget());
                        assertThat(actual.getPIData()).isEqualTo(expected.getPIData());
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        } catch (TraverserException ex) {
            // TODO: check how to fail properly
            throw new RuntimeException(ex);
        }
    }
}
