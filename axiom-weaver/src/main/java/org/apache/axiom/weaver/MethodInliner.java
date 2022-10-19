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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class MethodInliner extends MethodVisitor {
    private Label endLabel;
    private int maxStack;
    private int maxLocals;
    private Object[] locals;

    MethodInliner(MethodVisitor methodVisitor, int maxStack, int maxLocals, Object[] locals) {
        super(Opcodes.ASM9, methodVisitor);
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.locals = locals;
    }

    @Override
    public void visitCode() {
        endLabel = new Label();
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            super.visitJumpInsn(Opcodes.GOTO, endLabel);
        } else {
            super.visitInsn(opcode);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (maxStack > this.maxStack) {
            this.maxStack = maxStack;
        }
        if (maxLocals > this.maxLocals) {
            this.maxLocals = maxLocals;
        }
    }

    @Override
    public void visitEnd() {
        super.visitLabel(endLabel);
        super.visitFrame(Opcodes.F_NEW, locals.length, locals, 0, null);
        endLabel = null;
    }

    public void emitMaxs() {
        super.visitMaxs(maxStack, maxLocals);
    }
}
