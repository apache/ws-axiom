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
package org.apache.axiom.ts.fom.attribute;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.axiom.ts.fom.AbderaTestCase;

public class TestSetAttributeValueQNameNew extends AbderaTestCase {
    private final QName qname;
    
    public TestSetAttributeValueQNameNew(Abdera abdera, QName qname) {
        super(abdera);
        this.qname = qname;
        addTestParameter("qname", qname.toString());
        addTestParameter("prefix", qname.getPrefix());
    }

    @Override
    protected void runTest() throws Throwable {
        Element element = abdera.getFactory().newElement(new QName("test"));
        element.setAttributeValue(qname, "value");
        assertThat(element.getAttributeValue(qname)).isEqualTo("value");
        List<QName> attrs = element.getAttributes();
        assertThat(attrs).hasSize(1);
        QName actualQName = attrs.get(0);
        assertThat(actualQName).isEqualTo(qname);
        assertThat(actualQName.getPrefix()).isEqualTo(qname.getPrefix());
    }
}
