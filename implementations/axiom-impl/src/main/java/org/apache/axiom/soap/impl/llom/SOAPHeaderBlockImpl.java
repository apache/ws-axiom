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

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.common.AxiomSOAPHeaderBlock;

/** Class SOAPHeaderBlockImpl */
public abstract class SOAPHeaderBlockImpl extends OMSourcedElementImpl
        implements AxiomSOAPHeaderBlock {

    public SOAPHeaderBlockImpl(OMFactory factory) {
        super(factory);
    }

    public SOAPHeaderBlockImpl(SOAPFactory factory, OMDataSource source) {
        super(factory, source);
    }

    public SOAPHeaderBlockImpl(String localName, OMNamespace ns, SOAPFactory factory, 
                               OMDataSource ds) {
        super(localName, ns, factory, ds);
    }

    public void internalSetParent(CoreParentNode element) {
        super.internalSetParent(element);

        if (element instanceof OMElement) {
            checkParent((OMElement) element);
        }
    }
    
    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        SOAPHeaderBlock clone = ((SOAPFactory)getOMFactory()).createSOAPHeaderBlock(getLocalName(), getNamespace(), (SOAPHeader)targetParent);
        copyData(options, clone);
        return clone;
    }

    protected OMSourcedElement createClone(OMCloneOptions options, OMDataSource ds) {
        SOAPHeaderBlock clone = ((SOAPFactory)getOMFactory()).createSOAPHeaderBlock(ds);
        copyData(options, clone);
        return clone;
    }

    private void copyData(OMCloneOptions options, SOAPHeaderBlock targetSHB) {
        // Copy the processed flag.  The other SOAPHeaderBlock information 
        // (e.g. role, mustUnderstand) are attributes on the tag and are copied elsewhere.
        Boolean processedFlag = options instanceof SOAPCloneOptions ? ((SOAPCloneOptions)options).getProcessedFlag() : null;
        if ((processedFlag == null && isProcessed()) || (processedFlag != null && processedFlag.booleanValue())) {
            targetSHB.setProcessed();
        }
    }
}
