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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/** Make sure the expanded OMSourcedElement behaves like a normal OMElement. */
public class TestExpand implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMSourcedElement element = TestDocument.DOCUMENT1.createOMSourcedElement(factory, false, true);
        element.getAllDeclaredNamespaces();
        assertThat(countItems(element.getAllDeclaredNamespaces())).isEqualTo(1);
        assertThat(countItems(element.getAllAttributes())).isEqualTo(1);
        assertThat(element.getAttributeValue(new QName("books"))).isEqualTo("1");
        OMElement child = element.getFirstElement();
        assertThat(child.getLocalName()).isEqualTo("type");
        assertThat(child.getNamespace().getNamespaceURI()).isEqualTo("http://www.sosnoski.com/uwjws/library");
        OMNode next = child.getNextOMSibling();
        assertThat(next).isInstanceOf(OMElement.class);
        next = next.getNextOMSibling();
        assertThat(next).isInstanceOf(OMElement.class);
        child = (OMElement) next;
        assertThat(child.getLocalName()).isEqualTo("book");
        assertThat(child.getNamespace().getNamespaceURI()).isEqualTo("http://www.sosnoski.com/uwjws/library");
        assertThat(child.getAttributeValue(new QName("type"))).isEqualTo("xml");
    }

    private int countItems(Iterator iter) {
        int count = 0;
        while (iter.hasNext()) {
            count++;
            iter.next();
        }
        return count;
    }
}
