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

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

final class ClassFetcher {
    private final ClassLoader classLoader;

    ClassFetcher(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    Class<?> loadClass(String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException ex) {
            throw new WeaverException("Failed to load class " + name, ex);
        }
    }

    void fetch(String className, ClassVisitor classVisitor) {
        try (InputStream in = classLoader.getResourceAsStream(className.replace('.', '/') + ".class")) {
            if (in == null) {
                throw new WeaverException("Class " + className + " not found");
            }
            new ClassReader(in).accept(classVisitor, 0);
        } catch (IOException ex) {
            throw new WeaverException("Failed to load class " + className, ex);
        }
    }
}
