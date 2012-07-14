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
package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;

/**
 * Interface that is used internally by Axiom and that should not be considered being part of the
 * public API.
 */
public interface OMMetaFactoryEx extends OMMetaFactory {
    /**
     * Create a {@link SOAPMessage} not linked to a particular {@link SOAPFactory}. The returned
     * {@link SOAPMessage} instance is expected to resolve the {@link SOAPFactory} lazily using a
     * call to {@link StAXSOAPModelBuilder#getSOAPFactory()}.
     * 
     * @param builder
     *            the builder
     * @return the newly created {@link SOAPMessage} instance
     */
    SOAPMessage createSOAPMessage(OMXMLParserWrapper builder);
}
