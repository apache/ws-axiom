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
package org.apache.axiom.truth.xml;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.apache.axiom.truth.xml.spi.TraverserException;

class Filter implements Traverser {
    private final Traverser parent;

    Filter(Traverser parent) {
        this.parent = parent;
    }

    @Override
    public Event next() throws TraverserException {
        return parent.next();
    }

    @Override
    public String getRootName() {
        return parent.getRootName();
    }

    @Override
    public String getPublicId() {
        return parent.getPublicId();
    }

    @Override
    public String getSystemId() {
        return parent.getSystemId();
    }

    @Override
    public QName getQName() {
        return parent.getQName();
    }

    @Override
    public Map<QName, String> getAttributes() {
        return parent.getAttributes();
    }

    @Override
    public Map<String, String> getNamespaces() {
        return parent.getNamespaces();
    }

    @Override
    public String getText() {
        return parent.getText();
    }

    @Override
    public String getEntityName() {
        return parent.getEntityName();
    }

    @Override
    public String getPITarget() {
        return parent.getPITarget();
    }

    @Override
    public String getPIData() {
        return parent.getPIData();
    }
}
