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
package org.apache.axiom.testutils.suite;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fan-out node for types that implement {@link Dimension}. Parameters are extracted via {@link
 * Dimension#addTestParameters}.
 *
 * <p>For types that do <em>not</em> implement {@code Dimension}, use {@link ParameterFanOutNode}
 * instead.
 *
 * @param <D> the dimension type
 */
public class DimensionFanOutNode<D extends Dimension> extends AbstractFanOutNode<D> {
    public DimensionFanOutNode(Class<D> dimensionType, List<D> dimensions) {
        super(dimensionType, dimensions);
    }

    @Override
    protected Map<String, String> extractParameters(D dimension) {
        Map<String, String> parameters = new LinkedHashMap<>();
        dimension.addTestParameters(
                new TestParameterTarget() {
                    @Override
                    public void addTestParameter(String name, String value) {
                        parameters.put(name, value);
                    }

                    @Override
                    public void addTestParameter(String name, boolean value) {
                        addTestParameter(name, String.valueOf(value));
                    }

                    @Override
                    public void addTestParameter(String name, int value) {
                        addTestParameter(name, String.valueOf(value));
                    }
                });
        return parameters;
    }
}
