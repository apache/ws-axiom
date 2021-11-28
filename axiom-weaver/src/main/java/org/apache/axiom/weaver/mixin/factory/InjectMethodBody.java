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

import org.apache.axiom.weaver.mixin.MethodBody;
import org.apache.axiom.weaver.mixin.TargetContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class InjectMethodBody extends MethodBody {
    private final Class<?> iface;

    InjectMethodBody(Class<?> iface) {
        this.iface = iface;
    }

    @Override
    public void apply(TargetContext context, MethodVisitor mv) {
        String implementationClassName =
                context.getWeavingContext().getImplementationClassName(iface);
        if (implementationClassName != null) {
            mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    implementationClassName,
                    "INSTANCE",
                    "L" + implementationClassName + ";");
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(1, 1);
        } else {
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/UnsupportedOperationException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/UnsupportedOperationException",
                    "<init>",
                    "()V",
                    false);
            mv.visitInsn(Opcodes.ATHROW);
            mv.visitMaxs(2, 1);
        }
        mv.visitEnd();
    }
}
