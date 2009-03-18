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
package org.apache.axiom.om.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.apache.axiom.om.util.StAXUtils;

/**
 * Helper class that compares the events produced by two {@link XMLStreamReader} objects.
 */
public class XMLStreamReaderComparator extends Assert {
    private final XMLStreamReader expected;
    private final XMLStreamReader actual;
    
    public XMLStreamReaderComparator(XMLStreamReader expected, XMLStreamReader actual) {
        this.expected = expected;
        this.actual = actual;
    }

    private Object[] invoke(String methodName, Object[] args) throws Exception {
        if (args == null) {
            args = new Object[0];
        }
        Method[] methods = XMLStreamReader.class.getMethods();
        Method method = null;
        for (int i=0; i<methods.length; i++) {
            Method candidate = methods[i];
            if (candidate.getName().equals(methodName) &&
                    candidate.getParameterTypes().length == args.length) {
                method = candidate;
                break;
            }
        }
        if (method == null) {
            fail("Method " + methodName + " not found");
        }
        
        Object expectedResult;
        Throwable expectedException;
        try {
            expectedResult = method.invoke(expected, args);
            expectedException = null;
        } catch (InvocationTargetException ex) {
            expectedResult = null;
            expectedException = ex.getCause();
        }

        Object actualResult;
        Throwable actualException;
        try {
            actualResult = method.invoke(actual, args);
            actualException = null;
        } catch (InvocationTargetException ex) {
            actualResult = null;
            actualException = ex.getCause();
        }
        
        if (expectedException == null) {
            if (actualException != null) {
                fail("Method " + methodName + " threw unexpected exception " +
                        actualException.getClass().getName() + "; event type was " +
                        StAXUtils.getEventTypeString(expected.getEventType()));
            } else {
                return new Object[] { expectedResult, actualResult };
            }
        } else {
            if (actualException == null) {
                fail("Expected " + methodName + " to throw " +
                        expectedException.getClass().getName() +
                        ", but the method retuned normally; event type was " +
                        StAXUtils.getEventTypeString(expected.getEventType()));
            } else {
                assertEquals(expectedException.getClass(), actualException.getClass());
            }
        }
        return null;
    }
    
    private Object assertSameResult(String methodName, Object[] args) throws Exception {
        Object[] results = invoke(methodName, args);
        if (results != null) {
            assertEquals("Return value of " + methodName + " (event type " +
                        StAXUtils.getEventTypeString(expected.getEventType()) + ")",
                        results[0], results[1]);
            return results[0];
        } else {
            return null;
        }
    }
    
    public void compare() throws Exception {
        while (expected.next() != XMLStreamReader.END_DOCUMENT) {
            actual.next();
            Integer attributeCount = (Integer)assertSameResult("getAttributeCount", null);
            if (attributeCount != null) {
                for (int i=0; i<attributeCount.intValue(); i++) {
                    Object[] args = { Integer.valueOf(i) };
                    assertSameResult("getAttributeLocalName", args);
                    assertSameResult("getAttributeName", args);
                    assertSameResult("getAttributeNamespace", args);
                    assertSameResult("getAttributePrefix", args);
                    assertSameResult("getAttributeType", args);
                    assertSameResult("getAttributeValue", args);
                }
            }
            assertSameResult("getLocalName", null);
            assertSameResult("getName", null);
            Integer namespaceCount = (Integer)assertSameResult("getNamespaceCount", null);
            if (namespaceCount != null) {
                Map expectedNamespaces = new HashMap();
                Map actualNamespaces = new HashMap();
                for (int i=0; i<namespaceCount.intValue(); i++) {
                    expectedNamespaces.put(expected.getNamespacePrefix(i),
                            expected.getNamespaceURI(i));
                    actualNamespaces.put(actual.getNamespacePrefix(i),
                            actual.getNamespaceURI(i));
                }
                assertEquals(expectedNamespaces, actualNamespaces);
            }
            assertSameResult("getNamespaceURI", null);
            assertSameResult("getPIData", null);
            assertSameResult("getPITarget", null);
            assertSameResult("getPrefix", null);
            assertSameResult("getText", null);
            assertSameResult("getTextLength", null);
            assertSameResult("hasName", null);
            assertSameResult("hasText", null);
            assertSameResult("isCharacters", null);
            assertSameResult("isEndElement", null);
            assertSameResult("isStartElement", null);
            assertSameResult("isWhiteSpace", null);
        }
    }
}
