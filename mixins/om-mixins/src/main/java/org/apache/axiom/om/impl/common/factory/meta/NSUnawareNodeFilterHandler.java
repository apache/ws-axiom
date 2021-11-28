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
package org.apache.axiom.om.impl.common.factory.meta;

import javax.xml.XMLConstants;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;
import org.apache.axiom.om.OMException;

final class NSUnawareNodeFilterHandler extends XmlHandlerWrapper {
    NSUnawareNodeFilterHandler(XmlHandler parent) {
        super(parent);
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        int idx = name.indexOf(':');
        if (idx == -1) {
            if (name.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                super.processNamespaceDeclaration("", value);
            } else {
                super.processAttribute("", name, "", value, type, specified);
            }
        } else {
            if (idx == 5 && name.startsWith(XMLConstants.XMLNS_ATTRIBUTE)) {
                super.processNamespaceDeclaration(name.substring(6), value);
            } else {
                throw new OMException("Namespace unware attributes not supported");
            }
        }
    }
}
