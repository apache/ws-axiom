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

package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;

class XLXP1StreamReaderWrapper extends XLXPStreamReaderWrapper {
    public XLXP1StreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    public Object getProperty(String name) {
        if (DTDReader.PROPERTY.equals(name)) {
            return new XLXP1DTDReaderImpl(getParent());
        } else {
            return super.getProperty(name);
        }
    }

    public String getEncoding() {
        // Under some circumstances, some versions of XLXP return an empty string instead of null
        String encoding = super.getEncoding();
        return encoding == null || encoding.length() == 0 ? null : encoding;
    }

    public String getNamespaceURI(String prefix) {
        // XLXP may return "" instead of null
        String uri = super.getNamespaceURI(prefix);
        return uri == null || uri.length() == 0 ? null : uri;
    }

    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }
}
