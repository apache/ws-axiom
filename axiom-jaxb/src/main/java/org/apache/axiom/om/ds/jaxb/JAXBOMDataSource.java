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

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;

/**
 * {@link OMDataSource} backed by a JAXB object. This class can be used both for plain JAXB objects
 * and for {@link JAXBElement} instances. It implements {@link QNameAwareOMDataSource} so that it
 * can be used with {@link OMFactory#createOMElement(OMDataSource)}, i.e. it is not necessary to
 * supply the QName during construction of the {@link OMSourcedElement}. It also has full support
 * for XOP/MTOM. It is implemented as a push-style {@link OMDataSource} so that an {@link
 * OMSourcedElement} backed by an instance of this class can be expanded in an efficient way
 * (including the case where the JAXB object contains base64 binary data represented as {@link
 * DataHandler} instances or byte arrays).
 *
 * <p>The JAXB object encapsulated by an instance of this class can be retrieved using {@link
 * OMDataSourceExt#getObject()}. Note that modifying the JAXB object after passing it to the
 * constructor may result in unexpected behavior and should be avoided.
 *
 * <p>Instances of this class are non destructive, in the sense defined by {@link
 * OMDataSourceExt#isDestructiveWrite()}.
 */
public class JAXBOMDataSource extends AbstractPushOMDataSource implements QNameAwareOMDataSource {
    private final JAXBContext context;
    private final Object object;
    private QName cachedQName;

    /**
     * Constructor.
     *
     * @param context the JAXB context to which the object is known
     * @param object the JAXB object; this may be a plain Java bean or a {@link JAXBElement}
     */
    public JAXBOMDataSource(JAXBContext context, Object object) {
        this.context = context;
        this.object = object;
    }

    @Override
    public boolean isDestructiveWrite() {
        return false;
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            if (writer instanceof MTOMXMLStreamWriter) {
                MTOMXMLStreamWriter mtomWriter = (MTOMXMLStreamWriter) writer;
                if (mtomWriter.isOptimized()) {
                    marshaller.setAttachmentMarshaller(new AttachmentMarshallerImpl(mtomWriter));
                }
            }
            marshaller.marshal(object, writer);
        } catch (JAXBException ex) {
            // Try to propagate the original exception if possible (to avoid unreadable stacktraces)
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof XMLStreamException) {
                    throw (XMLStreamException) cause;
                }
                cause = cause.getCause();
            }
            throw new OMException("Error marshalling JAXB object", ex);
        }
    }

    private QName getQName() {
        if (cachedQName == null) {
            if (object instanceof JAXBElement) {
                cachedQName = ((JAXBElement<?>) object).getName();
            } else {
                cachedQName = context.createJAXBIntrospector().getElementName(object);
                if (cachedQName == null) {
                    // We get here if the class of the object is not known to
                    // the JAXBContext
                    throw new OMException("Unable to determine the element name of the object");
                }
            }
        }
        return cachedQName;
    }

    @Override
    public String getLocalName() {
        return getQName().getLocalPart();
    }

    @Override
    public String getNamespaceURI() {
        return getQName().getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public OMDataSourceExt copy() {
        return new JAXBOMDataSource(context, object);
    }
}
