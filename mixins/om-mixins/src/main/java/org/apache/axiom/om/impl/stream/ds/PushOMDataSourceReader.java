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
import org.apache.axiom.core.stream.XmlHandlerWrapper;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.core.stream.stax.push.input.XmlHandlerStreamWriter;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.stream.XmlDeclarationRewriterHandler;
import org.apache.axiom.om.impl.stream.stax.push.AxiomXMLStreamWriterExtensionFactory;

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
        // TODO: we might want to unwrap the NamespaceRepairingFilter (and some other filters) here
        XmlHandler handler = this.handler;
        OMOutputFormat format = null;
        XmlHandler current = handler;
        while (current instanceof XmlHandlerWrapper) {
            if (current instanceof XmlDeclarationRewriterHandler) {
                format = ((XmlDeclarationRewriterHandler) current).getFormat();
                break;
            }
            current = ((XmlHandlerWrapper) current).getParent();
        }
        if (format == null) {
            // This is for the OMSourcedElement expansion case
            format = new OMOutputFormat();
            format.setDoOptimize(true);
            handler = new PushOMDataSourceXOPHandler(handler);
        }
        try {
            XMLStreamWriter writer =
                    new XmlHandlerStreamWriter(
                            handler, null, AxiomXMLStreamWriterExtensionFactory.INSTANCE);
            // Seed the namespace context with the namespace context from the parent
            OMContainer parent = root.getParent();
            if (parent instanceof OMElement element) {
                for (Iterator<OMNamespace> it = element.getNamespacesInScope(); it.hasNext(); ) {
                    OMNamespace ns = it.next();
                    writer.setPrefix(ns.getPrefix(), ns.getNamespaceURI());
                }
            }
            handler.startFragment();
            dataSource.serialize(
                    new MTOMXMLStreamWriterImpl(new PushOMDataSourceStreamWriter(writer), format));
            handler.completed();
        } catch (XMLStreamException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof StreamException streamException) {
                throw streamException;
            } else {
                throw new StreamException(ex);
            }
        }
        return true;
    }

    @Override
    public void dispose() {}
}
