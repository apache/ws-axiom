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

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMContainer;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Strategy interface for {@link WebServiceMessage#getPayloadSource()} and
 * {@link SoapBody#getPayloadSource()}.
 * <p>
 * Axiom supports several methods to transform an {@link OMContainer} object into a representation
 * based on a different API. E.g.:
 * <ul>
 * <li>{@link OMContainer#getXMLStreamReader(boolean)} can be used to get a StAX
 * {@link XMLStreamReader} from any {@link OMContainer} object.
 * <li>{@link OMContainer#getSAXSource(boolean)} can be used to transform an {@link OMContainer}
 * into a sequence of SAX events.
 * <li>An Axiom implementation can support DOM directly. If such an implementation is used, then an
 * {@link OMContainer} object can be processed using DOM code by simply casting it to the
 * corresponding DOM interface ({@link Document} or {@link Element}).
 * </ul>
 * In the first two cases, Axiom can also be instructed to skip building the Axiom object model and
 * instead retrieve the XML events directly from the underlying parser (unless the Axiom object
 * model has been built before).
 * <p>
 * It is clear that depending on the use case, the choice of the method to use may have significant
 * impact on performance. On the other hand, Spring-WS defines a single API to retrieve the message
 * payload, namely {@link WebServiceMessage#getPayloadSource()} (which delegates to
 * {@link SoapBody#getPayloadSource()} for SOAP messages). That method is expected to return a
 * {@link Source} object, but it neither allows the caller to specify a preference for the type of
 * {@link Source} object ({@link DOMSource}, {@link SAXSource}, etc.). nor to indicate whether the
 * payload may be consumed or needs to be preserved for subsequent calls to
 * {@link WebServiceMessage#getPayloadSource()}.
 * <p>
 * This interface allows to define specific strategies that
 * {@link WebServiceMessage#getPayloadSource()} can use to prepare the {@link Source} object for the
 * message payload. A particular strategy can then be temporarily associated with a given
 * {@link WebServiceMessage} using
 * {@link AxiomWebServiceMessage#pushPayloadAccessStrategy(PayloadAccessStrategy, Object)} and
 * {@link AxiomWebServiceMessage#popPayloadAccessStrategy(Object)}. Note that application code is
 * not expected to use these methods directly. Instead it should configure a
 * {@link AxiomOptimizationEnabler} bean to automatically associate strategies with particular bean
 * type.
 */
public interface PayloadAccessStrategy {
    /**
     * Payload access strategy that uses {@link OMContainer#getSAXSource(boolean)} with
     * <code>cache</code> set to <code>true</code>.
     */
    PayloadAccessStrategy SAX_PRESERVE = new PayloadAccessStrategy() {
        public Source getSource(OMContainer container) {
            return container.getSAXSource(true);
        }

        @Override
        public String toString() {
            return "SAX_PRESERVE";
        }
    };
    
    /**
     * Payload access strategy that uses {@link OMContainer#getSAXSource(boolean)} with
     * <code>cache</code> set to <code>false</code>.
     */
    PayloadAccessStrategy SAX_CONSUME = new PayloadAccessStrategy() {
        public Source getSource(OMContainer container) {
            return container.getSAXSource(false);
        }

        @Override
        public String toString() {
            return "SAX_CONSUME";
        }
    };
    
    /**
     * The default payload access strategy, {@link #SAX_PRESERVE}.
     * {@link WebServiceMessage#getPayloadSource()} uses this default strategy if no strategy has
     * been set explicitly using
     * {@link AxiomWebServiceMessage#pushPayloadAccessStrategy(PayloadAccessStrategy, Object)}.
     */
    PayloadAccessStrategy DEFAULT = SAX_PRESERVE;

    /**
     * Create a {@link Source} object for the given {@link OMContainer}.
     * 
     * @param container
     *            the {@link OMDocument} or {@link OMElement}
     * @return the corresponding {@link Source} object
     */
    Source getSource(OMContainer container);
}
