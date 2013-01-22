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
package org.apache.axiom.ts.strategy;

import org.apache.axiom.ts.strategy.serialization.SerializationStrategy;
import org.apache.axiom.ts.strategy.serialization.SerializeFromSAXSource;
import org.apache.axiom.ts.strategy.serialization.SerializeFromXMLStreamReader;
import org.apache.axiom.ts.strategy.serialization.SerializeToOutputStream;
import org.apache.axiom.ts.strategy.serialization.SerializeToWriter;
import org.apache.axiom.ts.strategy.serialization.SerializeToXMLStreamWriter;

public final class Strategies {
    private static final SerializationStrategy[] serializationStrategies = {
        new SerializeToOutputStream(true),
        new SerializeToOutputStream(false),
        new SerializeToWriter(true),
        new SerializeToWriter(false),
        new SerializeToXMLStreamWriter(true),
        new SerializeToXMLStreamWriter(false),
        new SerializeFromXMLStreamReader(true),
        new SerializeFromXMLStreamReader(false),
        new SerializeFromSAXSource(true),
        new SerializeFromSAXSource(false) };
    
    private static final ElementContext[] elementContexts = {
        ElementContext.ORPHAN,
        ElementContext.ELEMENT,
        ElementContext.INCOMPLETE_ELEMENT,
    };
    
    private Strategies() {}
    
    public static SerializationStrategy[] getSerializationStrategies() {
        return (SerializationStrategy[])serializationStrategies.clone();
    }
    
    public static ElementContext[] getElementContexts() {
        return (ElementContext[])elementContexts.clone();
    }
}
