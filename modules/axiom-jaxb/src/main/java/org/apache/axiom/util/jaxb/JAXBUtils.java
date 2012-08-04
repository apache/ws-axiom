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
import org.apache.axiom.util.stax.xop.XOPEncodedStream;
import org.apache.axiom.util.stax.xop.XOPUtils;

/**
 * Contains low level utility methods that integrate JAXB with some of the Axiom specific StAX
 * extensions.
 */
public final class JAXBUtils {
    private JAXBUtils() {}
    
    /**
     * Create an adapter that enables an {@link Unmarshaller} to perform XOP/MTOM processing on an
     * {@link XMLStreamReader} that exposes the {@link DataHandlerReader} extension. This method
     * should be used in the following way:
     * <ol>
     * <li>Create the {@link Unmarshaller}.
     * <li>Call this method with the {@link XMLStreamReader} to be unmarshalled.
     * <li>Configure the {@link Unmarshaller} with the {@link AttachmentUnmarshaller} returned by
     * {@link UnmarshallerAdapter#getAttachmentUnmarshaller()}.
     * <li>Call {@link Unmarshaller#unmarshal(XMLStreamReader)} or
     * {@link Unmarshaller#unmarshal(XMLStreamReader, Class)}, passing the {@link XMLStreamReader}
     * returned by {@link UnmarshallerAdapter#getReader()} as parameter.
     * </ol>
     * 
     * @param reader
     *            an {@link XMLStreamReader} that may expose the {@link DataHandlerReader} extension
     * @return the adapter
     */
    public static UnmarshallerAdapter getUnmarshallerAdapter(XMLStreamReader reader) {
        XOPEncodedStream stream = XOPUtils.getXOPEncodedStream(reader);
        XMLStreamReader xopEncodedReader = stream.getReader();
        if (xopEncodedReader == reader) {
            return new UnmarshallerAdapter(reader, null);
        } else {
            return new UnmarshallerAdapter(xopEncodedReader, new AttachmentUnmarshallerImpl(stream.getMimePartProvider()));
        }
    }
}
