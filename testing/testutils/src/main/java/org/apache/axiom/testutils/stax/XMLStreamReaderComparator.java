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
package org.apache.axiom.testutils.stax;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * Helper class that compares the events produced by two {@link XMLStreamReader} objects. Note that
 * this class is not meant to be used to compare two XML documents (the error reporting would not be
 * clear enough for that purpose), but to validate implementations of the {@link XMLStreamReader}
 * interface. It uses a brute force approach: for each event, all methods (that don't modify the
 * reader state) are called on both readers and the results (return values or exceptions thrown) of
 * these invocations are compared to each other.
 */
public class XMLStreamReaderComparator {
    private XMLStreamReader expected;
    private XMLStreamReader actual;
    private boolean compareInternalSubset = true;
    private boolean compareEntityReplacementValue = true;
    private boolean compareCharacterEncodingScheme = true;
    private boolean compareEncoding = true;
    private boolean sortAttributes = false;
    private boolean treatSpaceAsCharacters = false;
    private final LinkedList<QName> path = new LinkedList<>();

    /**
     * Set collecting all prefixes seen in the document to be able to test {@link
     * XMLStreamReader#getNamespaceURI(String)}.
     */
    private final Set<String> prefixes = new HashSet<>();

    /**
     * Set collecting all namespace URIs seen in the document to be able to test {@link
     * NamespaceContext#getPrefix(String)}.
     */
    private final Set<String> namespaceURIs = new HashSet<>();

    public XMLStreamReaderComparator(XMLStreamReader expected, XMLStreamReader actual) {
        this.expected = expected;
        this.actual = actual;
    }

