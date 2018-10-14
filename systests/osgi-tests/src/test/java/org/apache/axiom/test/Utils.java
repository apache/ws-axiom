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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.felix.framework.cache.BundleCache;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.options.FrameworkPropertyOption;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

public class Utils {
    private static String[] defaultSystemPackages;
    
    private synchronized static String[] getDefaultSystemPackages() {
        if (defaultSystemPackages == null) {
            Map<String,String> configuration = new HashMap<>();
            configuration.put(BundleCache.CACHE_ROOTDIR_PROP, "target");
            Framework framework = ServiceLoader.load(FrameworkFactory.class).iterator().next().newFramework(configuration);
            try {
                framework.start();
                try {
                    defaultSystemPackages = framework.getBundleContext().getProperty(Constants.FRAMEWORK_SYSTEMPACKAGES).split("\\s*,\\s*");
                } finally {
                    framework.stop();
                }
            } catch (BundleException ex) {
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
        return CoreOptions.frameworkProperty(Constants.FRAMEWORK_SYSTEMPACKAGES).value(String.join(",", systemPackages));
    }
}
