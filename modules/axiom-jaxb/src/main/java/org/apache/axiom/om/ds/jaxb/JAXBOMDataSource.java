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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamWriter;

public class JAXBOMDataSource extends AbstractPushOMDataSource implements QNameAwareOMDataSource {
    private final JAXBContext context;
    private final Object object;
    private QName cachedQName;
    
    public JAXBOMDataSource(JAXBContext context, Object object) {
        this.context = context;
        this.object = object;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            if (writer instanceof MTOMXMLStreamWriter) {
                MTOMXMLStreamWriter mtomWriter = (MTOMXMLStreamWriter)writer;
                // TODO: usage of deprecated method here
                if (mtomWriter.isOptimized()) {
                    marshaller.setAttachmentMarshaller(new MTOMXMLStreamWriterAttachmentMarshaller(mtomWriter));
                }
            } else {
                DataHandlerWriter dataHandlerWriter; 
                try {
                    dataHandlerWriter = (DataHandlerWriter)writer.getProperty(DataHandlerWriter.PROPERTY);
                } catch (IllegalArgumentException ex) {
                    dataHandlerWriter = null;
                }
                if (dataHandlerWriter != null) {
                    DataHandlerWriterAttachmentMarshaller am = new DataHandlerWriterAttachmentMarshaller();
                    writer = new XOPDecodingStreamWriter(writer, am);
                    marshaller.setAttachmentMarshaller(am);
                }
            }
            marshaller.marshal(object, writer);
        } catch (JAXBException ex) {
            throw new XMLStreamException("Error marshalling JAXB object", ex);
        }
    }

    private QName getQName() {
        if (cachedQName == null) {
            if (object instanceof JAXBElement) {
                cachedQName = ((JAXBElement<?>)object).getName();
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
    
    public String getLocalName() {
        return getQName().getLocalPart();
    }

    public String getNamespaceURI() {
        return getQName().getNamespaceURI();
    }

    public String getPrefix() {
        return null;
    }
}
