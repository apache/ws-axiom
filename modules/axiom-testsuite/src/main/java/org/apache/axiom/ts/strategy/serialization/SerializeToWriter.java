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
package org.apache.axiom.ts.strategy.serialization;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Serializes an {@link OMContainer} using {@link OMContainer#serialize(Writer)} or
 * {@link OMContainer#serializeAndConsume(Writer)}.
 */
public class SerializeToWriter implements SerializationStrategy {
    private final boolean cache;
    
    public SerializeToWriter(boolean cache) {
        this.cache = cache;
    }

    public void addTestProperties(AxiomTestCase testCase) {
        testCase.addTestProperty("serializationStrategy", "Writer");
        testCase.addTestProperty("cache", String.valueOf(cache));
    }

    public XML serialize(OMContainer container) throws Exception {
        StringWriter sw = new StringWriter();
        if (cache) {
            container.serialize(sw);
        } else {
            container.serializeAndConsume(sw);
        }
        return new XMLAsString(sw.toString());
    }

    public boolean isCaching() {
        return cache;
    }
}
