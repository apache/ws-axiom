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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.core.stream.stax.StAX;
import org.apache.axiom.jaxb.DocumentBean;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.testutils.blob.TextBlob;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.example.identity.LinkIdentitiesType;
import org.example.identity.ObjectFactory;

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
        OMElement child = (OMElement) element.getFirstOMChild();
        assertThat(child.getLocalName()).isEqualTo("id");
        assertThat(child.getText()).isEqualTo("123456");
        child = (OMElement) child.getNextOMSibling();
        assertThat(child.getLocalName()).isEqualTo("content");
        OMText content = (OMText) child.getFirstOMChild();
        assertThat(content.isBinary()).isTrue();
        assertThat(content.isOptimized()).isTrue();
        assertThat(DataHandlerUtils.toDataHandler(content.getBlob())).isSameAs(dh);
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
        object.setContent(
                DataHandlerUtils.toDataHandler(
                        new TextBlob("some content", StandardCharsets.UTF_8)));
        SOAPEnvelope orgEnvelope = factory.getDefaultEnvelope();
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, object));
        orgEnvelope.getBody().addChild(element);

        // Serialize the message
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        MemoryBlob mtom = Blobs.createMemoryBlob();
        OutputStream out = mtom.getOutputStream();
        orgEnvelope.serialize(out, format);
        out.close();
        assertThat(element.isExpanded()).isFalse();

        // Parse the serialized message
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(mtom.getInputStream())
                        .setContentType(format.getContentType())
                        .build();
        assertThat(mb.getPartCount()).isEqualTo(2);
        SOAPEnvelope envelope = OMXMLBuilderFactory.createSOAPModelBuilder(mb).getSOAPEnvelope();
        OMElement contentElement =
                envelope.getBody()
                        .getFirstElement()
                        .getFirstChildWithName(
                                new QName("http://ws.apache.org/axiom/test/jaxb", "content"));
        OMText content = (OMText) contentElement.getFirstOMChild();
        assertThat(content.isBinary()).isTrue();
        assertThat(content.isOptimized()).isTrue();
        Blob blob = content.getBlob();
        assertThat(IOUtils.toString(blob.getInputStream(), StandardCharsets.UTF_8))
                .isEqualTo("some content");
    }

    /**
     * Tests that an {@link OMSourcedElement} backed by a {@link JAXBOMDataSource} with a bean
     * containing a {@link DataHandler} is correctly serialized.
     */
    @Test
    public void testDataHandlerSerializationWithoutMTOM() throws Exception {
        SOAPFactory factory = OMAbstractFactory.getSOAP11Factory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);

        // Construct the original message
        DocumentBean orgObject = new DocumentBean();
        orgObject.setId("123456");
        orgObject.setContent(
                DataHandlerUtils.toDataHandler(
                        new TextBlob("some content", StandardCharsets.UTF_8)));
        SOAPEnvelope orgEnvelope = factory.getDefaultEnvelope();
        OMSourcedElement element =
                factory.createOMElement(new JAXBOMDataSource(context, orgObject));
        orgEnvelope.getBody().addChild(element);

        // Serialize the message
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        orgEnvelope.serialize(out);
        assertThat(element.isExpanded()).isFalse();

        SOAPEnvelope envelope =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                                new ByteArrayInputStream(out.toByteArray()), null)
                        .getSOAPEnvelope();
        DocumentBean object =
                (DocumentBean)
                        context.createUnmarshaller()
                                .unmarshal(
                                        envelope.getBody()
                                                .getFirstElement()
                                                .getXMLStreamReader(false));
        assertThat(IOUtils.toString(object.getContent().getInputStream(), StandardCharsets.UTF_8))
                .isEqualTo("some content");
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
            element.serialize(
                    new ExceptionXMLStreamWriterWrapper(
                            StAX.createNullXMLStreamWriter(), exception));
            fail("Expected XMLStreamException");
        } catch (XMLStreamException ex) {
            assertThat(ex).isSameAs(exception);
        }
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
        JAXBElement<LinkIdentitiesType> jaxbElement =
                objectFactory.createLinkIdentities(new LinkIdentitiesType());
        OMSourcedElement element =
                omFactory.createOMElement(new JAXBOMDataSource(context, jaxbElement));
        assertThat(element.getNamespaceURI()).isEqualTo("http://www.example.org/identity");
        assertThat(element.getLocalName()).isEqualTo("LinkIdentities");
        assertThat(element.isExpanded()).isFalse();
        // Force expansion so that OMSourcedElement compares the namespace URI and local name
        // provided by JAXBOMDataSource with the actual name of the element
        element.getFirstOMChild();
    }

    /**
     * Tests that {@link JAXBOMDataSource} backed by a plain Java bean is able to determine the
     * namespace URI and local name of the element without expansion.
     */
    @Test
    public void TestGetNameFromPlainObject() throws Exception {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        OMSourcedElement element =
                omFactory.createOMElement(new JAXBOMDataSource(context, new DocumentBean()));
        assertThat(element.getNamespaceURI()).isEqualTo("http://ws.apache.org/axiom/test/jaxb");
        assertThat(element.getLocalName()).isEqualTo("document");
        assertThat(element.isExpanded()).isFalse();
        // Force expansion so that OMSourcedElement compares the namespace URI and local name
        // provided by JAXBOMDataSource with the actual name of the element
        element.getFirstOMChild();
    }
}
