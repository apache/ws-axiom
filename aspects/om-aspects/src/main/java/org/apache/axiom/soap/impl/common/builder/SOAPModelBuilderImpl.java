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

import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.builder.Detachable;
import org.apache.axiom.om.impl.common.builder.OMXMLParserWrapperImpl;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;

/**
 * Internal implementation class.
 */
public class SOAPModelBuilderImpl extends OMXMLParserWrapperImpl implements SOAPModelBuilder {
    public SOAPModelBuilderImpl(BuilderImpl builder, Detachable detachable) {
        super(builder, detachable);
    }
    
    @Override
    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        return (SOAPEnvelope)getDocumentElement();
    }

    @Override
    public SOAPMessage getSOAPMessage() {
        return (SOAPMessage)getDocument();
    }
}
