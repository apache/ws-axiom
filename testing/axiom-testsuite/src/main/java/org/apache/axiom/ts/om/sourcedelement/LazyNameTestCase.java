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
package org.apache.axiom.ts.om.sourcedelement;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;

public abstract class LazyNameTestCase extends AxiomTestCase {
    protected final OMSourcedElementVariant variant;
    protected final QName qname;

    public LazyNameTestCase(
            OMMetaFactory metaFactory, OMSourcedElementVariant variant, QName qname) {
        super(metaFactory);
        this.variant = variant;
        this.qname = qname;
        addTestParameter("variant", variant.getName());
        variant.addTestProperties(this);
        addTestParameter("prefix", qname.getPrefix());
        addTestParameter("uri", qname.getNamespaceURI());
    }

    @Override
    protected final void runTest() throws Throwable {
        runTest(variant.createOMSourcedElement(metaFactory.getOMFactory(), qname));
    }

    protected abstract void runTest(OMSourcedElement element) throws Throwable;
}
