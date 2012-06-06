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
package org.apache.axiom.testutils.conformance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class Conformance {
    private Conformance() {}

    public static String[] getConformanceTestFiles() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    Conformance.class.getResourceAsStream("filelist")));
            String line;
            List result = new ArrayList(10);
            while ((line = in.readLine()) != null) {
                result.add("org/apache/axiom/testutils/conformance/" + line);
            }
            in.close();
            return (String[])result.toArray(new String[result.size()]);
        } catch (IOException ex) {
            throw new Error("Unable to load file list: " + ex.getMessage());
        }
    }
}
