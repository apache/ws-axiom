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
package org.apache.axiom.ts.dom;

import java.net.URL;

import org.w3c.domts.DOMTest;
import org.w3c.domts.DOMTestLoadException;

/**
 * Aspect that modifies the code in the W3C DOM test suite to load test files from the right
 * location.
 */
public privileged aspect ResolveURI {
    URL around(DOMTest test, String baseURI) throws DOMTestLoadException: execution(URL DOMTest.resolveURI(String)) && this(test) && args(baseURI) {
        String docURI = test.factory.addExtension(baseURI);
        Class<?> testClass = test.getClass();
        String resourceName = "/" + testClass.getPackage().getName().replace('.', '/') + "/" + docURI;
        URL url = testClass.getResource(resourceName);
        if (url == null) {
            throw new DOMTestLoadException("Resource " + resourceName + " not found");
        }
        return url;
    }
}
