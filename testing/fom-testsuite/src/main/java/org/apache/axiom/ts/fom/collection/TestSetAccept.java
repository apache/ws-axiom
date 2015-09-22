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
package org.apache.axiom.ts.fom.collection;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.Constants;
import org.apache.axiom.ts.fom.AbderaTestCase;

public class TestSetAccept extends AbderaTestCase {
    public TestSetAccept(Abdera abdera) {
        super(abdera);
    }

    @Override
    protected void runTest() throws Throwable {
        Collection collection = abdera.getFactory().newCollection();
        collection.setAccept("image/png", "image/jpeg");
        List<Element> children = collection.getElements();
        assertThat(children).hasSize(2);
        assertThat(children.get(0).getQName()).isEqualTo(Constants.ACCEPT);
        assertThat(children.get(0).getText()).isEqualTo("image/png");
        assertThat(children.get(1).getQName()).isEqualTo(Constants.ACCEPT);
        assertThat(children.get(1).getText()).isEqualTo("image/jpeg");
    }
}
