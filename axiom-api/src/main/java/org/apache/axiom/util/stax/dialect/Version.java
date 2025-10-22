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
package org.apache.axiom.util.stax.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Version {
    private static final Pattern pattern = Pattern.compile("([0-9]+(\\.[0-9]+)*)([\\.-].*)?");

    private final int[] components;

    Version(String versionString) {
        Matcher matcher = pattern.matcher(versionString);
        if (matcher.matches()) {
            String[] componentStrings = matcher.group(1).split("\\.");
            int l = componentStrings.length;
            components = new int[l];
            for (int i = 0; i < l; i++) {
                components[i] = Integer.parseInt(componentStrings[i]);
            }
        } else {
            components = new int[0];
        }
    }

    int getComponent(int idx) {
        return idx < components.length ? components[idx] : 0;
    }
}
