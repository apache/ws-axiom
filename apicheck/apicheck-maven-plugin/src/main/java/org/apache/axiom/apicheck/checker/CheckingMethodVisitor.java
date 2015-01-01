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
package org.apache.axiom.apicheck.checker;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class CheckingMethodVisitor extends MethodVisitor implements MethodElement {
    private final APIChecker checker;
    private final String owner;
    private final String name;
    
    CheckingMethodVisitor(APIChecker checker, String owner, String name) {
        super(Opcodes.ASM5);
        this.checker = checker;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public String getDescription() {
        return "Method " + owner + "#" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        checker.checkClassUsage(this, "uses", owner);
    }
}
