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
import java.util.function.Function;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

final class MixinClassVisitor extends ClassVisitor {
    private final ClassFetcher classFetcher;
    private int bytecodeVersion;
    private String className;
    private Function<String, Remapper> remapperFactory;
    private Class<?> targetInterface;
    private final Set<Class<?>> addedInterfaces = new HashSet<>();
    private final List<FieldNode> fields = new ArrayList<>();
    private final List<MixinMethod> methods = new ArrayList<>();
    private final List<String> innerClassNames = new ArrayList<>();
    private InitializerMethod initializerMethod;
    private StaticInitializerMethod staticInitializerMethod;

    MixinClassVisitor(ClassFetcher classFetcher) {
        super(Opcodes.ASM9);
        this.classFetcher = classFetcher;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        bytecodeVersion = version;
        className = name;
        remapperFactory = (targetClassName) -> new Remapper() {
            @Override
            public String map(String internalName) {
                if (internalName.equals(name)) {
                    return targetClassName;
                }
                if (internalName.length() > name.length() && internalName.startsWith(name) && internalName.charAt(name.length()) == '$') {
                    return targetClassName + internalName.substring(name.length());
                }
                return internalName;
            }
        };
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
        MethodNode method = new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions);
        Function<String, Remapper> remapperFactory = this.remapperFactory;
        MethodBody body = new MethodBody() {
            @Override
            void apply(String targetClassName, MethodVisitor mv) {
                method.accept(new MethodRemapper(mv, remapperFactory.apply(targetClassName)));
            }
        };
        if (name.equals("<init>")) {
            if (!descriptor.equals("()V")) {
                throw new WeaverException("Expected only a default constructor");
            }
            initializerMethod = new InitializerMethod(body);
            return new ConstructorToMethodConverter(method);
        } else if (name.equals("<clinit>")) {
            staticInitializerMethod = new StaticInitializerMethod(body);
            return method;
        } else {
            methods.add(new MixinMethod(access, name, descriptor, signature, exceptions, body));
            return method;
        }
    }

    Mixin getMixin() {
        List<MixinInnerClass> innerClasses = new ArrayList<>();
        Function<String, Remapper> remapperFactory = this.remapperFactory;
        for (String innerClassName : innerClassNames) {
            ClassNode innerClass = new ClassNode();
            classFetcher.fetch(innerClassName, innerClass);
            innerClasses.add(new MixinInnerClass() {
                @Override
                ClassDefinition createClassDefinition(String targetClassName) {
                    Remapper remapper = remapperFactory.apply(targetClassName);
                    return new ClassDefinition(remapper.map(innerClass.name)) {
                        @Override
                        void accept(ClassVisitor cv) {
                            innerClass.accept(new ClassRemapper(cv, remapper));
                        }
                    };
                }
            });
        }
        return new Mixin(bytecodeVersion, className.substring(className.lastIndexOf('/')+1), targetInterface, addedInterfaces, fields, initializerMethod, staticInitializerMethod, methods, innerClasses);
    }
}
