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
package org.apache.axiom.buildutils.enforcer;

abstract class PackageMatcher {
    final static PackageMatcher ANY = new PackageMatcher() {
        @Override
        boolean matches(Package pkg) {
            return true;
        }
    };

    abstract boolean matches(Package pkg);

    final static PackageMatcher from(final String pkg) {
        return new PackageMatcher() {
            @Override
            boolean matches(Package otherPackage) {
                String name = otherPackage.getName();
                if (name.length() < pkg.length()) {
                    return false;
                } else if (name.length() == pkg.length()) {
                    return name.equals(pkg);
                } else {
                    return name.startsWith(pkg) && name.charAt(pkg.length()) == '.';
                }
            }
        };
    }

    final static PackageMatcher from(String[] packages) {
        if (packages == null) {
            return ANY;
        } else if (packages.length == 1) {
            return from(packages[0]);
        } else {
            final PackageMatcher[] matchers = new PackageMatcher[packages.length];
            for (int i=0; i<packages.length; i++) {
                matchers[i] = from(packages[i]);
            }
            return new PackageMatcher() {
                @Override
                boolean matches(Package pkg) {
                    for (PackageMatcher matcher : matchers) {
                        if (matcher.matches(pkg)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
    }
}
