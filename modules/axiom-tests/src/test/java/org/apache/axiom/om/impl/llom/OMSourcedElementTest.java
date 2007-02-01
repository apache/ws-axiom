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

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.OMNamespaceImpl;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.ByteArrayInputStream;
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

/**
 * Tests the characteristics of OMSourcedElementImpl.
 */
public class OMSourcedElementTest extends AbstractTestCase {
    private static String testDocument =
        "<library xmlns=\"http://www.sosnoski.com/uwjws/library\" books=\"1\">" +
        "<type id=\"java\" category=\"professional\" deductable=\"true\">"+
        "<name>Java Reference</name></type><type id=\"xml\" "+
        "category=\"professional\" deductable=\"true\"><name>XML Reference</name>" +
        "</type><book isbn=\"1930110111\" type=\"xml\"><title>XSLT Quickly</title>" +
        "<author>DuCharme, Bob</author><publisher>Manning</publisher>" +
        "<price>29.95</price></book></library>";
    
    private OMSourcedElementImpl element;
    private OMElement root;

    /**
     * @param testName
     */
    public OMSourcedElementTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        OMFactory f = new OMLinkedListImplFactory();
        OMNamespace ns = new OMNamespaceImpl("http://www.sosnoski.com/uwjws/library", "");
        OMNamespace rootNS = new OMNamespaceImpl("http://sampleroot", "rootPrefix");
        element = new OMSourcedElementImpl("library", ns, f, new TestDataSource(testDocument));
        root = f.createOMElement("root", rootNS);
        root.addChild(element);
    }
    
    /**
     * Ensure that each method of OMElementImpl is overridden in OMSourcedElementImpl
     */
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
    
    /**
     * Test serialization of OMSourcedElementImpl to a Stream
     * @throws Exception
     */
    public void testSerializeToStream() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        element.serialize(bos);
        String newText = new String(bos.toByteArray());
        System.out.println(testDocument);
        System.out.println(newText);
        assertEquals("Serialized text error", testDocument, newText);
        assertTrue("Element not expanded when serializing", element.isExpanded());
        
        bos = new ByteArrayOutputStream();
        element.serialize(bos);
        assertEquals("Serialized text error", testDocument,
            new String(bos.toByteArray()));
        assertTrue("Element not expanded when serializing", element.isExpanded());
    }
    
    /**
     * Test serialization of OMSourcedElementImpl to a Stream
     * @throws Exception
     */
    public void testSerializeAndConsumeToStream() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        element.serializeAndConsume(bos);
        assertEquals("Serialized text error", testDocument,
            new String(bos.toByteArray()));
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    /**
     * Test serialization of OMSourcedElementImpl to a Writer
     * @throws Exception
     */
    public void testSerializeToWriter() throws Exception {
        StringWriter writer = new StringWriter();
        element.serialize(writer);
        String result = writer.toString();
        assertEquals("Serialized text error", testDocument, result);
        assertTrue("Element not expanded when serializing", element.isExpanded());
        
        writer = new StringWriter();
        element.serialize(writer);
        result = writer.toString();
        assertEquals("Serialized text error", testDocument, result);
        assertTrue("Element not expanded when serializing", element.isExpanded());
    }
    
    /**
     * Test serialization of OMSourcedElementImpl to a Writer
     * @throws Exception
     */
    public void testSerializeAndConsumeToWriter() throws Exception {
        StringWriter writer = new StringWriter();
        element.serializeAndConsume(writer);
        String result = writer.toString();
        assertEquals("Serialized text error", testDocument, result);
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    /**
     * Test serialization of OMSourcedElementImpl to an XMLWriter
     * @throws Exception
     */
    public void testSerializeToXMLWriter() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        element.serialize(writer);
        xmlwriter.flush();
        assertEquals("Serialized text error", testDocument, writer.toString());
        assertTrue("Element not expanded when serializing", element.isExpanded());
        
        writer = new StringWriter();
        xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        element.serialize(writer);
        xmlwriter.flush();
        assertEquals("Serialized text error", testDocument, writer.toString());
        assertTrue("Element not expanded when serializing", element.isExpanded());
    }
    
    /**
     * Test serialization of OMSourcedElementImpl to an XMLWriter
     * @throws Exception
     */
    public void testSerializeAndConsumeToXMLWriter() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        element.serializeAndConsume(writer);
        xmlwriter.flush();
        assertEquals("Serialized text error", testDocument, writer.toString());
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    /**
     * Tests OMSourcedElement serialization when the root (parent) is serialized.
     * @throws Exception
     */
    public void testSerializeToXMLWriterEmbedded() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        root.serialize(writer);
        xmlwriter.flush();
        String result = writer.toString();
        // We can't test for equivalence because the underlying OMSourceElement is 
        // streamed as it is serialized.  So I am testing for an internal value.
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
        assertTrue("Element not expanded when serializing", element.isExpanded());
        
        writer = new StringWriter();
        xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        root.serialize(writer);
        xmlwriter.flush();
        result = writer.toString();
        // We can't test for equivalence because the underlying OMSourceElement is 
        // streamed as it is serialized.  So I am testing for an internal value.
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
        assertTrue("Element not expanded when serializing", element.isExpanded());
    }
    
    /**
     * Tests OMSourcedElement serialization when the root (parent) is serialized.
     * @throws Exception
     */
    public void testSerializeAndConsumeToXMLWriterEmbedded() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        root.serializeAndConsume(writer);
        xmlwriter.flush();
        String result = writer.toString();
        // We can't test for equivalence because the underlying OMSourceElement is 
        // streamed as it is serialized.  So I am testing for an internal value.
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    /**
     * Tests OMSourcedElement getReader support
     * @throws Exception
     */
    public void testSerializeToXMLWriterFromReader() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        
        StAXOMBuilder builder = new StAXOMBuilder(element.getXMLStreamReader());  
        OMDocument omDocument = builder.getDocument();
        Iterator it = omDocument.getChildren();
        while(it.hasNext()) {
            OMNode omNode = (OMNode) it.next();
            omNode.serializeAndConsume(xmlwriter);
        }
        
        xmlwriter.flush();
        String result = writer.toString();
        // We can't test for equivalence because the underlying OMSourceElement is 
        // changed as it is serialized.  So I am testing for an internal value.
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
        assertFalse("Element expansion when serializing", element.isExpanded());
    }
    
    /**
     * Tests OMSourcedElement processing when the getReader() of the parent is accessed.
     * @throws Exception
     */
    public void testSerializeToXMLWriterFromReaderEmbedded() throws Exception {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        
        StAXOMBuilder builder = new StAXOMBuilder(root.getXMLStreamReader());  
        OMDocument omDocument = builder.getDocument();
        Iterator it = omDocument.getChildren();
        while(it.hasNext()) {
            OMNode omNode = (OMNode) it.next();
            omNode.serializeAndConsume(xmlwriter);
        }
        xmlwriter.flush();
        String result = writer.toString();
        // We can't test for equivalence because the underlying OMSourceElement is 
        // changed as it is serialized.  So I am testing for an internal value.
        assertTrue("Serialized text error" + result, result.indexOf("1930110111") > 0);
        // The implementation uses OMNavigator to walk the tree.  Currently OMNavigator must 
        // expand the OMSourcedElement to correctly walk the elements. (See OMNavigator._getFirstChild)
        //assertFalse("Element expansion when serializing", element.isExpanded());
    }

    /**
     * Make sure the expanded OMSourcedElement behaves like a normal OMElement.
     * @throws Exception
     */
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
            "http://www.sosnoski.com/uwjws/library", child.getNamespace().getNamespaceURI());
        OMNode next = child.getNextOMSibling();
        assertTrue("Expected child element", next instanceof OMElement);
        next = next.getNextOMSibling();
        assertTrue("Expected child element", next instanceof OMElement);
        child = (OMElement)next;
        assertEquals("Child element name", "book", child.getLocalName());
        assertEquals("Child element namespace",
            "http://www.sosnoski.com/uwjws/library", child.getNamespace().getNamespaceURI());
        assertEquals("Attribute value error", "xml",
            child.getAttributeValue(new QName("type")));
    }
    
    private static class TestDataSource implements OMDataSource {
        // The data source is a ByteArrayInputStream so that we can verify that the datasource 
        // is only accessed once.  Currently there is no way to identify a destructive vs. non-destructive OMDataSource.
        private final ByteArrayInputStream data;
        
        private TestDataSource(String data) {
            this.data = new ByteArrayInputStream(data.getBytes());
            this.data.mark(0);
        }

        /* (non-Javadoc)
         * @see org.apache.axiom.om.OMDataSource#serialize(java.io.OutputStream, org.apache.axiom.om.OMOutputFormat)
         */
        public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
            try {
                output.write(getBytes());
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }

        /* (non-Javadoc)
         * @see org.apache.axiom.om.OMDataSource#serialize(java.io.Writer, org.apache.axiom.om.OMOutputFormat)
         */
        public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
            try {
                writer.write(getString());
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
            return inputFactory.createXMLStreamReader(new StringReader(getString()));
        }
        
        private byte[] getBytes() throws XMLStreamException {
            try {
                // The data from the data source should only be accessed once
                //data.reset();
                byte[] rc = new byte[data.available()];
                data.read(rc);
                return rc;
            } catch (IOException io) {
                throw new XMLStreamException(io);
            }
        }
        
        private String getString() throws XMLStreamException {
            String text = new String(getBytes());
            return text;
        }
    }
}