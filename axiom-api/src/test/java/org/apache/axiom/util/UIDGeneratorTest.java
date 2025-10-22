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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class UIDGeneratorTest extends TestCase {
    public void testGenerateContentIdFormat() {
        // This is actually a bit more restrictive than necessary
        assertTrue(
                Pattern.matches("\\w+(\\.\\w+)*@\\w+(\\.\\w+)", UIDGenerator.generateContentId()));
    }

    public void testGenerateContentIdUniqueness() {
        // Not very sophisticated, but should catch stupid regressions
        Set<String> values = new HashSet<String>();
        for (int i = 0; i < 1000; i++) {
            assertTrue(values.add(UIDGenerator.generateContentId()));
        }
    }

    public void testGenerateMimeBoundaryLength() {
        assertTrue(UIDGenerator.generateMimeBoundary().length() <= 70);
    }

    public void testGenerateUIDThreadSafety() {
        Set<String> generatedIds = Collections.synchronizedSet(new HashSet<String>());
        AtomicInteger errorCount = new AtomicInteger(0);
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] =
                    new Thread(
                            () -> {
                                for (int j = 0; j < 1000; j++) {
                                    String id = UIDGenerator.generateUID();
                                    if (!generatedIds.add(id)) {
                                        System.out.println(
                                                "ERROR - Same UID has been generated before. UID: "
                                                        + id);
                                        errorCount.incrementAndGet();
                                    }
                                }
                            });
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(0, errorCount.get());
    }

    public void testGenerateURNString() {
        Thread[] threads = new Thread[100];
        String[][] urns = new String[threads.length][1000];
        for (int i = 0; i < threads.length; i++) {
            String[] threadURNs = urns[i];
            threads[i] =
                    new Thread(
                            () -> {
                                for (int j = 0; j < threadURNs.length; j++) {
                                    threadURNs[j] = UIDGenerator.generateURNString();
                                }
                            });
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Set<String> set = new HashSet<String>();
        for (int i = 0; i < threads.length; i++) {
            for (int j = 0; j < urns[i].length; j++) {
                String urn = urns[i][j];
                assertTrue(urn.startsWith("urn:uuid:"));
                assertTrue(set.add(urn));
                UUID uuid = UUID.fromString(urn.substring(9));
                assertEquals(4, uuid.version());
                assertEquals(2, uuid.variant());
            }
        }
    }
}
