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
package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import javax.xml.stream.XMLStreamReader;



/**
 * A Custom Builder is registered on the StAXBuilder for a particular QName or payload.
 * When the QName or payload is encountered, the CustomBuilder will build the OMElement
 * or OMSourcedElement for the StAXBuilder.  
 *
 * @See StAXBuilder.registerCustomBuilder()
 */
public interface CustomBuilder {
    /**
     * Create an OMElement for this whole subtree.
     * A null is returned if the default StAXBuilder behavior should be used.
     * @param namespace
     * @param localPart
     * @param parent
     * @param reader
     * @return null or OMElement
     */
    public OMElement create(String namespace, 
                            String localPart, 
                            OMContainer parent, 
                            XMLStreamReader reader,
                            OMFactory factory)
        throws OMException;
}
