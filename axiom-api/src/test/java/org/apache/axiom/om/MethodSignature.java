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
package org.apache.axiom.om;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodSignature {
    // Note: Although the JVM considers the return type as part of the signature,
    //       the Java compiler does not.
    private final String name;
    private final Class<?>[] parameterTypes;

    public MethodSignature(String name, Class<?>[] parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public MethodSignature(Method method) {
        name = method.getName();
        parameterTypes = method.getParameterTypes();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodSignature other) {
            return other.name.equals(name) && Arrays.equals(other.parameterTypes, parameterTypes);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = name.hashCode();
        for (int i = 0; i < parameterTypes.length; i++) {
            hashCode = 31 * hashCode + parameterTypes[i].hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        buffer.append('(');
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(parameterTypes[i].getName());
        }
        buffer.append(')');
        return buffer.toString();
    }
}
