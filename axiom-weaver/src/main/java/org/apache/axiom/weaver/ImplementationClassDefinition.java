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
package org.apache.axiom.weaver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class ImplementationClassDefinition extends ClassDefinition {
    private final int version;
    private final int access;
    private final String superName;
    private final String[] ifaceNames;
    private final Mixin[] mixins;
    private final MixinMethod[] methods;

    ImplementationClassDefinition(int version, int access, String className, String superName,
            String[] ifaceNames, Mixin[] mixins) {
        super(className);
        this.version = version;
        this.access = access;
        this.superName = superName != null ? superName : Type.getInternalName(Object.class);
        this.ifaceNames = ifaceNames;
        this.mixins = mixins;
        Map<String, MixinMethod> methodMap = new LinkedHashMap<>();
        for (Mixin mixin : mixins) {
            for (MixinMethod method : mixin.getMethods()) {
                String signature = method.getSignature();
                // TODO: check that the method being replaced is not final
                MixinMethod existingMethod = methodMap.get(signature);
                if (existingMethod != null && !method.getMixin().appliesAfter(existingMethod.getMixin())) {
                    if (existingMethod.getMixin().appliesAfter(method.getMixin())) {
                        // Keep the existing method.
                        continue;
                    } else {
                        throw new WeaverException("Method collision");
                    }
                }
                methodMap.put(signature, method);
            }
        }
        methods = methodMap.values().toArray(new MixinMethod[methodMap.size()]);
    }

    private void generateConstructor(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitIntInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "<init>", "()V", false);
        for (Mixin mixin : mixins) {
            mv.visitIntInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, mixin.getInitMethodName(), "()V", false);
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void generateStaticInitializer(ClassVisitor cv) {
        boolean needed = false;
        for (Mixin mixin : mixins) {
            if (mixin.getStaticInitializerMethodName() != null) {
                needed = true;
                break;
            }
        }
        if (!needed) {
            return;
        }
        MethodVisitor mv = cv.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "<clinit>", "()V", null, null);
        mv.visitCode();
        for (Mixin mixin : mixins) {
            String methodName = mixin.getStaticInitializerMethodName();
            if (methodName != null) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, methodName, "()V", false);
            }
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    void accept(ClassVisitor cv) {
        cv.visit(version, access, className, null, superName, ifaceNames);
        generateConstructor(cv);
        generateStaticInitializer(cv);
        for (Mixin mixin : mixins) {
            mixin.apply(className, cv);
        }
        for (MixinMethod method : methods) {
            method.apply(className, cv);
        }
        cv.visitEnd();
    }
}
