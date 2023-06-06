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

package org.apache.axiom.testutils.concurrent;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentTestUtils {

    public static void testThreadSafety(Action action) throws Throwable {
        int threadCount = 10;
        List<Throwable> results = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(
                            () -> {
                                Throwable result;
                                try {
                                    for (int j = 0; j < 1000; j++) {
                                        action.execute();
                                    }
                                    result = null;
                                } catch (Throwable ex) {
                                    result = ex;
                                }
                                synchronized (results) {
                                    results.add(result);
                                    results.notifyAll();
                                }
                            })
                    .start();
        }
        synchronized (results) {
            while (results.size() < threadCount) {
                results.wait();
            }
        }
        for (Throwable result : results) {
            if (result != null) {
                throw result;
            }
        }
    }
}
