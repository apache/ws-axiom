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
package org.apache.axiom.soap.impl.common;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeader;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;

public final class SOAPHeaderBlockMapper implements Mapper<SOAPHeaderBlock,AxiomElement> {
    public static final SOAPHeaderBlockMapper INSTANCE = new SOAPHeaderBlockMapper();
    
    private SOAPHeaderBlockMapper() {}
    
    @Override
    public SOAPHeaderBlock map(AxiomElement element) {
        if (element instanceof SOAPHeaderBlock) {
            return (SOAPHeaderBlock)element;
        } else {
            try {
                AxiomSOAPHeaderBlock newElement = ((AxiomSOAPHeader)element.coreGetParent()).getSOAPHelper().getHeaderBlockType().create(element.getNodeFactory());
                element.corePromote(newElement, AxiomSemantics.INSTANCE);
                return newElement;
            } catch (CoreModelException ex) {
                throw AxiomSemantics.INSTANCE.toUncheckedException(ex);
            }
        }
    }
}
