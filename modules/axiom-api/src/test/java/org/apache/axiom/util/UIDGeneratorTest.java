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

package org.apache.axiom.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class UIDGeneratorTest extends TestCase {
    public void testGenerateContentIdFormat() {
        // This is actually a bit more restrictive than necessary
        assertTrue(Pattern.matches("\\w+(\\.\\w+)*@\\w+(\\.\\w+)", UIDGenerator.generateContentId()));
    }
    
    public void testGenerateContentIdUniqueness() {
        // Not very sophisticated, but should catch stupid regressions
        Set values = new HashSet();
        for (int i=0; i<1000; i++) {
            assertTrue(values.add(UIDGenerator.generateContentId()));
        }
    }
}
