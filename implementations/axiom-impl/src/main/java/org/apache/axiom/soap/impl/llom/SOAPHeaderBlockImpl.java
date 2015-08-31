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
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.common.AxiomSOAPHeaderBlock;

/** Class SOAPHeaderBlockImpl */
public abstract class SOAPHeaderBlockImpl extends OMSourcedElementImpl
        implements AxiomSOAPHeaderBlock {

    public SOAPHeaderBlockImpl(OMFactory factory) {
        super(factory);
    }

    public void internalSetParent(CoreParentNode element) {
        super.internalSetParent(element);

        if (element instanceof OMElement) {
            checkParent((OMElement) element);
        }
    }
    
    protected OMSourcedElement createClone(OMCloneOptions options, OMDataSource ds) {
        AxiomSOAPHeaderBlock clone = (AxiomSOAPHeaderBlock)((SOAPFactory)getOMFactory()).createSOAPHeaderBlock(ds);
        copyData(options, clone);
        return clone;
    }
}
