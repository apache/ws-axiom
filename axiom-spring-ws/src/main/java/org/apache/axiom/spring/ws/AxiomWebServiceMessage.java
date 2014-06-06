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
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapElement;
import org.springframework.ws.stream.StreamingWebServiceMessage;

/**
 * Interface implemented by {@link WebServiceMessage} instances created by
 * {@link AxiomSoapMessageFactory}.
 */
public interface AxiomWebServiceMessage extends StreamingWebServiceMessage {
    /**
     * Returns the root qualified name of the payload of this message. The return value is the same
     * as that of {@link PayloadRootUtils#getPayloadRootQName(Source, TransformerFactory)} when
     * invoked with the {@link Source} object returned by
     * {@link WebServiceMessage#getPayloadSource()}, but the implementation is more efficient.
     * 
     * @return the qualified name of they payload root element
     */
    QName getPayloadRootQName();
    
    /**
     * Set the extraction strategy used in subsequent calls to
     * {@link WebServiceMessage#getPayloadSource()}, {@link SoapBody#getPayloadSource()} and
     * {@link SoapElement#getSource()}. The strategy is pushed to a stack and will be in effect as
     * long as it is on top of the stack, i.e. until the next call to
     * {@link #pushSourceExtractionStrategy(SourceExtractionStrategy, Object)} or
     * {@link #popSourceExtractionStrategy(Object)}.
     * <p>
     * Note: this method is used internally; it is not expected to be called by application code.
     * 
     * @param strategy
     *            the strategy
     * @param bean
     *            the bean on behalf of which the strategy is configured; this information is only
     *            used for logging and to detect missing or unexpected calls to
     *            {@link #popSourceExtractionStrategy(Object)}
     */
    void pushSourceExtractionStrategy(SourceExtractionStrategy strategy, Object bean);
    
    /**
     * Restore the previous extraction strategy. This method removes the top of the stack so that
     * the previous strategy again comes into effect.
     * 
     * @param bean
     *            the bean corresponding to the current extraction strategy (i.e. the strategy on
     *            top of the stack); must match the reference passed to the corresponding call to
     *            {@link #pushSourceExtractionStrategy(SourceExtractionStrategy, Object)}.
     * @throws IllegalStateException
     *             if the stack is empty or if the caller didn't pass the expected bean
     */
    void popSourceExtractionStrategy(Object bean);
}
