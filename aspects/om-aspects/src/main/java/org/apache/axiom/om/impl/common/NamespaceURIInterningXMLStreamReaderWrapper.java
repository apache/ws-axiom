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
package org.apache.axiom.om.impl.common;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

/**
 * {@link XMLStreamReader} wrapper that interns namespace URIs. It is used to implement the
 * {@link OMXMLStreamReaderConfiguration#isNamespaceURIInterning()} option.
 */
class NamespaceURIInterningXMLStreamReaderWrapper extends XMLStreamReaderWrapper implements OMXMLStreamReader {
    private NamespaceURIInterningNamespaceContextWrapper namespaceContextWrapper;
    
    public NamespaceURIInterningXMLStreamReaderWrapper(OMXMLStreamReader parent) {
        super(parent);
    }

    private static String intern(String s) {
        return s == null ? null : s.intern();
    }
    
    public String getAttributeNamespace(int index) {
        return intern(super.getAttributeNamespace(index));
    }

    public String getNamespaceURI() {
        return intern(super.getNamespaceURI());
    }

    public String getNamespaceURI(int index) {
        return intern(super.getNamespaceURI(index));
    }

    public String getNamespaceURI(String prefix) {
        return intern(super.getNamespaceURI(prefix));
    }

    public DataHandler getDataHandler(String blobcid) {
        return ((OMXMLStreamReader)getParent()).getDataHandler(blobcid);
    }

    public NamespaceContext getNamespaceContext() {
        NamespaceContext namespaceContext = super.getNamespaceContext();
        if (namespaceContextWrapper == null || namespaceContextWrapper.getParent() != namespaceContext) {
            namespaceContextWrapper = new NamespaceURIInterningNamespaceContextWrapper(namespaceContext);
        }
        return namespaceContextWrapper;
    }

    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)getParent()).isInlineMTOM();
    }


    public void setInlineMTOM(boolean value) {
        ((OMXMLStreamReader)getParent()).setInlineMTOM(value);
    }
}
