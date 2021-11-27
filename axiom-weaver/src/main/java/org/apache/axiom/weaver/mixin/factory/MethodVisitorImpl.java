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

import java.util.List;

import org.apache.axiom.weaver.classio.ClassFetcher;
import org.apache.axiom.weaver.mixin.MixinMethod;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class MethodVisitorImpl extends MethodVisitor {
    private final String methodName;
    private final String methodDescriptor;
    private final ClassFetcher classFetcher;
    private final List<MixinMethod> mixinMethods;

    MethodVisitorImpl(
            String methodName,
            String methodDescriptor,
            ClassFetcher classFetcher,
            List<MixinMethod> mixinMethods) {
        super(Opcodes.ASM9);
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.classFetcher = classFetcher;
        this.mixinMethods = mixinMethods;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (!descriptor.equals("Lorg/apache/axiom/weaver/annotation/FactoryMethod;")) {
            return null;
        }
        // TODO: check that the method has the expected signature
        mixinMethods.add(
                new MixinMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                        methodName,
                        methodDescriptor,
                        null,
                        null,
                        new FactoryMethodBody(
                                classFetcher.loadClass(
                                        Type.getReturnType(methodDescriptor).getClassName()))));
        return null;
    }
}
