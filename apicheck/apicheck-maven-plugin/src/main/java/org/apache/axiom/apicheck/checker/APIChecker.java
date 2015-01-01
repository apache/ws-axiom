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
package org.apache.axiom.apicheck.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

public final class APIChecker {
    private final Map<String,String> classSignatures = new HashMap<String,String>();
    private final ViolationListener listener;
    
    public APIChecker(ViolationListener listener) {
        this.listener = listener;
    }
    
    public void loadSignatures(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            int idx = line.indexOf(' ');
            classSignatures.put(line.substring(0, idx), line.substring(idx+1));
        }
    }
    
    private void reportViolation(Element element, String verb, String object, String msg) {
        listener.log(element, element.getDescription() + " " + verb + " forbidden " + object + ": " + msg);
    }
    
    void checkClassUsage(Element element, String verb, String className) {
        String msg = classSignatures.get(className);
        if (msg != null) {
            reportViolation(element, verb, "class/interface " + className, msg);
        }
    }
    
    void checkTypeUsage(Element element, String verb, Type type) {
        switch (type.getSort()) {
            case Type.OBJECT:
                checkClassUsage(element, verb, type.getInternalName());
                break;
            case Type.ARRAY:
                checkTypeUsage(element, verb, type.getElementType());
                break;
        }
    }
    
    public void checkClass(InputStream in) throws IOException {
        new ClassReader(in).accept(new CheckingClassVisitor(this),
                ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }
}
