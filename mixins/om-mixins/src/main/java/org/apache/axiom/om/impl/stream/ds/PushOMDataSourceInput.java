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

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.stream.stax.push.XMLStreamWriterHandler;

public final class PushOMDataSourceInput implements XmlInput {
    private final AxiomSourcedElement root;
    private final OMDataSource dataSource;

    public PushOMDataSourceInput(AxiomSourcedElement root, OMDataSource dataSource) {
        this.root = root;
        this.dataSource = dataSource;
    }

    @Override
    public XmlReader createReader(XmlHandler handler) {
        XmlHandler unwrappedHandler = handler;
        while (unwrappedHandler instanceof XmlHandlerWrapper) {
            unwrappedHandler = ((XmlHandlerWrapper) unwrappedHandler).getParent();
        }
        if (unwrappedHandler instanceof XMLStreamWriterHandler) {
            XMLStreamWriter writer = ((XMLStreamWriterHandler) unwrappedHandler).getWriter();
            // This condition is to cover the case where the following conditions are met:
            //  - The OMDataSource uses MTOMXMLStreamWriter to add optimized binary data.
            //  - The tree is serialized to an XMLStreamWriter that implements the
            //    DataHandlerWriter extension.
            // TODO: need a test case for that scenario
            if (writer instanceof MTOMXMLStreamWriter) {
                return new DirectPushOMDataSourceReader(writer, dataSource);
            }
        }
        return new PushOMDataSourceReader(handler, root, dataSource);
    }
}
