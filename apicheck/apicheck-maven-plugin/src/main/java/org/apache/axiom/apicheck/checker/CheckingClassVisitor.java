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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class CheckingClassVisitor extends ClassVisitor implements ClassElement {
    private final APIChecker checker;
    private String name;
    
    CheckingClassVisitor(APIChecker checker) {
        super(Opcodes.ASM5);
        this.checker = checker;
    }
    
    @Override
    public String getDescription() {
        return "Class " + name;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        this.name = name;
        checker.checkClassUsage(this, "extends", superName);
        for (String iface : interfaces) {
            checker.checkClassUsage(this, "implements", iface);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
            String[] exceptions) {
        CheckingMethodVisitor visitor = new CheckingMethodVisitor(checker, this.name, name);
        Type type = Type.getType(desc);
        checker.checkTypeUsage(visitor, "returns", type.getReturnType());
        for (Type argumentType : type.getArgumentTypes()) {
            checker.checkTypeUsage(visitor, "has argument of", argumentType);
        }
        return visitor;
    }
}
