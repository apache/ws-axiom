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
package org.apache.axiom.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.nat.internal.NativeTestContainer;
import org.ops4j.pax.exam.options.FrameworkPropertyOption;
import org.ops4j.pax.exam.spi.DefaultExamSystem;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.FrameworkFactory;

public class Utils {
    private static String[] defaultSystemPackages;

    private static synchronized String[] getDefaultSystemPackages() {
        if (defaultSystemPackages == null) {
            try {
                NativeTestContainer testContainer =
                        new NativeTestContainer(
                                DefaultExamSystem.create(null),
                                ServiceLoader.load(FrameworkFactory.class).iterator().next());
                testContainer.start();
                try {
                    defaultSystemPackages =
                            testContainer
                                    .getSystemBundle()
                                    .getBundleContext()
                                    .getProperty(Constants.FRAMEWORK_SYSTEMPACKAGES)
                                    .split("\\s*,\\s*");
                } finally {
                    testContainer.stop();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return defaultSystemPackages;
    }

    public static FrameworkPropertyOption filteredSystemPackages(String... excludedPackage) {
        List<String> systemPackages = new ArrayList<>();
        Set<String> excludedPackageSet = new HashSet<>(Arrays.asList(excludedPackage));
        for (String systemPackage : getDefaultSystemPackages()) {
            if (!excludedPackageSet.contains(systemPackage.split(";")[0])) {
                systemPackages.add(systemPackage);
            }
        }
        return CoreOptions.frameworkProperty(Constants.FRAMEWORK_SYSTEMPACKAGES)
                .value(String.join(",", systemPackages));
    }
}
