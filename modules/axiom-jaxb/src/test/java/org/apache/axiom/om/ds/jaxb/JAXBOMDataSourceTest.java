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
package org.apache.axiom.om.ds.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.ds.jaxb.beans.DocumentBean;
import org.apache.axiom.om.ds.jaxb.beans.MyBean;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.commons.io.IOUtils;
import org.example.identity.LinkIdentitiesType;
import org.example.identity.ObjectFactory;
import org.junit.Test;

public class JAXBOMDataSourceTest {
    /**
     * Tests the expansion of an {@link OMSourcedElement} backed by a {@link JAXBOMDataSource} with
     * a bean that has a {@link DataHandler}. The expansion should result in an {@link OMText} node
     * linked to that {@link DataHandler}.
     */
    @Test
    public void testDataHandlerExpansion() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DataHandler dh = new DataHandler("some content", "text/plain");
        DocumentBean object = new DocumentBean();
        object.setId("123456");
        object.setContent(dh);
        OMElement element = factory.createOMElement(new JAXBOMDataSource(context, object));
        OMElement child = (OMElement)element.getFirstOMChild();
        assertEquals("id", child.getLocalName());
        assertEquals("123456", child.getText());
        child = (OMElement)child.getNextOMSibling();
        assertEquals("content", child.getLocalName());
        OMText content = (OMText)child.getFirstOMChild();
        assertTrue(content.isBinary());
        assertTrue(content.isOptimized());
        assertSame(dh, content.getDataHandler());
    }
    
    /**
     * Tests that an {@link OMSourcedElement} backed by a {@link JAXBOMDataSource} with a bean
     * containing a {@link DataHandler} is correctly serialized.
     */
    @Test
    public void testDataHandlerSerializationWithoutMTOM() throws Exception{
        SOAPFactory factory = OMAbstractFactory.getSOAP11Factory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        
        // Construct the original message
        DocumentBean orgObject = new DocumentBean();
        orgObject.setId("123456");
        orgObject.setContent(new DataHandler("some content", "text/plain; charset=utf-8"));
        SOAPEnvelope orgEnvelope = factory.getDefaultEnvelope();
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, orgObject));
        orgEnvelope.getBody().addChild(element);
        
        // Serialize the message
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        orgEnvelope.serialize(out);
        assertFalse(element.isExpanded());
        
        SOAPEnvelope envelope = OMXMLBuilderFactory.createSOAPModelBuilder(
                new ByteArrayInputStream(out.toByteArray()), null).getSOAPEnvelope();
        DocumentBean object = (DocumentBean)context.createUnmarshaller().unmarshal(
                envelope.getBody().getFirstElement().getXMLStreamReader(false));
        assertEquals("some content", IOUtils.toString(object.getContent().getInputStream(), "utf-8"));
    }
    
    /**
     * Tests that an {@link OMSourcedElement} backed by a {@link JAXBOMDataSource} with a bean
     * containing a {@link DataHandler} is correctly serialized to MTOM.
     */
    @Test
    public void testDataHandlerSerializationWithMTOM() throws Exception {
        SOAPFactory factory = OMAbstractFactory.getSOAP11Factory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        
        // Construct the original message
        DocumentBean object = new DocumentBean();
        object.setId("123456");
        object.setContent(new DataHandler("some content", "text/plain; charset=utf-8"));
        SOAPEnvelope orgEnvelope = factory.getDefaultEnvelope();
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, object));
        orgEnvelope.getBody().addChild(element);
        
        // Serialize the message
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        orgEnvelope.serialize(out, format);
        assertFalse(element.isExpanded());
        
        // Parse the serialized message
        Attachments att = new Attachments(new ByteArrayInputStream(out.toByteArray()), format.getContentType());
        assertEquals(2, att.getAllContentIDs().length);
        SOAPEnvelope envelope = OMXMLBuilderFactory.createSOAPModelBuilder(att).getSOAPEnvelope();
        OMElement contentElement = envelope.getBody().getFirstElement().getFirstChildWithName(
                new QName("http://ws.apache.org/axiom/test/jaxb", "content"));
        OMText content = (OMText)contentElement.getFirstOMChild();
        assertTrue(content.isBinary());
        assertTrue(content.isOptimized());
        DataHandler dh = (DataHandler)content.getDataHandler();
        assertEquals("some content", dh.getContent());
    }
    
    /**
     * Tests serialization using {@link OMDataSource#serialize(XMLStreamWriter)} to a standard
     * {@link XMLStreamWriter}.
     */
    @Test
    public void testSerializeDirect() throws Exception {
        JAXBContext context = JAXBContext.newInstance(MyBean.class);
        MyBean orgBean = new MyBean(3, 5);
        JAXBOMDataSource ds = new JAXBOMDataSource(context, orgBean);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
        ds.serialize(writer);
        writer.close();
        MyBean bean = (MyBean)context.createUnmarshaller().unmarshal(
                new ByteArrayInputStream(out.toByteArray()));
        assertEquals(orgBean.getA(), bean.getA());
        assertEquals(orgBean.getB(), bean.getB());
    }
    
    /**
     * Tests that {@link JAXBOMDataSource} backed by a plain Java bean is able to determine the
     * namespace URI and local name of the element without expansion.
     */
    @Test
    public void testGetNameFromPlainObject() throws Exception {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        OMSourcedElement element = omFactory.createOMElement(new JAXBOMDataSource(context, new DocumentBean()));
        assertEquals("http://ws.apache.org/axiom/test/jaxb", element.getNamespaceURI());
        assertEquals("document", element.getLocalName());
        assertFalse(element.isExpanded());
        // Force expansion so that OMSourcedElement compares the namespace URI and local name
        // provided by JAXBOMDataSource with the actual name of the element
        element.getFirstOMChild();
    }
    
    /**
     * Tests that {@link JAXBOMDataSource} backed by a {@link JAXBElement} is able to determine the
     * namespace URI and local name of the element without expansion.
     */
    @Test
    public void testGetNameFromJAXBElement() throws Exception {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        JAXBElement<LinkIdentitiesType> jaxbElement = objectFactory.createLinkIdentities(new LinkIdentitiesType());
        OMSourcedElement element = omFactory.createOMElement(new JAXBOMDataSource(context, jaxbElement));
        assertEquals("http://www.example.org/identity", element.getNamespaceURI());
        assertEquals("LinkIdentities", element.getLocalName());
        assertFalse(element.isExpanded());
        // Force expansion so that OMSourcedElement compares the namespace URI and local name
        // provided by JAXBOMDataSource with the actual name of the element
        element.getFirstOMChild();
    }
    
    /**
     * Tests that an {@link XMLStreamException} thrown by the {@link XMLStreamWriter} during
     * serialization is propagated without being wrapped. Note that this implies that the data must
     * unwrap {@link JAXBException} to extract the cause.
     */
    @Test
    public void testExceptionDuringSerialization() throws Exception {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DocumentBean object = new DocumentBean();
        object.setId("test");
        OMSourcedElement element = omFactory.createOMElement(new JAXBOMDataSource(context, object));
        XMLStreamException exception = new XMLStreamException("TEST");
        try {
            element.serialize(new ExceptionXMLStreamWriterWrapper(StAXUtils.createXMLStreamWriter(new ByteArrayOutputStream()), exception));
            fail("Expected XMLStreamException");
        } catch (XMLStreamException ex) {
            assertSame(exception, ex);
        }
    }
}
