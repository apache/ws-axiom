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
package org.apache.axiom.ts.om.sourcedelement.push;

import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.testutils.suite.Dimension;

/**
 * Describes a usage scenario for {@link AbstractPushOMDataSource}. An implementation of this
 * interface has the capability of writing a certain sequence of StAX events to an {@link
 * XMLStreamWriter} and to validate the OM tree built from that sequence of events.
 */
public interface PushOMDataSourceScenario extends Dimension {
    PushOMDataSourceScenario[] INSTANCES = {
        new CloseScenario(),
        new GetNamespaceContextScenario(),
        new WriteAttributeAutoPrefixScenario(),
        new WriteAttributeNamespaceUnawareScenario(),
        new WriteCDataScenario(),
        new WriteCharactersCharArrayScenario(),
        new WriteCommentScenario(),
        new WriteDataHandlerScenario(),
        new WriteDataHandlerProviderScenario(),
        new WriteEmptyElementScenario(),
        new WriteEmptyElementAutoPrefixScenario(),
        new WriteEmptyElementNamespaceUnawareScenario(),
        new WriteEntityRefScenario(),
        new WriteNamespaceScenario("", ""),
        new WriteNamespaceScenario("", "urn:test"),
        new WriteNamespaceScenario("p", "urn:test"),
        new WriteProcessingInstruction1Scenario(),
        new WriteProcessingInstruction2Scenario(),
        new WriteStartElementAutoPrefixScenario(),
        new WriteStartElementNamespaceUnawareScenario(),
        new WriteStartElementWithDefaultNamespaceDeclaredOnParentScenario(),
        new WriteStartEndDocumentScenario(),
    };

    /**
     * Get a map with namespace bindings that this scenario expects to exist on the parent element.
     *
     * @return the namespace context
     */
    Map<String, String> getNamespaceContext();

    /**
     * Write the StAX events to the given {@link XMLStreamWriter}.
     *
     * @param writer the {@link XMLStreamWriter}
     * @throws XMLStreamException
     */
    void serialize(XMLStreamWriter writer) throws XMLStreamException;

    /**
     * Validate the resulting OM tree.
     *
     * @param element the {@link OMElement} representing the XML data produced by {@link
     *     #serialize(XMLStreamWriter)}
     * @param dataHandlersPreserved <code>true</code> if {@link DataHandler} objects written to the
     *     {@link XMLStreamWriter} are expected to have been preserved as is; <code>false</code> if
     *     they are expected to have been transformed to base64 encoded character data
     * @throws Throwable
     */
    void validate(OMElement element, boolean dataHandlersPreserved) throws Throwable;
}
