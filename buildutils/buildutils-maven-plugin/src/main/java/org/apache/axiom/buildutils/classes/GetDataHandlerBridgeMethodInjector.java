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
package org.apache.axiom.buildutils.classes;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Injects a bridge method for {@code getDataHandler} returning {@link Object}, for compatibility
 * with previous versions of {@code OMText}.
 */
final class GetDataHandlerBridgeMethodInjector extends ClassVisitor {
    private String className;

    GetDataHandlerBridgeMethodInjector(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals("getDataHandler") && desc.equals("()Ljavax/activation/DataHandler;")) {
            MethodVisitor mv = super.visitMethod((access | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC) & ~Opcodes.ACC_FINAL, name, "()Ljava/lang/Object;", null, exceptions);
            if ((access & Opcodes.ACC_ABSTRACT) == 0) {
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, name, desc, false);
                mv.visitInsn(Opcodes.ARETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

}
