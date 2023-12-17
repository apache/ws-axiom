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
package org.apache.axiom.util.xml;

import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class NSUtilsTest {
    /**
     * Test that the generated prefixes are unique for a sample of namespace URIs collected from the
     * Axiom source tree.
     *
     * @throws Exception
     */
    @Test
    public void testUniqueness() throws Exception {
        Set<String> prefixes = new HashSet<String>();
        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                NSUtilsTest.class.getResourceAsStream("namespaces.txt"),
                                StandardCharsets.UTF_8));
        try {
            String uri;
            while ((uri = in.readLine()) != null) {
                assertThat(prefixes.add(NSUtils.generatePrefix(uri))).isTrue();
            }
        } finally {
            in.close();
        }
    }
}
