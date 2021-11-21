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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.veithen.jrel.association.MutableReferences;

final class Mixin {
    private final int bytecodeVersion;
    private final String className;
    private final Class<?> targetInterface;
    private final Set<Class<?>> addedInterfaces;
    private final List<FieldNode> fields;
    private final MethodNode initMethod;
    private final MethodNode clinitMethod;
    private final MutableReferences<MixinMethod> methods = Relations.MIXIN_METHODS.newReferenceHolder(this);
    private final int weight;
    private final List<ClassNode> innerClasses;

    Mixin(int bytecodeVersion, String className, Class<?> targetInterface, Set<Class<?>> addedInterfaces, List<FieldNode> fields, MethodNode initMethod, MethodNode clinitMethod, List<MixinMethod> methods, int weight, List<ClassNode> innerClasses) {
        this.bytecodeVersion = bytecodeVersion;
        this.className = className;
        this.targetInterface = targetInterface;
        this.addedInterfaces = addedInterfaces;
        this.fields = fields;
        this.initMethod = initMethod;
        this.clinitMethod = clinitMethod;
        this.methods.addAll(methods);
        this.weight = weight;
        this.innerClasses = innerClasses;
    }

    String getClassName() {
        return className;
    }

    int getBytecodeVersion() {
        return bytecodeVersion;
    }

    String getSimpleName() {
        return className.substring(className.lastIndexOf('/')+1);
    }

    Class<?> getTargetInterface() {
        return targetInterface;
    }

    Set<Class<?>> getAddedInterfaces() {
        return addedInterfaces;
    }

    boolean contributesCode() {
        // TODO: also attributes
        return !methods.isEmpty();
    }

    int getWeight() {
        return weight;
    }

    MutableReferences<MixinMethod> getMethods() {
        return methods;
    }

    boolean appliesAfter(Mixin other) {
        return other.targetInterface.isAssignableFrom(targetInterface);
    }

    private String getTargetInnerClassName(ClassNode innerClass, String targetClassName) {
        return targetClassName + innerClass.name.substring(innerClass.name.indexOf('$'));
    }

    Remapper createRemapper(String targetClassName) {
        final Map<String, String> map = new HashMap<>();
        map.put(className, targetClassName);
        for (ClassNode innerClass : innerClasses) {
            map.put(innerClass.name, getTargetInnerClassName(innerClass, targetClassName));
        }
        return new Remapper() {
            @Override
            public String map(String internalName) {
                String mappedName = map.get(internalName);
                return mappedName != null ? mappedName : internalName;
            }
        };
    }

    void apply(String targetClassName, ClassVisitor cv) {
        cv = new ClassRemapper(cv, createRemapper(targetClassName));
        for (FieldNode field : fields) {
            field.accept(cv);
        }
        initMethod.accept(cv);
        if (clinitMethod != null) {
            clinitMethod.accept(cv);
        }
    }

    String getInitMethodName() {
        return initMethod.name;
    }

    String getStaticInitializerMethodName() {
        return clinitMethod == null ? null : clinitMethod.name;
    }

    List<ClassDefinition> createInnerClassDefinitions(String targetClassName) {
        final Remapper remapper = createRemapper(targetClassName);
        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (final ClassNode innerClass : innerClasses) {
            classDefinitions.add(new ClassDefinition(getTargetInnerClassName(innerClass, targetClassName)) {
                @Override
                void accept(ClassVisitor cv) {
                    innerClass.accept(new ClassRemapper(cv, remapper));
                }
            });
        }
        return classDefinitions;
    }
}
