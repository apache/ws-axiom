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

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Attribute;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;
import org.apache.axiom.ts.fom.AbderaTestCase;

/**
 * Tests that an {@link Attribute} added using {@link Element#setAttributeValue(String, String)}
 * inherits the factory from the element.
 */
public class TestGetFactory extends AbderaTestCase {
    public TestGetFactory(Abdera abdera) {
        super(abdera);
    }

    @Override
    protected void runTest() throws Throwable {
        Factory factory = abdera.getFactory();
        Feed feed = factory.newFeed();
        feed.setAttributeValue("attr", "value");
        Object xpathResult = abdera.getXPath().selectSingleNode("./@attr", feed);
        assertThat(xpathResult).isInstanceOf(Attribute.class);
        assertThat(((Attribute)xpathResult).getFactory()).isSameAs(factory);
    }
}
