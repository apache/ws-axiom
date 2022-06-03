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
package org.apache.axiom.ts.dimension.serialization;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.testutils.suite.Dimension;

/** Defines a strategy to serialize an {@link OMContainer} instance to XML. */
public abstract class SerializationStrategy extends Multiton implements Dimension {
    SerializationStrategy() {}

    /**
     * Serialize the given {@link OMContainer}.
     *
     * @param container the container to serialize to XML
     * @return the serialized XML
     * @throws Exception
     */
    public abstract XML serialize(OMContainer container) throws Exception;

    /**
     * Determine if this serialization strategy works in pull or push mode.
     *
     * @return <code>true</code> if the serialization is driven by Axiom, <code>false</code> if the
     *     serialization is driven by the application code
     */
    public abstract boolean isPush();

    /**
     * Determine if this serialization strategy consumes the content of the {@link OMContainer}.
     *
     * @return <code>true</code> if the strategy preserves the content, <code>false</code> if it
     *     consumes the content
     */
    public abstract boolean isCaching();

    /**
     * Determine if this serialization strategy is able to correctly serialize the internal subset
     * of a DOCTYPE declaration.
     *
     * @return <code>true</code> if this strategy correctly serializes the internal subset of a DTD,
     *     <code>false</code> otherwise
     */
    public abstract boolean supportsInternalSubset();

    @Instances
    private static SerializationStrategy[] instances() {
        return new SerializationStrategy[] {
            new SerializeToOutputStream(true),
            new SerializeToOutputStream(false),
            new SerializeToWriter(true),
            new SerializeToWriter(false),
            new SerializeToXMLStreamWriter(true),
            new SerializeToXMLStreamWriter(false),
            new SerializeFromXMLStreamReader(true),
            new SerializeFromXMLStreamReader(false),
            new SerializeFromSAXSource(true),
            new SerializeFromSAXSource(false)
        };
    }
}
