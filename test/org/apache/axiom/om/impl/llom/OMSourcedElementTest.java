/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;

public class OMSourcedElementTest extends AbstractTestCase {
    private static String testDocument =
        "<library xmlns='http://www.sosnoski.com/uwjws/library' books='1'>" +
        "<type id='java' category='professional' deductable='true'>"+
        "<name>Java Reference</name></type><type id='xml' "+
        "category='professional' deductable='true'><name>XML Reference</name>" +
        "</type><book isbn='1930110111' type='xml'><title>XSLT Quickly</title>" +
        "<author>DuCharme, Bob</author><publisher>Manning</publisher>" +
        "<price>29.95</price></book></library>";
    
    private OMSourcedElementImpl element;

    /**
     * @param testName
     */
    public OMSourcedElementTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        element = new OMSourcedElementImpl("library",
            new OMNamespaceImpl("http://www.sosnoski.com/uwjws/library", ""),
                new OMLinkedListImplFactory(), new TestDataSource(testDocument));
    }
    
    public void testMethodOverrides() {
        Method[] submeths = OMSourcedElementImpl.class.getDeclaredMethods();
        Method[] supmeths = OMElementImpl.class.getDeclaredMethods();
        outer: for (int i = 0; i < supmeths.length; i++) {
            Method supmeth = supmeths[i];
            Class[] params = supmeth.getParameterTypes();
            if (!Modifier.isPrivate(supmeth.getModifiers())) {
                for (int j = 0; j < submeths.length; j++) {
                    Method submeth = submeths[j];
                    if (supmeth.getName().equals(submeth.getName())) {
                        if (Arrays.equals(params, submeth.getParameterTypes())) {
                            continue outer;
                        }
                    }
                }
                fail("OMSourcedElementImpl must override method " + supmeth +
                    "\nSee class JavaDocs for details");
            }
        }
    }
    
    private int countItems(Iterator iter) {
        int count = 0;
        while (iter.hasNext()) {
            count++;
            iter.next();
        }
        return count;
    }
    
    public void testSerializeToStream() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        element.serialize(bos);
        assertEquals("Serialized text error", testDocument,
            new String(bos.toByteArray()));
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    public void testSerializeToWriter() throws Exception {
        StringWriter writer = new StringWriter();
        element.serialize(writer);
        assertEquals("Serialized text error", testDocument, writer.toString());
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    public void testSerializeToXMLWriter() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        element.serialize(writer);
        xmlwriter.flush();
        assertEquals("Serialized text error", testDocument, writer.toString());
        assertFalse("Element expansion when serializing", element.isExpanded());
    }

    public void testExpand() throws Exception {
        element.getAllDeclaredNamespaces();
        assertEquals("Expanded namespace count error", 1,
            countItems(element.getAllDeclaredNamespaces()));
        assertEquals("Expanded attribute count error", 1,
            countItems(element.getAllAttributes()));
        assertEquals("Expanded attribute value error", "1",
            element.getAttributeValue(new QName("books")));
        OMElement child = element.getFirstElement();
        assertEquals("Child element name", "type", child.getLocalName());
        assertEquals("Child element namespace",
            "http://www.sosnoski.com/uwjws/library", child.getNamespace().getName());
        OMNode next = child.getNextOMSibling();
        assertTrue("Expected child element", next instanceof OMElement);
        next = next.getNextOMSibling();
        assertTrue("Expected child element", next instanceof OMElement);
        child = (OMElement)next;
        assertEquals("Child element name", "book", child.getLocalName());
        assertEquals("Child element namespace",
            "http://www.sosnoski.com/uwjws/library", child.getNamespace().getName());
        assertEquals("Attribute value error", "xml",
            child.getAttributeValue(new QName("type")));
    }
    
    private static class TestDataSource implements OMDataSource {
        private final String data;
        
        private TestDataSource(String data) {
            this.data = data;
        }

        /* (non-Javadoc)
         * @see org.apache.axiom.om.OMDataSource#serialize(java.io.OutputStream, org.apache.axiom.om.OMOutputFormat)
         */
        public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
            try {
                output.write(data.getBytes());
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }

        /* (non-Javadoc)
         * @see org.apache.axiom.om.OMDataSource#serialize(java.io.Writer, org.apache.axiom.om.OMOutputFormat)
         */
        public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
            try {
                writer.write(data);
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }

        /* (non-Javadoc)
         * @see org.apache.axiom.om.OMDataSource#serialize(javax.xml.stream.XMLStreamWriter)
         */
        public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
            StreamingOMSerializer serializer = new StreamingOMSerializer();
            serializer.serialize(getReader(), xmlWriter);
        }

        /* (non-Javadoc)
         * @see org.apache.axiom.om.OMDataSource#getReader()
         */
        public XMLStreamReader getReader() throws XMLStreamException {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            return inputFactory.createXMLStreamReader(new StringReader(data));
        }
    }
}