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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

final class MixinClassVisitor extends ClassVisitor {
    private final ClassFetcher classFetcher;
    private int bytecodeVersion;
    private String className;
    private Class<?> targetInterface;
    private final Set<Class<?>> addedInterfaces = new HashSet<>();
    private final List<FieldNode> fields = new ArrayList<>();
    private final List<MixinMethod> methods = new ArrayList<>();
    private int weight;
    private final List<String> innerClassNames = new ArrayList<>();
    private MethodNode initMethod;
    private MethodNode clinitMethod;

    MixinClassVisitor(ClassFetcher classFetcher) {
        super(Opcodes.ASM9);
        this.classFetcher = classFetcher;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        bytecodeVersion = version;
        className = name;
        for (String iface : interfaces) {
            addedInterfaces.add(classFetcher.loadClass(iface.replace('/', '.')));
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals("Lorg/apache/axiom/weaver/annotation/Mixin;")) {
            return new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public void visit(String name, Object value) {
                    if (name.equals("value")) {
                        String ifaceName = ((Type)value).getClassName();
                        targetInterface = classFetcher.loadClass(ifaceName);
                        if (!targetInterface.isInterface()) {
                            throw new WeaverException(ifaceName + " is not an interface");
                        }
                    }
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        innerClassNames.add(name);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature,
            Object value) {
        FieldNode field = new FieldNode(Opcodes.ASM9, access, name, descriptor, signature, value);
        fields.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        if (name.equals("<init>")) {
            if (!descriptor.equals("()V")) {
                throw new WeaverException("Expected only a default constructor");
            }
            initMethod = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PRIVATE, "init$" + className.replace('/', '_'), descriptor, signature, exceptions);
            return new ConstructorToMethodConverter(initMethod);
        } else if (name.equals("<clinit>")) {
            clinitMethod = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "clinit$" + className.replace('/', '_'), descriptor, signature, exceptions);
            return clinitMethod;
        } else {
            MethodNode method = new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions);
            methods.add(new MixinMethod(method));
            return new MethodVisitor(Opcodes.ASM9, method) {
                @Override
                public void visitLineNumber(int line, Label start) {
                    super.visitLineNumber(line, start);
                    weight++;
                }
            };
        }
    }

    Mixin getMixin() {
        List<ClassNode> innerClasses = new ArrayList<>();
        for (String innerClassName : innerClassNames) {
            ClassNode innerClass = new ClassNode();
            classFetcher.fetch(innerClassName, innerClass);
            innerClasses.add(innerClass);
        }
        // TODO: include inner classes in the weight
        return new Mixin(bytecodeVersion, className, targetInterface, addedInterfaces, fields, initMethod, clinitMethod, methods, weight, innerClasses);
    }
}
