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
package org.apache.axiom.weaver.mixin;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.FieldNode;

import com.github.veithen.jrel.association.MutableReferences;

public final class Mixin {
    private final int bytecodeVersion;
    private final String name;
    private final Class<?> targetInterface;
    private final List<FieldNode> fields;
    private final InitializerMethod initializerMethod;
    private final StaticInitializerMethod staticInitializerMethod;
    private final MutableReferences<MixinMethod> methods =
            Relations.MIXIN_METHODS.newReferenceHolder(this);
    private final int weight;
    private final List<MixinInnerClass> innerClasses;

    public Mixin(
            int bytecodeVersion,
            String name,
            Class<?> targetInterface,
            List<FieldNode> fields,
            InitializerMethod initializerMethod,
            StaticInitializerMethod staticInitializerMethod,
            List<MixinMethod> methods,
            List<MixinInnerClass> innerClasses) {
        this.bytecodeVersion = bytecodeVersion;
        this.name = name;
        this.targetInterface = targetInterface;
        this.fields = fields;
        this.initializerMethod = initializerMethod;
        this.staticInitializerMethod = staticInitializerMethod;
        this.methods.addAll(methods);
        this.innerClasses = innerClasses;
        int weight = 0;
        if (initializerMethod != null) {
            weight += initializerMethod.getBody().getWeight();
        }
        if (staticInitializerMethod != null) {
            weight += staticInitializerMethod.getBody().getWeight();
        }
        for (MixinMethod method : methods) {
            weight += method.getBody().getWeight();
        }
        for (MixinInnerClass innerClass : innerClasses) {
            Counter counter = new Counter();
            innerClass.createClassDefinition("Dummy").accept(new WeighingClassVisitor(counter));
            weight += counter.get();
        }
        this.weight = weight;
    }

    public int getBytecodeVersion() {
        return bytecodeVersion;
    }

    public String getName() {
        return name;
    }

    public Class<?> getTargetInterface() {
        return targetInterface;
    }

    public int getWeight() {
        return weight;
    }

    public MutableReferences<MixinMethod> getMethods() {
        return methods;
    }

    public boolean appliesAfter(Mixin other) {
        return other.targetInterface.isAssignableFrom(targetInterface);
    }

    public void apply(String targetClassName, ClassVisitor cv) {
        for (FieldNode field : fields) {
            field.accept(cv);
        }
    }

    public InitializerMethod getInitializerMethod() {
        return initializerMethod;
    }

    public StaticInitializerMethod getStaticInitializerMethod() {
        return staticInitializerMethod;
    }

    public List<ClassDefinition> createInnerClassDefinitions(String targetClassName) {
        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (MixinInnerClass innerClass : innerClasses) {
            classDefinitions.add(innerClass.createClassDefinition(targetClassName));
        }
        return classDefinitions;
    }
}
