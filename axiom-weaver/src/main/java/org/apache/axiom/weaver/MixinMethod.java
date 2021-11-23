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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.veithen.jrel.association.MutableReference;

final class MixinMethod {
    private final MutableReference<Mixin> mixin = Relations.MIXIN_METHODS.getConverse().newReferenceHolder(this);
    private final int access;
    private final String name;
    private final String descriptor;
    private final String signature;
    private final String[] exceptions;
    private final MethodBody body;

    MixinMethod(int access, String name, String descriptor, String signature, String[] exceptions, MethodBody body) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
        this.body = body;
    }

    Mixin getMixin() {
        return mixin.get();
    }

    String getSignature() {
        return name + descriptor;
    }

    MethodBody getBody() {
        return body;
    }

    void apply(String targetClassName, ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            body.apply(targetClassName, mv);
        }
    }
}
