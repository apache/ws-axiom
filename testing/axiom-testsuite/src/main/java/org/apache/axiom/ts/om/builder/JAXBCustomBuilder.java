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

package org.apache.axiom.ts.om.builder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.XOPEncoded;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;

public class JAXBCustomBuilder implements CustomBuilder {
    private final JAXBContext jaxbContext;
    private Object jaxbObject;
    private boolean attachmentsAccessed;
    
    public JAXBCustomBuilder(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    @Override
    public OMDataSource create(OMElement element) throws OMException {
        try {
            XOPEncoded<XMLStreamReader> xopStream = element.getXOPEncodedStreamReader(false);
            XMLStreamReader reader = xopStream.getRootPart();
            reader.next();
            final String namespaceURI = reader.getNamespaceURI();
            final String localName = reader.getLocalName();
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            AttachmentUnmarshallerImpl attachmentUnmarshaller = new AttachmentUnmarshallerImpl(xopStream.getMimePartProvider());
            unmarshaller.setAttachmentUnmarshaller(attachmentUnmarshaller);
            // For the purpose of the test we just store the JAXB object and return
            // a dummy OMDataSource.
            jaxbObject = unmarshaller.unmarshal(reader);
            reader.close();
            attachmentsAccessed = attachmentUnmarshaller.isAccessed();
            return new AbstractPushOMDataSource() {
                @Override
                public boolean isDestructiveWrite() {
                    return false;
                }

                @Override
                public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
                    xmlWriter.writeEmptyElement("ns1", localName, namespaceURI);
                }
            };
        } catch (JAXBException ex) {
            throw new OMException(ex);
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    public Object getJaxbObject() {
        return jaxbObject;
    }

    public boolean isAttachmentsAccessed() {
        return attachmentsAccessed;
    }
}
