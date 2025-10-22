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
package org.apache.axiom.om;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;

/**
 * Represents an information item in an XML document. This is the super-interface for all
 * information items known by Axiom.
 */
public interface OMInformationItem {
    /**
     * Get the {@link OMFactory} corresponding to the type of this information item. For information
     * items created using one of the methods defined by {@link OMFactory}, this is the instance
     * returned by {@link OMMetaFactory#getOMFactory()} (for the {@link OMMetaFactory} corresponding
     * to the implementation of the Axiom API this information item belongs to). For information
     * items created by one of the methods defined by {@link SOAPFactory}, this is the {@link
     * SOAPFactory} instance for the corresponding SOAP version.
     *
     * <p>This means that the returned factory will be a {@link SOAPFactory} only if the method is
     * called on a {@link SOAPMessage} or an {@link OMElement} that implements one of the SOAP
     * specific extension interfaces.
     *
     * @return the {@link OMFactory} corresponding to this information item
     */
    OMFactory getOMFactory();

    /**
     * Clone this information item. If the information item is a container, then its descendants
     * will be cloned recursively. Note that in this case, this method will traverse the descendants
     * and create clones immediately. It will also preserve the original nodes. This means that
     * after the execution of this method, both the returned clone and the original container will
     * be completely built.
     *
     * @param options the options to use when cloning this element and its descendants; for object
     *     models with domain specific extensions (such as SOAP), this may be a subclass of {@link
     *     OMCloneOptions}
     * @return the cloned element
     */
    OMInformationItem clone(OMCloneOptions options);
}
