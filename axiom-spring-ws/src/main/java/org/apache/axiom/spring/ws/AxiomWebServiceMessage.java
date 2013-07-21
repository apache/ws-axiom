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
package org.apache.axiom.spring.ws;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.apache.axiom.spring.ws.soap.AxiomSoapMessageFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.server.endpoint.support.PayloadRootUtils;

/**
 * Interface implemented by {@link WebServiceMessage} instances created by
 * {@link AxiomSoapMessageFactory}.
 */
public interface AxiomWebServiceMessage extends WebServiceMessage {
    /**
     * Returns the root qualified name of the payload of this message. The return value is the same
     * as that of {@link PayloadRootUtils#getPayloadRootQName(Source, TransformerFactory)} when
     * invoked with the {@link Source} object returned by
     * {@link WebServiceMessage#getPayloadSource()}, but the implementation is more efficient.
     * 
     * @return the qualified name of they payload root element
     */
    QName getPayloadRootQName();
}
