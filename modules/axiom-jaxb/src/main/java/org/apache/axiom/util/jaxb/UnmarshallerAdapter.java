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
package org.apache.axiom.util.jaxb;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;

/**
 * Adapter that enables an {@link Unmarshaller} to perform XOP/MTOM processing on an
 * {@link XMLStreamReader} that exposes the {@link DataHandlerReader} extension.
 * 
 * @see JAXBUtils#getUnmarshallerAdapter(XMLStreamReader)
 */
public class UnmarshallerAdapter {
    private final XMLStreamReader reader;
    private final AttachmentUnmarshaller attachmentUnmarshaller;
    
    UnmarshallerAdapter(XMLStreamReader reader, AttachmentUnmarshaller attachmentUnmarshaller) {
        this.reader = reader;
        this.attachmentUnmarshaller = attachmentUnmarshaller;
    }

    /**
     * Get the {@link XMLStreamReader} to be passed to the {@link Unmarshaller}.
     * 
     * @return the reader
     */
    public XMLStreamReader getReader() {
        return reader;
    }

    /**
     * Get the {@link AttachmentUnmarshaller} to be configured on the {@link Unmarshaller}.
     * 
     * @return the {@link AttachmentUnmarshaller} instance; this may be <code>null</code> if the
     *         underlying {@link XMLStreamReader} doesn't expose the {@link DataHandlerReader}
     *         extension
     */
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return attachmentUnmarshaller;
    }
}
