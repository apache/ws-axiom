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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.weaver.mixin.ClassDefinition;
import org.apache.axiom.weaver.mixin.InitializerMethod;
import org.apache.axiom.weaver.mixin.Mixin;
import org.apache.axiom.weaver.mixin.MixinMethod;
import org.apache.axiom.weaver.mixin.StaticInitializerMethod;
import org.apache.axiom.weaver.mixin.TargetContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class ImplementationClassDefinition extends ClassDefinition {
    private static final Log log = LogFactory.getLog(ImplementationClassDefinition.class);

    private final TargetContext targetContext;
    private final int version;
    private final int access;
    private final String superName;
    private final String[] ifaceNames;
    private final boolean singleton;
    private final Mixin[] mixins;
    private final MixinMethod[] methods;
    private final List<Named<InitializerMethod>> initializerMethods = new ArrayList<>();
    private final List<Named<StaticInitializerMethod>> staticInitializerMethods = new ArrayList<>();

    ImplementationClassDefinition(
            TargetContext targetContext,
            int version,
            int access,
            String superName,
            String[] ifaceNames,
            boolean singleton,
            Mixin[] mixins) {
        super(targetContext.getTargetClassName());
        this.targetContext = targetContext;
        this.version = version;
        this.access = access;
        this.superName = superName != null ? superName : Type.getInternalName(Object.class);
        this.ifaceNames = ifaceNames;
        this.singleton = singleton;
        this.mixins = mixins;
        Map<String, MixinMethod> methodMap = new LinkedHashMap<>();
        UniqueNameGenerator methodNameGenerator = new UniqueNameGenerator();
        for (Mixin mixin : mixins) {
            for (MixinMethod method : mixin.getMethods()) {
                String signature = method.getSignature();
                // TODO: check that the method being replaced is not final
                MixinMethod existingMethod = methodMap.get(signature);
                if (existingMethod != null
                        && !method.getMixin().appliesAfter(existingMethod.getMixin())) {
                    if (existingMethod.getMixin().appliesAfter(method.getMixin())) {
                        // Keep the existing method.
                        continue;
                    } else {
                        String body1 = existingMethod.getBody().toString(targetContext);
                        String body2 = method.getBody().toString(targetContext);
                        // Allow two mixins to define identical methods. This is useful for DOOM
                        // because some methods are defined in the same way by DOM and Axiom.
                        if (body1.equals(body2)) {
                            log.info(
                                    "Ignoring identical mixin method for "
                                            + signature
                                            + " in "
                                            + targetContext.getTargetClassName());
                            continue;
                        }
                        throw new WeaverException(
                                "Method collision for "
                                        + signature
                                        + " in "
                                        + targetContext.getTargetClassName()
                                        + "\n--- Method defined by "
                                        + existingMethod.getMixin().getName()
                                        + ":\n"
                                        + body1
                                        + "\n--- Method defined by "
                                        + method.getMixin().getName()
                                        + ":\n"
                                        + body2);
                    }
                }
                methodMap.put(signature, method);
            }
            InitializerMethod initializerMethod = mixin.getInitializerMethod();
            if (initializerMethod != null) {
                initializerMethods.add(
                        new Named<>(
                                initializerMethod,
                                methodNameGenerator.generateUniqueName("init$" + mixin.getName())));
            }
            StaticInitializerMethod staticInitializerMethod = mixin.getStaticInitializerMethod();
            if (staticInitializerMethod != null) {
                staticInitializerMethods.add(
                        new Named<>(
                                staticInitializerMethod,
                                methodNameGenerator.generateUniqueName(
                                        "clinit$" + mixin.getName())));
            }
        }
        methods = methodMap.values().toArray(new MixinMethod[methodMap.size()]);
    }

    private void generateConstructor(ClassVisitor cv) {
        MethodVisitor mv =
                cv.visitMethod(
                        singleton ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
                        "<init>",
                        "()V",
                        null,
                        null);
        mv.visitCode();
        mv.visitIntInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "<init>", "()V", false);
        for (Named<InitializerMethod> method : initializerMethods) {
            mv.visitIntInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, method.getName(), "()V", false);
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void generateStaticInitializer(ClassVisitor cv) {
        if (staticInitializerMethods.isEmpty() && !singleton) {
            return;
        }
        MethodVisitor mv =
                cv.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        for (Named<StaticInitializerMethod> method : staticInitializerMethods) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, method.getName(), "()V", false);
        }
        if (singleton) {
            mv.visitTypeInsn(Opcodes.NEW, targetContext.getTargetClassName());
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    targetContext.getTargetClassName(),
                    "<init>",
                    "()V",
                    false);
            mv.visitFieldInsn(
                    Opcodes.PUTSTATIC,
                    targetContext.getTargetClassName(),
                    "INSTANCE",
                    "L" + targetContext.getTargetClassName() + ";");
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(singleton ? 2 : 0, 0);
        mv.visitEnd();
    }

    @Override
    public void accept(ClassVisitor cv) {
        cv.visit(version, access, className, null, superName, ifaceNames);
        if (singleton) {
            cv.visitField(
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                    "INSTANCE",
                    "L" + targetContext.getTargetClassName() + ";",
                    null,
                    null);
        }
        generateConstructor(cv);
        generateStaticInitializer(cv);
        for (Mixin mixin : mixins) {
            mixin.apply(className, cv);
        }
        for (Named<InitializerMethod> method : initializerMethods) {
            MethodVisitor mv =
                    cv.visitMethod(Opcodes.ACC_PRIVATE, method.getName(), "()V", null, null);
            if (mv != null) {
                method.get().getBody().apply(targetContext, mv);
            }
        }
        for (Named<StaticInitializerMethod> method : staticInitializerMethods) {
            MethodVisitor mv =
                    cv.visitMethod(
                            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                            method.getName(),
                            "()V",
                            null,
                            null);
            if (mv != null) {
                method.get().getBody().apply(targetContext, mv);
            }
        }
        for (MixinMethod method : methods) {
            method.apply(targetContext, cv);
        }
        cv.visitEnd();
    }
}
