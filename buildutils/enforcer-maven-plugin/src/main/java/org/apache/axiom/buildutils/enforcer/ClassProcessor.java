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
package org.apache.axiom.buildutils.enforcer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class ClassProcessor extends ClassVisitor {
    private final ReferenceCollector referenceCollector;
    private boolean deprecated;
    private ReferenceProcessor referenceProcessor;
    
    ClassProcessor(ReferenceCollector referenceCollector) {
        super(Opcodes.ASM5);
        this.referenceCollector = referenceCollector;
    }

    private static boolean isPublic(int access) {
        return (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        deprecated = (access & Opcodes.ACC_DEPRECATED) != 0;
        if (!deprecated) {
            referenceProcessor = new ReferenceProcessor(referenceCollector, new Clazz(Type.getObjectType(name).getClassName()), isPublic(access));
            referenceProcessor.processType(Type.getObjectType(superName), true);
            for (String iface : interfaces) {
                referenceProcessor.processType(Type.getObjectType(iface), true);
            }
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
            Object value) {
        if (!deprecated) {
            referenceProcessor.processType(Type.getType(desc), isPublic(access));
        }
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
            String[] exceptions) {
        if (!deprecated && (access & Opcodes.ACC_DEPRECATED) == 0) {
            boolean isPublic = isPublic(access);
            referenceProcessor.processType(Type.getMethodType(desc), isPublic);
            if (exceptions != null) {
                for (String exception : exceptions) {
                    referenceProcessor.processType(Type.getObjectType(exception), isPublic);
                }
            }
            return new MethodProcessor(referenceProcessor);
        } else {
            return null;
        }
    }
}