    private String getLocation() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("event type ");
        buffer.append(expected.getEventType());
        buffer.append("; location ");
        for (QName qname : path) {
            buffer.append('/');
            buffer.append(qname);
        }
        return buffer.toString();
    }

    private <T> InvocationResults<T> invoke(
            Class<T> returnType, String methodName, Class<?>[] paramTypes, Object[] args)
            throws Exception {
        Method method = XMLStreamReader.class.getMethod(methodName, paramTypes);

        T expectedResult;
        Throwable expectedException;
        try {
            expectedResult = returnType.cast(method.invoke(expected, args));
            expectedException = null;
        } catch (InvocationTargetException ex) {
            expectedResult = null;
            expectedException = ex.getCause();
        }

        T actualResult;
        Throwable actualException;
        try {
            actualResult = returnType.cast(method.invoke(actual, args));
            actualException = null;
        } catch (InvocationTargetException ex) {
            actualResult = null;
            actualException = ex.getCause();
        }

        if (expectedException == null) {
            if (actualException != null) {
                actualException.printStackTrace(System.out);
                fail(
                        "Method "
                                + methodName
                                + " threw unexpected exception "
                                + actualException.getClass().getName()
                                + " ("
                                + getLocation()
                                + ")");
            } else {
                return new InvocationResults<>(expectedResult, actualResult);
            }
        } else {
            if (actualException == null) {
                fail(
                        "Expected "
                                + methodName
                                + " to throw "
                                + expectedException.getClass().getName()
                                + ", but the method returned normally ("
                                + getLocation()
                                + ")");
            } else {
                assertEquals(
                        "Unexpected exception thrown by " + methodName,
                        expectedException.getClass(),
                        actualException.getClass());
            }
        }
        return null;
    }

    private <T> InvocationResults<T> invoke(Class<T> returnType, String methodName)
            throws Exception {
        return invoke(returnType, methodName, new Class[0], new Object[0]);
    }

    private <T> T assertSameResult(
            Class<T> returnType,
            String methodName,
            Class<?>[] paramTypes,
            Object[] args,
            Normalizer<T> normalizer)
            throws Exception {

        InvocationResults<T> results = invoke(returnType, methodName, paramTypes, args);
        if (results != null) {
            T expected =
                    normalizer == null
                            ? results.getExpected()
                            : normalizer.normalize(results.getExpected());
            T actual =
                    normalizer == null
                            ? results.getActual()
                            : normalizer.normalize(results.getActual());
            assertEquals(
                    "Return value of "
                            + methodName
                            + " for arguments "
                            + Arrays.asList(args)
                            + " ("
                            + getLocation()
                            + ")",
                    expected,
                    actual);
            return results.getExpected();
        } else {
            return null;
        }
    }

    private <T> T assertSameResult(
            Class<T> returnType, String methodName, Class<?>[] paramTypes, Object[] args)
            throws Exception {
        return assertSameResult(returnType, methodName, paramTypes, args, null);
    }

    private <T> T assertSameResult(Class<T> returnType, String methodName, Normalizer<T> normalizer)
            throws Exception {
        return assertSameResult(returnType, methodName, new Class[0], new Object[0], normalizer);
    }

    private <T> T assertSameResult(Class<T> returnType, String methodName) throws Exception {
        return assertSameResult(returnType, methodName, null);
    }

    private Set<String> toPrefixSet(Iterator<?> it) {
        Set<String> set = new HashSet<>();
        while (it.hasNext()) {
            String prefix = (String) it.next();
            // TODO: Woodstox returns null instead of "" for the default namespace.
            //       This seems incorrect, but the javax.namespace.NamespaceContext specs are
            //       not very clear.
            set.add(prefix == null ? "" : prefix);
        }
        return set;
    }

    private void compareNamespaceContexts(NamespaceContext expected, NamespaceContext actual) {
        for (String prefix : prefixes) {
            if (prefix != null) {
                assertEquals(
                        "Namespace URI for prefix '" + prefix + "' (" + getLocation() + ")",
                        expected.getNamespaceURI(prefix),
                        actual.getNamespaceURI(prefix));
            }
        }
        for (String namespaceURI : namespaceURIs) {
            if (namespaceURI != null && namespaceURI.length() > 0) {
                Set<String> prefixes = toPrefixSet(expected.getPrefixes(namespaceURI));
                assertEquals(
                        "Prefixes for namespace URI '" + namespaceURI + "' (" + getLocation() + ")",
                        prefixes,
                        toPrefixSet(actual.getPrefixes(namespaceURI)));
                if (prefixes.size() <= 1) {
                    assertEquals(
                            "Prefix for namespace URI '"
                                    + namespaceURI
                                    + "' ("
                                    + getLocation()
                                    + ")",
                            expected.getPrefix(namespaceURI),
                            actual.getPrefix(namespaceURI));
                } else {
                    assertThat(actual.getPrefix(namespaceURI)).isIn(prefixes);
                }
            }
        }
    }

    /**
     * Add a prefix that should be used in testing the {@link
     * XMLStreamReader#getNamespaceURI(String)} method.
     *
     * @param prefix the prefix to add
     */
    public void addPrefix(String prefix) {
        prefixes.add(prefix);
    }

    public void setCompareInternalSubset(boolean compareInternalSubset) {
        this.compareInternalSubset = compareInternalSubset;
    }

    /**
     * Specify whether the replacement value for entity references (as reported by {@link
     * XMLStreamReader#getText()}) should be compared. The default value for this option is <code>
     * true</code>.
     *
     * @param value <code>true</code> if the replacement value should be compared; <code>false
     *     </code> if replacement values for entity references are ignored
     */
    public void setCompareEntityReplacementValue(boolean value) {
        compareEntityReplacementValue = value;
    }

    public void setCompareCharacterEncodingScheme(boolean value) {
        compareCharacterEncodingScheme = value;
    }

    public void setCompareEncoding(boolean value) {
        compareEncoding = value;
    }

    public void setSortAttributes(boolean sortAttributes) {
        this.sortAttributes = sortAttributes;
    }

    public void setTreatSpaceAsCharacters(boolean treatSpaceAsCharacters) {
        this.treatSpaceAsCharacters = treatSpaceAsCharacters;
    }

    public void compare() throws Exception {
        if (sortAttributes) {
            expected = new AttributeSortingXMLStreamReaderFilter(expected);
            actual = new AttributeSortingXMLStreamReaderFilter(actual);
        }
        if (treatSpaceAsCharacters) {
            expected = new SpaceAsCharactersXMLStreamReaderFilter(expected);
            actual = new SpaceAsCharactersXMLStreamReaderFilter(actual);
        }
        while (true) {
            int eventType = assertSameResult(Integer.class, "getEventType");
            if (eventType == XMLStreamReader.START_ELEMENT) {
                path.addLast(expected.getName());
            }
            if (compareCharacterEncodingScheme) {
                assertSameResult(String.class, "getCharacterEncodingScheme");
            }
            if (compareEncoding) {
                assertSameResult(String.class, "getEncoding", Normalizer.LOWER_CASE);
            }
            Integer attributeCount = assertSameResult(Integer.class, "getAttributeCount");
            // Test the behavior of the getAttributeXxx methods for all types of events,
            // to check that an appropriate exception is thrown for events other than
            // START_ELEMENT
            for (int i = 0; i < (attributeCount == null ? 1 : attributeCount.intValue()); i++) {
                Class<?>[] paramTypes = {Integer.TYPE};
                Object[] args = {new Integer(i)};
                assertSameResult(String.class, "getAttributeLocalName", paramTypes, args);
                assertSameResult(QName.class, "getAttributeName", paramTypes, args);
                namespaceURIs.add(
                        assertSameResult(String.class, "getAttributeNamespace", paramTypes, args));
                prefixes.add(
                        assertSameResult(
                                String.class,
                                "getAttributePrefix",
                                paramTypes,
                                args,
                                Normalizer.EMPTY_STRING_TO_NULL));
                assertSameResult(String.class, "getAttributeType", paramTypes, args);
                assertSameResult(String.class, "getAttributeValue", paramTypes, args);
                assertSameResult(Boolean.class, "isAttributeSpecified", paramTypes, args);
            }
            if (attributeCount != null) {
                for (int i = 0; i < attributeCount.intValue(); i++) {
                    QName qname = expected.getAttributeName(i);
                    assertSameResult(
                            String.class,
                            "getAttributeValue",
                            new Class[] {String.class, String.class},
                            new Object[] {qname.getNamespaceURI(), qname.getLocalPart()});
                }
            }
            assertSameResult(String.class, "getLocalName");
            assertSameResult(QName.class, "getName");
            Integer namespaceCount = assertSameResult(Integer.class, "getNamespaceCount");
            if (namespaceCount != null) {
                Map<String, String> expectedNamespaces = new HashMap<>();
                Map<String, String> actualNamespaces = new HashMap<>();
                for (int i = 0; i < namespaceCount.intValue(); i++) {
                    String expectedPrefix = expected.getNamespacePrefix(i);
                    String expectedNamespaceURI = expected.getNamespaceURI(i);
                    if (expectedNamespaceURI != null && expectedNamespaceURI.length() == 0) {
                        expectedNamespaceURI = null;
                    }
                    String actualPrefix = actual.getNamespacePrefix(i);
                    String actualNamespaceURI = actual.getNamespaceURI(i);
                    if (actualNamespaceURI != null && actualNamespaceURI.length() == 0) {
                        actualNamespaceURI = null;
                    }
                    expectedNamespaces.put(expectedPrefix, expectedNamespaceURI);
                    actualNamespaces.put(actualPrefix, actualNamespaceURI);
                    prefixes.add(expectedPrefix);
                    namespaceURIs.add(expectedNamespaceURI);
                }
                assertEquals(expectedNamespaces, actualNamespaces);
            }
            namespaceURIs.add(assertSameResult(String.class, "getNamespaceURI"));
            assertSameResult(String.class, "getPIData");
            assertSameResult(String.class, "getPITarget");
            prefixes.add(assertSameResult(String.class, "getPrefix"));
            if ((eventType != XMLStreamReader.DTD || compareInternalSubset)
                    && (eventType != XMLStreamReader.ENTITY_REFERENCE
                            || compareEntityReplacementValue)) {
                assertSameResult(
                        String.class,
                        "getText",
                        eventType == XMLStreamReader.DTD ? Normalizer.DTD : null);
            }
            Integer textLength = assertSameResult(Integer.class, "getTextLength");
            InvocationResults<Integer> textStart = invoke(Integer.class, "getTextStart");
            InvocationResults<char[]> textCharacters = invoke(char[].class, "getTextCharacters");
            if (textLength != null) {
                assertEquals(
                        new String(
                                textCharacters.getExpected(), textStart.getExpected(), textLength),
                        new String(textCharacters.getActual(), textStart.getActual(), textLength));
            }
            assertSameResult(Boolean.class, "hasName");
            assertSameResult(Boolean.class, "hasText");
            assertSameResult(Boolean.class, "isCharacters");
            assertSameResult(Boolean.class, "isEndElement");
            assertSameResult(Boolean.class, "isStartElement");
            assertSameResult(Boolean.class, "isWhiteSpace");

            // Only check getNamespaceURI(String) for START_ELEMENT and END_ELEMENT. The Javadoc
            // of XMLStreamReader suggests that this method is valid for all states, but Woodstox
            // only allows it for some states.
            if (eventType == XMLStreamReader.START_ELEMENT
                    || eventType == XMLStreamReader.END_ELEMENT) {
                for (String prefix : prefixes) {
                    // The StAX specs are not clear about the expected result of getNamespaceURI
                    // when called with prefix "xml" (which doesn't require an explicit declaration)
                    if (prefix != null && !prefix.equals("xml")) {
                        assertSameResult(
                                String.class,
                                "getNamespaceURI",
                                new Class[] {String.class},
                                new Object[] {prefix});
                    }
                }
            }

            compareNamespaceContexts(expected.getNamespaceContext(), actual.getNamespaceContext());

            if (eventType == XMLStreamReader.END_ELEMENT) {
                path.removeLast();
            }

            assertSameResult(Boolean.class, "hasNext");

            int expectedNextEvent;
            try {
                expectedNextEvent = expected.next();
            } catch (IllegalStateException ex) {
                expectedNextEvent = -1;
            } catch (NoSuchElementException ex) {
                expectedNextEvent = -1;
            }
            if (expectedNextEvent == -1) {
                try {
                    actual.next();
                } catch (IllegalStateException ex) {
                    break;
                } catch (NoSuchElementException ex) {
                    break;
                }
                fail("Expected reader to throw IllegalStateException or NoSuchElementException");
            } else {
                assertEquals("Event type at " + getLocation(), expectedNextEvent, actual.next());
            }
        }
    }
}
