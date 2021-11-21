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
package org.apache.axiom.om.impl.common.builder;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder.Selector;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.intf.AxiomDocument;

public class OMXMLParserWrapperImpl implements OMXMLParserWrapper, CustomBuilderSupport {
    private final BuilderImpl builder;
    private final Detachable detachable;
    private final CustomBuilderManager customBuilderManager = new CustomBuilderManager();

    public OMXMLParserWrapperImpl(BuilderImpl builder, Detachable detachable) {
        this.builder = builder;
        this.detachable = detachable;
        builder.setFacade(this);
        builder.addListener(customBuilderManager);
    }

    @Override
    public final void registerCustomBuilder(Selector selector, CustomBuilder customBuilder) {
        customBuilderManager.register(selector, customBuilder);
    }
    
    @Override
    public final boolean isCompleted() {
        return builder.isCompleted();
    }

    @Override
    public final OMDocument getDocument() {
        try {
            return (AxiomDocument)builder.getDocument();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    @Override
    public final OMElement getDocumentElement() {
        return getDocumentElement(false);
    }

    @Override
    public final OMElement getDocumentElement(boolean discardDocument) {
        try {
            OMDocument document = getDocument();
            OMElement element = document.getOMDocumentElement();
            if (discardDocument) {
                element.detach();
                ((AxiomDocument)document).coreDiscard(false);
            }
            return element;
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final void close() {
        builder.close();
    }

    @Override
    public final void detach() throws OMException {
        if (detachable != null) {
            detachable.detach();
        } else {
            try {
                while (!builder.isCompleted()) {
                    builder.next();
                }
            } catch (CoreModelException ex) {
                throw AxiomExceptionTranslator.translate(ex);
            }
        }
    }
}
