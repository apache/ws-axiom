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

package org.apache.axiom.soap.impl.common.builder;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.stream.FilteredXmlInput;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.common.builder.BuilderImpl;
import org.apache.axiom.om.impl.common.builder.BuilderListener;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;

/**
 * Internal implementation class.
 */
public class SOAPModelBuilderImpl extends BuilderImpl implements SOAPModelBuilder {
    public SOAPModelBuilderImpl(XmlInput input, NodeFactory nodeFactory, boolean repairNamespaces, Detachable detachable) {
        super(new FilteredXmlInput(input, SOAPFilter.INSTANCE), nodeFactory, new SOAPModel(), null, true, detachable);
        // The SOAPFactory instance linked to the SOAPMessage is unknown until we reach the
        // SOAPEnvelope. Register a post-processor that does the necessary updates on the
        // SOAPMessage.
        addListener(new BuilderListener() {
            private AxiomSOAPMessage message;
            
            @Override
            public Runnable nodeAdded(CoreNode node, int depth) {
                if (node instanceof AxiomSOAPMessage) {
                    message = (AxiomSOAPMessage)node;
                } else if (message != null && node instanceof AxiomSOAPEnvelope) {
                    message.initSOAPFactory((SOAPFactory)((AxiomSOAPEnvelope)node).getOMFactory());
                }
                return null;
            }
        });
    }
    
    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        return (SOAPEnvelope)getDocumentElement();
    }

    public SOAPMessage getSOAPMessage() {
        return (SOAPMessage)getDocument();
    }
}
