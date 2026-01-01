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
package org.apache.axiom.weaver.mixin.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.axiom.weaver.classio.ClassFetcher;
import org.apache.axiom.weaver.mixin.Mixin;
import org.apache.axiom.weaver.mixin.MixinMethod;
import org.objectweb.asm.Opcodes;

public final class FactoryMixinFactory {
    private FactoryMixinFactory() {}

    public static Optional<Mixin> createFactoryMixin(ClassFetcher classFetcher, Class<?> iface) {
        List<MixinMethod> mixinMethods = new ArrayList<>();
        classFetcher.fetch(iface.getName(), new ClassVisitorImpl(classFetcher, mixinMethods));
        if (mixinMethods.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
                new Mixin(
                        Opcodes.V17,
                        iface.getSimpleName() + "FactoryMixin",
                        iface,
                        Collections.emptyList(),
                        null,
                        null,
                        mixinMethods,
                        Collections.emptyList()));
    }
}
