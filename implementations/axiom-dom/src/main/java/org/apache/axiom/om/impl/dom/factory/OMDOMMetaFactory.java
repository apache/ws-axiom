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

package org.apache.axiom.om.impl.dom.factory;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.om.impl.common.factory.meta.AbstractOMMetaFactory;
import org.w3c.dom.DOMImplementation;

/**
 * Meta factory for the DOOM implementation.
 */
public class OMDOMMetaFactory extends AbstractOMMetaFactory implements DOMMetaFactory {
    public static final OMDOMMetaFactory INSTANCE = new OMDOMMetaFactory();
    
    private OMDOMMetaFactory() {
        super(DOOMNodeFactory.INSTANCE);
    }

    @Override
    public DocumentBuilderFactory newDocumentBuilderFactory() {
        return new DOOMDocumentBuilderFactory(getOMFactory());
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return Factories.DOM_NODE_FACTORY;
    }
}
