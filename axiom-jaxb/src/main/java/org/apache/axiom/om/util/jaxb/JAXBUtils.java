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
package org.apache.axiom.om.util.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.XOPEncoded;

/** Contains utility methods that integrate JAXB with Axiom. */
public final class JAXBUtils {
    private JAXBUtils() {}

    /**
     * @deprecated Use {@link #unmarshal(OMContainer, JAXBContext, UnmarshallerConfigurator,
     *     boolean)} instead.
     */
    public static Object unmarshal(JAXBContext context, OMElement element, boolean cache)
            throws JAXBException {
        return unmarshal(element, context, null, cache);
    }

    /**
     * Unmarshall the information item using JAXB.
     *
     * @param container the document or element to unmarshall
     * @param context the JAXB context
     * @param configurator custom unmarshaller settings to apply; may be {@code null}
     * @param preserve specifies whether the content of the information item should be preserved
     * @return the unmarshalled object
     * @throws JAXBException if an error occurred while unmarshalling
     */
    public static Object unmarshal(
            OMContainer container,
            JAXBContext context,
            UnmarshallerConfigurator configurator,
            boolean preserve)
            throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        if (configurator != null) {
            configurator.configure(unmarshaller);
        }
        XOPEncoded<XMLStreamReader> xopEncodedStream =
                container.getXOPEncodedStreamReader(preserve);
        unmarshaller.setAttachmentUnmarshaller(
                new AttachmentUnmarshallerImpl(xopEncodedStream.getAttachmentAccessor()));
        return unmarshaller.unmarshal(xopEncodedStream.getRootPart());
    }

    /**
     * Unmarshall the information item using JAXB.
     *
     * @param container the document or element to unmarshall
     * @param context the JAXB context
     * @param configurator custom unmarshaller settings to apply; may be {@code null}
     * @param declaredType a JAXB mapped class to hold the XML data.
     * @param preserve specifies whether the content of the information item should be preserved
     * @return the unmarshalled object
     * @throws JAXBException if an error occurred while unmarshalling
     */
    public static <T> JAXBElement<T> unmarshal(
            OMContainer container,
            JAXBContext context,
            UnmarshallerConfigurator configurator,
            Class<T> declaredType,
            boolean preserve)
            throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        if (configurator != null) {
            configurator.configure(unmarshaller);
        }
        XOPEncoded<XMLStreamReader> xopEncodedStream =
                container.getXOPEncodedStreamReader(preserve);
        unmarshaller.setAttachmentUnmarshaller(
                new AttachmentUnmarshallerImpl(xopEncodedStream.getAttachmentAccessor()));
        return unmarshaller.unmarshal(xopEncodedStream.getRootPart(), declaredType);
    }
}
