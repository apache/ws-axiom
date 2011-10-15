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

import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.axiom.om.OMDataSource;

public class StreamSwitch extends StreamReaderDelegate {
    // namespaceURI interning
    // default is false because most XMLStreamReader implementations don't do interning
    // due to performance impacts
    private boolean namespaceURIInterning = false;

    /**
     * Set namespace uri interning
     * @param b
     */
    public void setNamespaceURIInterning(boolean b) {
        this.namespaceURIInterning = b;
    }
    
    /**
     * @return if namespace uri interning 
     */
    public boolean isNamespaceURIInterning() {
        return this.namespaceURIInterning;
    }
    
    public boolean isClosed() {
        return ((SwitchingWrapper)getParent()).isClosed();
    }

    public void releaseParserOnClose(boolean value) {
        ((SwitchingWrapper)getParent()).releaseParserOnClose(value);
    }

    public OMDataSource getDataSource() {
        return ((SwitchingWrapper)getParent()).getDataSource();
    }
    
    public void enableDataSourceEvents(boolean value) {
        ((SwitchingWrapper)getParent()).enableDataSourceEvents(value);
    }

    // TODO: one would expect that the same is applied to other methods returning namespace URIs, but this was not the case in the original OMStAXWrapper code
    public String getNamespaceURI() {
        String namespaceURI = super.getNamespaceURI();
        
        // By default most parsers don't intern the namespace.
        // Unfortunately the property to detect interning on the delegate parsers is hard to detect.
        // Woodstox has a proprietary property on the XMLInputFactory.
        // IBM has a proprietary property on the XMLStreamReader.
        // For now only force the interning if requested.
        if (this.isNamespaceURIInterning()) {
            namespaceURI = (namespaceURI != null) ? namespaceURI.intern() : null;
        }
        return namespaceURI;
    }
}
