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
package org.apache.axiom.locator;

import java.util.Arrays;

import org.apache.axiom.om.OMMetaFactory;

final class Implementation {
    private final String name;
    private final OMMetaFactory metaFactory;
    private final Feature[] features;

    Implementation(String name, OMMetaFactory metaFactory, Feature[] features) {
        this.name = name;
        this.metaFactory = metaFactory;
        this.features = features;
    }

    String getName() {
        return name;
    }

    OMMetaFactory getMetaFactory() {
        return metaFactory;
    }

    Feature[] getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return name
                + "(metaFactory="
                + metaFactory.getClass().getName()
                + ",features="
                + (features != null ? Arrays.asList(features) : null)
                + ")";
    }
}
