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
package org.apache.axiom.ts.dom.w3c;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Assert;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestFramework;

final class JUnitTestFramework implements DOMTestFramework {
    public static final JUnitTestFramework INSTANCE = new JUnitTestFramework();
    
    private JUnitTestFramework() {}
    
    private static String[] toArray(Collection<String> collection, boolean normalizeCase, boolean sort) {
        String[] array = new String[collection.size()];
        int i = 0;
        for (String item : collection) {
            array[i++] = normalizeCase ? item.toLowerCase(Locale.ENGLISH) : item;
        }
        if (sort) {
            Arrays.sort(array);
        }
        return array;
    }
    
    @Override
    public boolean hasFeature(DocumentBuilder docBuilder, String feature, String version)  {
       return docBuilder.getDOMImplementation().hasFeature(feature,version);
    }

    @Override
    public void wait(int millisecond) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void fail(DOMTestCase test, String assertID) {
        Assert.fail(assertID);
    }

    @Override
    public void assertTrue(DOMTestCase test, String assertID, boolean actual) {
        Assert.assertTrue(assertID, actual);
    }

    @Override
    public void assertFalse(DOMTestCase test, String assertID, boolean actual) {
        Assert.assertFalse(assertID, actual);
    }

    @Override
    public void assertNull(DOMTestCase test, String assertID, Object actual) {
        Assert.assertNull(assertID, actual);
    }

    @Override
    public void assertNotNull(DOMTestCase test, String assertID, Object actual) {
        Assert.assertNotNull(assertID, actual);
    }

    @Override
    public void assertSame(DOMTestCase test, String assertID, Object expected, Object actual) {
        Assert.assertSame(assertID, expected, actual);
    }

    @Override
    public void assertInstanceOf(DOMTestCase test, String assertID, Object obj, Class cls) {
        Assert.assertTrue(assertID, cls.isInstance(obj));
    }

    @Override
    public void assertSize(DOMTestCase test, String assertID, int expectedSize, NodeList collection) {
        Assert.assertEquals(assertID, expectedSize, collection.getLength());
    }

    @Override
    public void assertSize(DOMTestCase test, String assertID, int expectedSize, NamedNodeMap collection) {
        Assert.assertEquals(assertID, expectedSize, collection.getLength());
    }

    @Override
    public void assertSize(DOMTestCase test, String assertID, int expectedSize, Collection collection) {
        Assert.assertEquals(assertID, expectedSize, collection.size());
    }

    @Override
    public void assertEqualsIgnoreCase(DOMTestCase test, String assertID, String expected, String actual) {
        Assert.assertEquals(assertID, expected, actual);
    }

    @Override
    public void assertEqualsIgnoreCase(DOMTestCase test, String assertID, Collection expected, Collection actual) {
        Assert.assertArrayEquals(assertID, toArray(expected, true, true), toArray(actual, true, true));
    }

    @Override
    public void assertEqualsIgnoreCase(DOMTestCase test, String assertID, List expected, List actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertEquals(DOMTestCase test, String assertID, String expected, String actual) {
        Assert.assertEquals(assertID, expected, actual);
    }

    @Override
    public void assertEquals(DOMTestCase test, String assertID, int expected, int actual) {
        Assert.assertEquals(assertID, expected, actual);
    }

    @Override
    public void assertEquals(DOMTestCase test, String assertID, boolean expected, boolean actual) {
        Assert.assertEquals(assertID, expected, actual);
    }

    @Override
    public void assertEquals(DOMTestCase test, String assertID, double expected, double actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertEquals(DOMTestCase test, String assertID, Collection expected, Collection actual) {
        Assert.assertArrayEquals(assertID, toArray(expected, false, true), toArray(actual, false, true));
    }

    @Override
    public void assertNotEqualsIgnoreCase(DOMTestCase test, String assertID, String expected, String actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertNotEquals(DOMTestCase test, String assertID, String expected, String actual) {
        Assert.assertFalse(assertID, expected.equals(actual));
    }

    @Override
    public void assertNotEquals(DOMTestCase test, String assertID, int expected, int actual) {
        Assert.assertFalse(assertID, expected == actual);
    }

    @Override
    public void assertNotEquals(DOMTestCase test, String assertID, boolean expected, boolean actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertNotEquals(DOMTestCase test, String assertID, double expected, double actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean same(Object expected, Object actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equalsIgnoreCase(String expected, String actual) {
        return expected.equalsIgnoreCase(actual);
    }

    @Override
    public boolean equalsIgnoreCase(Collection expected, Collection actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equalsIgnoreCase(List expected, List actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(String expected, String actual) {
        return expected.equals(actual);
    }

    @Override
    public boolean equals(int expected, int actual) {
        return expected == actual;
    }

    @Override
    public boolean equals(boolean expected, boolean actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(double expected, double actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Collection expected, Collection actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(List expected, List actual) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public int size(Collection collection) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public int size(NamedNodeMap collection) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public int size(NodeList collection) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
