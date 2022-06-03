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
package org.apache.axiom.ts.om.element;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the iterator returned by {@link OMContainer#getChildrenWithName(QName)} returns the
 * correct element when {@link Iterator#next()} is used without calling {@link Iterator#hasNext()}
 * before. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-78">AXIOM-78</a> and <a
 * href="https://issues.apache.org/jira/browse/AXIOM-172">AXIOM-172</a>.
 */
public class TestGetChildrenWithNameNextWithoutHasNext extends AxiomTestCase {
    public TestGetChildrenWithNameNextWithoutHasNext(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement(new QName("root"));
        factory.createOMElement(new QName("child1"), element);
        OMElement child2 = factory.createOMElement(new QName("child2"), element);
        Iterator<OMElement> it = element.getChildrenWithName(new QName("child2"));
        assertSame(child2, it.next());
    }
}
