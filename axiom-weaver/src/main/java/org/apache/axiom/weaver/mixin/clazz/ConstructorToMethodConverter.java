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
package org.apache.axiom.weaver.mixin.clazz;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Converts a constructor into a normal method. This will remove the call to the constructor of the
 * superclass. Note that this is only meaningful if the constructor is a default constructor (i.e.
 * has no parameters) and calls the default constructor of the superclass. By definition, this is
 * the case for mixins.
 *
 * <p>The class actually looks for the following sequence of instructions and removes them:
 *
 * <pre>
 *    ALOAD 0
 *    INVOKESPECIAL java/lang/Object.&lt;init> ()V
 * </pre>
 */
final class ConstructorToMethodConverter extends MethodVisitor {
    /**
     * Flag indicating that the last processed instruction was ALOAD 0. This will only be set if
     * {@link #callRemoved} is <code>false</code>.
     */
    private boolean lastWasALoad0;

    /** Flag indicating that the call to the constructor of the superclass has been removed. */
    private boolean callRemoved;

    public ConstructorToMethodConverter(MethodVisitor mv) {
        super(Opcodes.ASM9, mv);
    }

    private void reset() {
        if (lastWasALoad0) {
            super.visitVarInsn(Opcodes.ALOAD, 0);
            lastWasALoad0 = false;
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        reset();
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        reset();
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitInsn(int opcode) {
        reset();
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        reset();
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        reset();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        reset();
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        reset();
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        reset();
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitLocalVariable(
            String name, String desc, String signature, Label start, Label end, int index) {
        reset();
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        reset();
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMethodInsn(
            int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (lastWasALoad0 && opcode == Opcodes.INVOKESPECIAL) {
            lastWasALoad0 = false;
            callRemoved = true;
        } else {
            reset();
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        reset();
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        reset();
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        reset();
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        reset();
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        reset();
        if (!callRemoved && opcode == Opcodes.ALOAD && var == 0) {
            lastWasALoad0 = true;
        } else {
            super.visitVarInsn(opcode, var);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(
            String name,
            String descriptor,
            Handle bootstrapMethodHandle,
            Object... bootstrapMethodArguments) {
        reset();
        super.visitInvokeDynamicInsn(
                name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }
}
