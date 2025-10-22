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
package org.apache.axiom.om.ds;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.QNameAwareOMDataSource;

/** {@link OMDataSource} implementation that represents a text node wrapped inside an element. */
public abstract class WrappedTextNodeOMDataSource extends AbstractPullOMDataSource
        implements QNameAwareOMDataSource {
    protected final QName wrapperElementName;

    public WrappedTextNodeOMDataSource(QName wrapperElementName) {
        this.wrapperElementName = wrapperElementName;
    }

    @Override
    public String getLocalName() {
        return wrapperElementName.getLocalPart();
    }

    @Override
    public String getNamespaceURI() {
        return wrapperElementName.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return wrapperElementName.getPrefix();
    }
}
