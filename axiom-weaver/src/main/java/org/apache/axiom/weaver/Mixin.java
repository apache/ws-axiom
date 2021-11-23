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
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.FieldNode;

import com.github.veithen.jrel.association.MutableReferences;

final class Mixin {
    private final int bytecodeVersion;
    private final String name;
    private final Class<?> targetInterface;
    private final Set<Class<?>> addedInterfaces;
    private final List<FieldNode> fields;
    private final InitializerMethod initializerMethod;
    private final StaticInitializerMethod staticInitializerMethod;
    private final MutableReferences<MixinMethod> methods = Relations.MIXIN_METHODS.newReferenceHolder(this);
    private final int weight;
    private final List<MixinInnerClass> innerClasses;

    Mixin(int bytecodeVersion, String name, Class<?> targetInterface, Set<Class<?>> addedInterfaces, List<FieldNode> fields, InitializerMethod initializerMethod, StaticInitializerMethod staticInitializerMethod, List<MixinMethod> methods, int weight, List<MixinInnerClass> innerClasses) {
        this.bytecodeVersion = bytecodeVersion;
        this.name = name;
        this.targetInterface = targetInterface;
        this.addedInterfaces = addedInterfaces;
        this.fields = fields;
        this.initializerMethod = initializerMethod;
        this.staticInitializerMethod = staticInitializerMethod;
        this.methods.addAll(methods);
        this.weight = weight;
        this.innerClasses = innerClasses;
    }

    int getBytecodeVersion() {
        return bytecodeVersion;
    }

    String getName() {
        return name;
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

    void apply(String targetClassName, ClassVisitor cv) {
        for (FieldNode field : fields) {
            field.accept(cv);
        }
    }

    InitializerMethod getInitializerMethod() {
        return initializerMethod;
    }

    StaticInitializerMethod getStaticInitializerMethod() {
        return staticInitializerMethod;
    }

    List<ClassDefinition> createInnerClassDefinitions(String targetClassName) {
        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (MixinInnerClass innerClass : innerClasses) {
            classDefinitions.add(innerClass.createClassDefinition(targetClassName));
        }
        return classDefinitions;
    }
}
