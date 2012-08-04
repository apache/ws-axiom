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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.util.jaxb.UnmarshallerAdapter;

/**
 * Contains utility methods that integrate JAXB with Axiom.
 */
public final class JAXBUtils {
    private JAXBUtils() {}
    
    /**
     * Unmarshal the given element.
     * 
     * @param context
     *            the JAXB context
     * @param element
     *            the element to unmarshal
     * @param cache
     *            <code>true</code> if the element must be preserved; <code>false</code> if it may
     *            be consumed during unmarshalling
     * @return the JAXB object
     * @throws JAXBException
     *             if an error occurs while unmarshalling
     */
    public static Object unmarshal(JAXBContext context, OMElement element, boolean cache) throws JAXBException {
        UnmarshallerAdapter adapter = org.apache.axiom.util.jaxb.JAXBUtils.getUnmarshallerAdapter(
                element.getXMLStreamReader(cache));
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller(adapter.getAttachmentUnmarshaller());
        return unmarshaller.unmarshal(adapter.getReader());
    }
}
