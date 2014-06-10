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
import javax.xml.transform.stax.StAXSource;

import org.apache.axiom.om.OMContainer;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Strategy interface for {@link WebServiceMessage#getPayloadSource()},
 * {@link SoapBody#getPayloadSource()} and {@link SoapElement#getSource()}.
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
 * impact on performance. On the other hand, Spring-WS defines only very simple APIs to retrieve the
 * message payload ({@link WebServiceMessage#getPayloadSource()}, which delegates to
 * {@link SoapBody#getPayloadSource()} for SOAP messages) or part of a SOAP message (
 * {@link SoapElement#getSource()}). These methods are expected to return {@link Source} objects,
 * but they neither allow the caller to specify a preference for the type of {@link Source} object
 * ({@link DOMSource}, {@link SAXSource}, etc.). nor to indicate whether the requested part of the
 * message may be consumed or needs to be preserved for later.
 * <p>
 * This interface allows to define specific strategies that
 * {@link WebServiceMessage#getPayloadSource()}, {@link SoapBody#getPayloadSource()} and
 * {@link SoapElement#getSource()} can use to prepare the requested {@link Source} object. A
 * particular strategy can then be temporarily associated with a given {@link WebServiceMessage}
 * using
 * {@link AxiomWebServiceMessage#pushSourceExtractionStrategy(SourceExtractionStrategy, Object)} and
 * {@link AxiomWebServiceMessage#popSourceExtractionStrategy(Object)}. Note that application code is
 * not expected to use these methods directly. Instead it should configure a
 * {@link AxiomOptimizationEnabler} bean to automatically associate strategies with particular bean
 * type.
 */
public interface SourceExtractionStrategy {
    /**
     * Pseudo extraction strategy that will throw an exception if the payload source is requested.
     * This is for use with beans that are not expected to access the payload.
     */
    SourceExtractionStrategy NONE = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "NONE";
        }
    };
    
    /**
     * Extraction strategy that creates a {@link StAXSource} using
     * {@link OMContainer#getXMLStreamReader(boolean) with <code>cache</code> set to
     * <code>true</code>.
     */
    SourceExtractionStrategy STAX_PRESERVE = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            return new StAXSource(container.getXMLStreamReader(true));
        }

        @Override
        public String toString() {
            return "STAX_PRESERVE";
        }
    };
    
    /**
     * Extraction strategy that creates a {@link StAXSource} using
     * {@link OMContainer#getXMLStreamReader(boolean) with <code>cache</code> set to
     * <code>false</code>.
     */
    SourceExtractionStrategy STAX_CONSUME = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            return new StAXSource(container.getXMLStreamReader(false));
        }

        @Override
        public String toString() {
            return "STAX_CONSUME";
        }
    };

    /**
     * Extraction strategy that uses {@link OMContainer#getSAXSource(boolean)} with
     * <code>cache</code> set to <code>true</code>.
     */
    SourceExtractionStrategy SAX_PRESERVE = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            return container.getSAXSource(true);
        }

        @Override
        public String toString() {
            return "SAX_PRESERVE";
        }
    };
    
    /**
     * Extraction strategy that uses {@link OMContainer#getSAXSource(boolean)} with
     * <code>cache</code> set to <code>false</code>.
     */
    SourceExtractionStrategy SAX_CONSUME = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            return container.getSAXSource(false);
        }

        @Override
        public String toString() {
            return "SAX_CONSUME";
        }
    };
    
    /**
     * Extraction strategy that creates a {@link DOMSource} if the Axiom implementation supports DOM
     * and falls back to {@link #SAX_PRESERVE} otherwise.
     */
    SourceExtractionStrategy DOM_OR_SAX_PRESERVE = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            if (container instanceof Node) {
                return new DOMSource((Node)container);
            } else {
                return SAX_PRESERVE.getSource(container);
            }
        }

        @Override
        public String toString() {
            return "DOM_OR_SAX_PRESERVE";
        }
    };
    
    /**
     * Extraction strategy that creates a {@link DOMSource} if the Axiom implementation supports DOM
     * and falls back to {@link #SAX_CONSUME} otherwise.
     */
    SourceExtractionStrategy DOM_OR_SAX_CONSUME = new SourceExtractionStrategy() {
        public Source getSource(OMContainer container) {
            if (container instanceof Node) {
                return new DOMSource((Node)container);
            } else {
                return SAX_CONSUME.getSource(container);
            }
        }

        @Override
        public String toString() {
            return "DOM_OR_SAX_CONSUME";
        }
    };
    
    /**
     * The default extraction strategy, {@link #SAX_PRESERVE}.
     * {@link WebServiceMessage#getPayloadSource()}, {@link SoapBody#getPayloadSource()} and
     * {@link SoapElement#getSource()} use this default strategy if no strategy has been set
     * explicitly using
     * {@link AxiomWebServiceMessage#pushSourceExtractionStrategy(SourceExtractionStrategy, Object)}.
     */
    SourceExtractionStrategy DEFAULT = SAX_PRESERVE;

    /**
     * Create a {@link Source} object for the given {@link OMContainer}.
     * 
     * @param container
     *            the {@link OMDocument} or {@link OMElement}
     * @return the corresponding {@link Source} object
     */
    Source getSource(OMContainer container);
}
