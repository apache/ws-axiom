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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public abstract class MethodBody {
    public abstract void apply(TargetContext context, MethodVisitor mv);

    public final int getWeight() {
        Counter counter = new Counter();
        apply(DummyTargetContext.INSTANCE, new WeighingMethodVisitor(counter));
        return counter.get();
    }

    public final String toString(TargetContext targetContext) {
        Textifier textifier = new Textifier();
        apply(targetContext, new LineNumberFilter(new TraceMethodVisitor(textifier)));
        StringWriter sw = new StringWriter();
        textifier.print(new PrintWriter(sw));
        return sw.toString();
    }
}
