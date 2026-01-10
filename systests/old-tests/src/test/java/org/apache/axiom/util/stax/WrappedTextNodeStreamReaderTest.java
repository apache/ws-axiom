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

package org.apache.axiom.util.stax;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;

public class WrappedTextNodeStreamReaderTest extends TestCase {
    //
    // Tests that construct the Axiom tree and check the result
    //

    private void testUsingBuilder(QName wrapperElementName, String testString, int chunkSize) {
        StringReader reader = new StringReader(testString);
        XMLStreamReader xmlStreamReader =
                new WrappedTextNodeStreamReader(wrapperElementName, reader, chunkSize);
        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(xmlStreamReader).getDocumentElement();
        assertEquals(wrapperElementName, element.getQName());
        assertEquals(wrapperElementName.getPrefix(), element.getQName().getPrefix());
        assertEquals(testString, element.getText());
    }

    public void testShortStringUsingBuilder() {
        testUsingBuilder(
                new QName("urn:test", "test"),
                "This is a test string for WrappedTextNodeStreamReader",
                4096);
    }

    public void testLongStringUsingBuilder() {
        // "Long" is relative to the chunk size
        testUsingBuilder(
                new QName("urn:test", "test"),
                "This is a test string for WrappedTextNodeStreamReader",
                10);
    }

    public void testWrapperElementWithoutNamespaceUsingBuilder() {
        testUsingBuilder(
                new QName("test"), "This is a test string for WrappedTextNodeStreamReader", 4096);
    }

    public void testWrapperElementWithPrefixUsingBuilder() {
        testUsingBuilder(
                new QName("urn:test", "bar", "foo"),
                "This is a test string for WrappedTextNodeStreamReader",
                4096);
    }

    //
    // Tests that construct the Axiom tree, serialize it using serializeAndConsume and
    // compare with the expected result.
    //

    private void testUsingSerializeAndConsume(
            QName wrapperElementName, String testString, int chunkSize, String expectedXML)
            throws Exception {
        StringReader reader = new StringReader(testString);
        XMLStreamReader xmlStreamReader =
                new WrappedTextNodeStreamReader(wrapperElementName, reader, chunkSize);
        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(xmlStreamReader).getDocumentElement();
        StringWriter writer = new StringWriter();
        element.serializeAndConsume(writer);
        assertAbout(xml()).that(writer.toString()).hasSameContentAs(expectedXML);
    }

    public void testShortStringUsingSerializeAndConsume() throws Exception {
        String testString = "This is a test string for WrappedTextNodeStreamReader";
        testUsingSerializeAndConsume(
                new QName("urn:test", "test"),
                testString,
                4096,
                "<test xmlns=\"urn:test\">" + testString + "</test>");
    }
}
