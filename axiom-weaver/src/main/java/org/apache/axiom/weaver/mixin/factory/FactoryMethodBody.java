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

final class FactoryMethodBody extends MethodBody {
    private final Class<?> iface;

    FactoryMethodBody(Class<?> iface) {
        this.iface = iface;
    }

    @Override
    public void apply(TargetContext context, MethodVisitor mv) {
        String implementationClassName =
                context.getWeavingContext().getImplementationClassName(iface);
        mv.visitTypeInsn(Opcodes.NEW, implementationClassName);
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, implementationClassName, "<init>", "()V", false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }
}
