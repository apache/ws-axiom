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
package org.apache.axiom.om.impl.stream.ds;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.stream.stax.XmlHandlerStreamWriter;

final class PushOMDataSourceReader implements XmlReader {
    private final XmlHandler handler;
    private final AxiomSourcedElement root;
    private final OMDataSource dataSource;

    PushOMDataSourceReader(XmlHandler handler, AxiomSourcedElement root, OMDataSource dataSource) {
        this.handler = handler;
        this.root = root;
        this.dataSource = dataSource;
    }
    
    @Override
    public boolean proceed() throws StreamException {
        try {
            XMLStreamWriter writer = new XmlHandlerStreamWriter(handler);
            // Seed the namespace context with the namespace context from the parent
            OMContainer parent = root.getParent();
            if (parent instanceof OMElement) {
                for (Iterator<OMNamespace> it = ((OMElement)parent).getNamespacesInScope(); it.hasNext(); ) {
                    OMNamespace ns = it.next();
                    writer.setPrefix(ns.getPrefix(), ns.getNamespaceURI());
                }
            }
            dataSource.serialize(new PushOMDataSourceStreamWriter(writer));
        } catch (XMLStreamException ex) {
            throw new StreamException(ex);
        }
        return true;
    }
}
